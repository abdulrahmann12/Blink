
## Issue Summary Table

| ID | Severity | Module | File | Line | Status |
|:---|:---------|:-------|:-----|:-----|:-------|


| PR-033 | **Medium** | URL Shortener | `UrlService.java` | 57-61 | ⚠️ |





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



