
## Issue Summary Table

| ID | Severity | Module | File | Line | Status |
|:---|:---------|:-------|:-----|:-----|:-------|


| PR-007 | **Critical** | URL Shortener | `UrlService.java` | 143-155 | ❌ |
| PR-008 | **Critical** | Rate Limiting | `RateLimitService.java` | 13 | ❌ |
| PR-017 | **High** | Email | `UserRegisteredConsumer.java` | 22 | ⚠️ |
| PR-033 | **Medium** | URL Shortener | `UrlService.java` | 57-61 | ⚠️ |
| PR-040 | **Medium** | Configuration | `RabbitCommonConfig.java` | all | ⚠️ |



### PR-007 · Critical · URL Shortener — Server-Side Request Forgery (SSRF)

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Method: checkUrl()⃁
Lines: 143-155
File: src/main/java/com/example/Blink/security/SecurityConfig.java
Line: 64
```

**Problem:** The `checkUrl()` method opens an HTTP connection to any URL provided by the user with no allowlist validation:
```java
URL url = URI.create(rawUrl).toURL();
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("HEAD");
connection.setInstanceFollowRedirects(true);
```

This is a classic SSRF vulnerability. An attacker can:
1. Probe internal network: `http://192.168.1.1/admin`, `http://10.0.0.1/`
2. Access cloud metadata: `http://169.254.169.254/latest/meta-data/` (AWS IMDSv1 — returns IAM credentials)
3. Scan internal ports
4. Hit internal services behind the firewall

This endpoint is **publicly accessible** — no authentication required (SecurityConfig line 64: `permitAll()`).

**Why it passes in development:** Developer only tests with public URLs; internal network not reachable from dev machine.

**Impact:** Cloud credential exfiltration (IAM role via IMDS), internal service port scanning, privilege escalation. This is one of the highest-impact vulnerabilities in the codebase.

**Recommendation:** (1) Allowlist only `http://` and `https://` schemes. (2) Resolve hostname; block RFC-1918 ranges (10.x, 172.16-31.x, 192.168.x), loopback (127.x, ::1), link-local (169.254.x), cloud metadata IPs. (3) Consider removing or restricting to authenticated users.

**Evidence:**
```java
// UrlService.java lines 144-154 — no IP or scheme validation
URL url = URI.create(rawUrl).toURL();
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setInstanceFollowRedirects(true);  // Follows redirects — bypasses naive host checks
return connection.getResponseCode() < 400;

// SecurityConfig.java line 64 — publicly accessible
.requestMatchers("/api/v1/urls/check").permitAll()
```

---

### PR-008 · Critical · Rate Limiting — In-Memory Buckets Cause OOM and Multi-Instance Bypass

**Location:**
```
File: src/main/java/com/example/Blink/rate_limit/RateLimitService.java
Line: 13
```

**Problem:** Rate limiting state is stored in a JVM-level `ConcurrentHashMap`:
```java
private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
```

Three production-critical failures:

1. **Memory leak / OOM:** Buckets are created per `key + "_" + ruleName` and **never evicted**. Under a DDoS attack with spoofed IPs, the map grows until OOM, crashing the application.

2. **State reset on restart:** Every restart clears all rate limit state. An attacker can trigger a restart (e.g., via OOM above) and get a full quota reset.

3. **Multi-instance bypass:** With horizontal scaling, each instance has its own independent map. A single attacker gets `N × limit` requests across N instances, completely defeating rate limiting.

**Impact:** Brute-force attacks succeed (the exact thing rate limiting prevents), OOM crash, rate limits silently bypassed in scaled deployments.

**Recommendation:** Replace with Redis-backed Bucket4j distributed rate limiter. Use TTL-expiring keys to prevent memory leak.

**Evidence:**
```java
// RateLimitService.java line 13 — unbounded, non-distributed, non-persistent
private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
```


```


### PR-017 · High · Email / RabbitMQ — No Dead-Letter Queue; Emails Silently Dropped

**Location:**
```
File: src/main/java/com/example/Blink/common/consumer/UserRegisteredConsumer.java
Line: 22
File: src/main/java/com/example/Blink/config/rabbitconfig/AuthRabbitConfig.java
All queue declarations
```

**Problem:** All RabbitMQ queues are declared without Dead-Letter Exchange (DLX):
```java
return new Queue(RabbitConstants.USER_REGISTERED_QUEUE); // No DLX config
```

`spring.rabbitmq.listener.simple.default-requeue-rejected=false` means after 3 failed retries, messages are **nacked and discarded** — never moved to a DLQ. Emails are permanently lost with no alerting or recovery.

Additionally, `UserVerifiedConsumer.java` line 22: `throw new MailSendingException()` (no-arg) loses the original exception's details.

**Impact:** Users never receive verification/reset emails. Silent data loss. No operational visibility.

**Recommendation:** Configure DLX and DLQ for all queues. Implement DLQ consumer with alerting. Fix `throw new MailSendingException()` → `throw new MailSendingException(e)`.

---
### PR-033 · Medium · URL Shortener — URI.create() Accepts javascript: and data: Schemes

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Method: generateShortUrl()
Lines: 57-61
```

**Problem:**
```java
try {
    URI.create(request.getOriginalUrl());
} catch (Exception e) {
    throw new InvalidUrlException();
}
```

`URI.create("javascript:alert(1)")` does NOT throw an exception — it creates a valid URI. The `@URL` Hibernate annotation on `CreateUrlRequest.originalUrl` is the actual guard (it rejects non-http/https). However, the duplicate `URI.create()` check creates false confidence and adds no security value.

**Recommendation:** Remove the redundant `URI.create()` check. Add explicit scheme validation after the `@URL` annotation: reject any scheme other than `http` and `https`.

---


### PR-040 · Medium · Configuration — RabbitMQ DLQ Not Configured for Any Queue

**Location:**
```
File: src/main/java/com/example/Blink/config/rabbitconfig/AuthRabbitConfig.java
```

**Problem:** All queue declarations omit Dead-Letter Exchange arguments. Combined with `default-requeue-rejected=false`, messages failing all 3 retries are permanently discarded. No DLQ consumer, no alerting, no audit trail.

**Recommendation:**
```java
return QueueBuilder.durable(RabbitConstants.USER_REGISTERED_QUEUE)
    .withArgument("x-dead-letter-exchange", "auth.dlx")
    .withArgument("x-dead-letter-routing-key", "user.registered.dead")
    .build();
```
Create DLX and DLQs. Implement DLQ consumer with alerts.

