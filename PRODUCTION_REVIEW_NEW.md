# PRODUCTION READINESS AUDIT — Blink URL Shortener

> **Auditor:** Senior Staff Engineer · **Date:** 2026-07-04 · **Application:** Blink (Spring Boot 4.0.6, Java 21)
> **Audit Scope:** Full codebase — every file, every execution path, every configuration value

---

## Issue Summary Table

| ID | Severity | Module | File | Line | Status |
|:---|:---------|:-------|:-----|:-----|:-------|


| PR-007 | **Critical** | URL Shortener | `UrlService.java` | 143-155 | ❌ |
| PR-008 | **Critical** | Rate Limiting | `RateLimitService.java` | 13 | ❌ |
| PR-011 | **High** | Security | `SecurityConfig.java` | 49 | ⚠️ |
| PR-012 | **High** | Database | `AuthService.java` | 58-62 | ⚠️ |
| PR-013 | **High** | Authentication | `AuthService.java` | 103 | ⚠️ |
| PR-014 | **High** | URL Shortener | `UrlService.java` | 88-100 | ⚠️ |
| PR-015 | **High** | Exception Handling | `GlobalExceptionHandler.java` | 153-161 | ⚠️ |
| PR-016 | **High** | Cache | `CacheConfig.java` | 30 | ⚠️ |
| PR-017 | **High** | Email | `UserRegisteredConsumer.java` | 22 | ⚠️ |
| PR-018 | **High** | Database | `UrlService.java` | 188, 228, 259, 300 | ⚠️ |
| PR-019 | **High** | Security | `JwtService.java` | 29-31 | ⚠️ |
| PR-020 | **High** | URL Shortener | `UrlService.java` | 63-69 | ⚠️ |
| PR-021 | **High** | Rate Limiting | `RateLimitFilter.java` | 64-66 | ⚠️ |
| PR-022 | **High** | Scheduler | `UrlCleanupService.java` | 22-33 | ⚠️ |
| PR-023 | **High** | Authentication | `AuthService.java` | 144-157 | ⚠️ |
| PR-024 | **High** | Performance | `AuthenticatedUserService.java` | 33 | ⚠️ |
| PR-025 | **High** | Security | `application.properties` | 97-99 | ⚠️ |
| PR-026 | **Medium** | Exception Handling | `GlobalExceptionHandler.java` | 84-87 | ⚠️ |
| PR-027 | **Medium** | Database | `User.java` | 41 | ⚠️ |
| PR-028 | **Medium** | Database | `Url.java` | 30-31 | ⚠️ |
| PR-029 | **Medium** | Configuration | `application.properties` | 36-41 | ⚠️ |
| PR-030 | **Medium** | Rate Limiting | `RateLimitFilter.java` | 114 | ⚠️ |
| PR-031 | **Medium** | Performance | `UrlClickService.java` | 78-106 | ⚠️ |
| PR-032 | **Medium** | Security | `UserController.java` | 41-44 | ⚠️ |
| PR-033 | **Medium** | URL Shortener | `UrlService.java` | 57-61 | ⚠️ |
| PR-034 | **Medium** | Email | `EmailService.java` | 89-91 | ⚠️ |
| PR-035 | **Medium** | Database | `UrlCleanupService.java` | 22-33 | ⚠️ |
| PR-036 | **Medium** | Performance | `UrlService.java` | 111-124 | ⚠️ |
| PR-037 | **Medium** | Configuration | `application.properties` | 76 | ⚠️ |
| PR-038 | **Medium** | Security | `BlockedUrlService.java` | 78-81 | ⚠️ |
| PR-039 | **Medium** | QR Code | `QrCodeService.java` | 35-68 | ⚠️ |
| PR-040 | **Medium** | Configuration | `RabbitCommonConfig.java` | all | ⚠️ |
| PR-041 | **Low** | Code Quality | `BlinkApplication.java` | 13-14 | ℹ️ |
| PR-042 | **Low** | Database | `UrlRepository.java` | 65-74 | ℹ️ |
| PR-043 | **Low** | API Design | `AuthController.java` | 63-66 | ℹ️ |
| PR-044 | **Low** | Code Quality | `CacheConfig.java` | 30 | ℹ️ |
| PR-045 | **Low** | Testing | `BlinkApplicationTests.java` | all | ℹ️ |
| PR-046 | **Suggestion** | Performance | `AsyncConfig.java` | 17-18 | 💡 |
| PR-047 | **Suggestion** | Observability | `application.properties` | 97-99 | 💡 |
| PR-048 | **Suggestion** | Production Readiness | `application.properties` | all | 💡 |
| PR-049 | **Suggestion** | Code Quality | `ImageService.java` | 21-23 | 💡 |
| PR-050 | **Suggestion** | Scheduler | `UrlCleanupScheduler.java` | 14 | 💡 |

---

## Detailed Findings


```


### PR-011 · High · Security — Swagger UI Publicly Exposed in Production

**Location:**
```
File: src/main/java/com/example/Blink/security/SecurityConfig.java
Line: 49
File: src/main/resources/application.properties
Lines: 83-85
```

**Problem:**
```java
.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
```
```properties
springdoc.swagger-ui.enabled=true
```

Swagger UI is fully public in production. Any anonymous user can:
- Discover every endpoint, parameter, and data model
- Understand token format and auth flows
- Execute requests directly against the production API

The `SwaggerConfigConfiguration.java` also leaks the developer's personal email address in the API contact section.

**Recommendation:** Set `springdoc.swagger-ui.enabled=${SWAGGER_ENABLED:false}`. Restrict via Spring profile. Remove personal email from Swagger contact.

---

### PR-012 · High · Database — Race Condition on User Registration (TOCTOU)

**Location:**
```
File: src/main/java/com/example/Blink/auth/service/AuthService.java
Method: createUser()
Lines: 58-62
```

**Problem:** Check-then-act without lock:
```java
if(userRepository.existsByEmail(createUserRequest.getEmail())) { throw ... }
if(userRepository.existsByUsername(createUserRequest.getUsername())) { throw ... }
// time gap — two concurrent requests can both pass here
User savedUser = userRepository.save(user);
```

Two concurrent requests with the same email/username can both pass the existence checks simultaneously. The second `save()` hits the unique constraint and throws `DataIntegrityViolationException`, which is **not handled** in `GlobalExceptionHandler` — surfaces as an unhandled 500.

**Why it fails in production:** Under load or with registration bots, this race is frequently triggered.

**Impact:** Confusing 500 errors for users; unhandled exception propagating to clients.

**Recommendation:** Add `@ExceptionHandler(DataIntegrityViolationException.class)` to `GlobalExceptionHandler` mapping to 409 Conflict.

---

### PR-013 · High · Authentication — Refresh Token Expiry Uses LocalDateTime (Timezone Unsafe)

**Location:**
```
File: src/main/java/com/example/Blink/auth/service/AuthService.java
Methods: login(), refreshToken()
Lines: 103, 247
```

**Problem:** Refresh token expiry uses `LocalDateTime.now()` — a type with no timezone information:
```java
LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(refreshTokenProperties.getExpirationMinutes());
// ...
if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
```

If the application server and database server are in different timezones (common in cloud deployments), token expiry comparison becomes unreliable — tokens may expire too early or never expire.

**Recommendation:** Use `Instant` (UTC) everywhere. Change `Token.expiresAt` to `Instant`.

---

### PR-014 · High · URL Shortener — Short Code Collision Throws Raw RuntimeException

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Method: generateUniqueShortCode()
Lines: 88-100
```

**Problem:** After 10 collision-check retries, the method throws:
```java
throw new RuntimeException("Failed generating unique code");
```

This is caught by the generic `RuntimeException` handler in `GlobalExceptionHandler` and returned as a generic 500. No logging exists to trace this failure. Additionally, each retry queries the database (`existsByShortUrl`) — up to 10 database round-trips — while holding a write transaction, increasing connection hold time and latency.

**Impact:** Silent 500 failures with no root cause logged; increased DB pressure under collision.

**Recommendation:** Use a custom `ShortCodeExhaustedException` with `log.error()` when retries are exhausted. Consider sequence-based generation to eliminate collisions.

---

### PR-015 · High · Exception Handling — UnauthorizedException Returns HTTP 409 (Should be 403)

**Location:**
```
File: src/main/java/com/example/Blink/exception/GlobalExceptionHandler.java
Lines: 153-161
```

**Problem:**
```java
@ExceptionHandler(UnauthorizedActionException.class)
public ResponseEntity<BaseResponse> handleUnauthorizedAction(...) {
    return buildErrorResponse(ex, request, HttpStatus.CONFLICT);  // WRONG: should be 403
}
@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<BaseResponse> handleUnauthorizedException(...) {
    return buildErrorResponse(ex, request, HttpStatus.CONFLICT);  // WRONG: should be 403
}
```

Authorization failures should return HTTP 403 Forbidden. HTTP 409 is for data conflicts (duplicate resource, etc.). Every ownership check failure in the application (`url.getUser() != currentUser`) currently returns 409 to the client.

**Impact:** API clients cannot distinguish authorization failures from data conflicts. Frontend error handling is broken. REST semantics violated.

**Recommendation:** Change both handlers to `HttpStatus.FORBIDDEN`.

---

### PR-016 · High · Cache — Duplicate qrCodes Cache Definition

**Location:**
```
File: src/main/java/com/example/Blink/config/CacheConfig.java
Lines: 29-30
```

**Problem:**
```java
buildCache("qrCodes", 10, TimeUnit.MINUTES, 5_000),   // line 29
buildCache("qrCodes", 10, TimeUnit.MINUTES, 10_000),  // line 30 — duplicate!
```

`SimpleCacheManager` returns the first matching cache by name. The second definition is dead code. The intended max size is accidentally determined by list order rather than explicit intent.

**Recommendation:** Remove the duplicate entry. Choose the correct max size (5,000 or 10,000).

---

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

### PR-018 · High · Database — LAZY Loading in Transactional Methods Without JOIN FETCH

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Methods: toggleStatus(), updateUrl(), removePassword(), changePassword()
Lines: 181-193, 222-251, 253-268, 289-319
```

**Problem:** Multiple methods load `Url` via `urlRepository.findById(urlId)` then access `url.getUser().getUserId()` for ownership checks. Since `Url.user` is `FetchType.LAZY`, this triggers a separate SQL query for each ownership check — hidden N+1 behavior in the hot path.

The `findByIdWithUser(urlId)` method in `UrlRepository` (line 76-82) already does `JOIN FETCH u.user`, but these methods don't use it.

**Recommendation:** Replace `urlRepository.findById(urlId)` with `urlRepository.findByIdWithUser(urlId)` in all methods that access `url.getUser()`.

---

### PR-019 · High · Security — JWT Secret Key Derived from ASCII Byte Encoding

**Location:**
```
File: src/main/java/com/example/Blink/jwt/service/JwtService.java
Method: getSignKey()
Lines: 29-31
```

**Problem:**
```java
return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
```

The `secret` property is a hex string (`3c9d4f0b1e284b8b...`). `getBytes(UTF_8)` converts each hex character to its ASCII byte value — producing 64 bytes of ASCII values 48-57 and 97-102 (ASCII for 0-9 and a-f), not the 32 cryptographically random bytes the hex string represents. Effective key entropy is significantly lower than intended. Additionally, this committed secret must be considered compromised (see PR-002).

**Recommendation:** Decode the hex secret to actual bytes, or generate a new Base64-encoded 32-byte random key and decode with `Base64.getDecoder().decode(secret)`. Rotate immediately.

---

### PR-020 · High · URL Shortener — Blocked Domain Check Missing from URL Creation

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Method: generateShortUrl()
Lines: 63-85
```

**Problem:** The URL creation flow never checks whether the original URL's domain is in the blocked list. The `BlockedUrlService.isDomainBlocked()` exists and is cached, but is **never called** from `UrlService.generateShortUrl()`. A user can shorten `https://malware-site.com/payload` even if that domain is explicitly blocked by an admin.

Additionally, custom aliases are not validated against reserved path segments (`my`, `check`, `dashboard`, `id`) that would conflict with existing API routes. An alias of `"my"` would create `https://blink.ly/my` which shadows the user's URL list endpoint.

**Recommendation:** (1) Call `blockedUrlService.isDomainBlocked()` on the original URL's domain in `generateShortUrl()`. (2) Maintain a reserved alias set and reject aliases matching existing route path segments.

**Evidence:**
```java
// UrlService.java lines 67-69 — only uniqueness check, no blocked-domain check
if(alias != null && urlRepository.existsByCustomAlias(alias)){
    throw new AliasAlreadyUsed();
}
// No: if(blockedUrlService.isDomainBlocked(extractDomain(request.getOriginalUrl()))) { ... }
```

---

### PR-021 · High · Rate Limiting — Register Endpoint Path Mismatch; Dead SecurityConfig Entry

**Location:**
```
File: src/main/java/com/example/Blink/rate_limit/RateLimitFilter.java
Lines: 64-67
File: src/main/java/com/example/Blink/security/SecurityConfig.java
Line: 53
```

**Problem:** `RateLimitFilter.resolveRule()` correctly targets `POST /api/v1/auth/register` for the REGISTER rate rule. However, `SecurityConfig.java` line 53 permits `/api/v1/users/register` — a path that **does not exist** in any controller. The actual registration endpoint is `POST /api/v1/auth/register` (per `AuthController.java`). This stale config entry permits a non-existent endpoint while potentially confusing security audits and tooling.

**Impact:** Dead security config creates maintenance confusion; any future route at `/api/v1/users/register` would be automatically public without intentional configuration.

**Recommendation:** Remove the stale `/api/v1/users/register` entry from `SecurityConfig`.

---

### PR-022 · High · Scheduler — URL Cleanup: External API Calls Inside Transaction, No Cascade for UrlClicks

**Location:**
```
File: src/main/java/com/example/Blink/scheduler/service/UrlCleanupService.java
Method: removeExpiredUrls()
Lines: 22-33
```

**Problem:**

1. **External API in transaction:** The entire cleanup runs in a single `@Transactional` method. `imageService.deleteImage()` (Cloudinary API) is called inside the transaction. If Cloudinary fails mid-loop, the DB transaction rolls back — but Cloudinary deletions from earlier iterations are **not** rolled back. Next run tries to delete already-deleted images → `ImageDeletedException` → cleanup fails permanently.

2. **N+1 Cloudinary API calls in transaction:** For each URL, a Cloudinary HTTP call is made while holding a DB connection. With many expired URLs, this holds connections for the total sum of all API call durations, potentially exhausting the connection pool.

3. **UrlClick orphans:** When `urlRepository.delete(url)` is called, associated `UrlClick` records are not deleted (no cascade, no explicit delete). Orphaned click data accumulates indefinitely.

4. **No logging:** No log statements to track cleanup progress or failures.

**Recommendation:** Process in batches; delete Cloudinary images outside transaction boundary; add `urlClickRepository.deleteByUrl_UrlId(url.getUrlId())` before URL delete; add `log.info()`/`log.error()` statements.

---

### PR-023 · High · Authentication — reGenerateCode() Allows Code Reset for Active Accounts

**Location:**
```
File: src/main/java/com/example/Blink/auth/service/AuthService.java
Method: reGenerateCode()
Lines: 143-157
```

**Problem:** `reGenerateCode()` does not check whether the account is already active. An already-verified user can call this endpoint, receiving a new verification code. This code is then stored in `user.verificationCode` on a live account. Because `resetPassword()` only checks `verificationCode` equality (not purpose), this regenerated code can be used to reset the user's password without going through `forgetPassword()`.

**Recommendation:** Add `if (user.isActive()) { throw new AccountAlreadyVerifiedException(); }` at the beginning of `reGenerateCode()`.

---

### PR-024 · High · Performance — getCurrentUser() Issues DB Query on Every Authenticated Request

**Location:**
```
File: src/main/java/com/example/Blink/security/AuthenticatedUserService.java
Method: getCurrentUser()
Line: 33
```

**Problem:**
```java
return userRepository.findByIdWithRole(userDetails.getUserId())
        .orElseThrow(UserNotFoundException::new);
```

Every call to `getCurrentUser()` issues a `SELECT u FROM User u JOIN FETCH u.role WHERE u.userId = ?` query. This is called in `UrlService`, `QrCodeService`, `UrlClickService`, and `AuthService` — multiple times per request in some flows.

Under 100 req/sec, this alone adds 100+ extra DB queries per second for user lookups. These compete with URL lookup queries on the highest-traffic path (redirect).

**Recommendation:** For ownership and role checks, use `CustomUserDetails` from the security context directly — it already holds `userId` and role. Reserve `getCurrentUser()` (DB fetch) only for operations that truly need a fresh entity state (e.g., password change).

---

### PR-025 · High · Security — Actuator health Endpoint Exposes Full System Details Without Auth

**Location:**
```
File: src/main/resources/application.properties
Lines: 97-99
```

**Problem:**
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

`health` with `show-details=always` exposes HikariCP pool status, RabbitMQ state, disk space, and JVM memory to any unauthenticated caller. `metrics` endpoint exposes performance counters. No authentication is required.

**Recommendation:** Change to `show-details=when_authorized`. Restrict actuator to an internal management port or add security config requiring admin role.

---

### PR-026 · Medium · Exception Handling — ExpiredJwtException Returns HTTP 400 Instead of 401

**Location:**
```
File: src/main/java/com/example/Blink/exception/GlobalExceptionHandler.java
Lines: 84-87
```

**Problem:**
```java
@ExceptionHandler(ExpiredJwtException.class)
public ResponseEntity<BaseResponse> handleJwtExpired(ExpiredJwtException ex, WebRequest request) {
    return buildErrorResponse(Messages.SESSION_EXPIRED, request, HttpStatus.BAD_REQUEST);
}
```

An expired JWT is an authentication failure → should return 401 Unauthorized, not 400 Bad Request. The `JwtAuthenticationFilter` correctly returns 401 (line 59), creating inconsistent behavior depending on which handler catches the exception first.

**Recommendation:** Change `HttpStatus.BAD_REQUEST` to `HttpStatus.UNAUTHORIZED`.

---

### PR-027 · Medium · Database — verificationCode Column Has No Length Constraint

**Location:**
```
File: src/main/java/com/example/Blink/user/entity/User.java
Line: 41
```

**Problem:**
```java
private String verificationCode;
```

No `@Column(length=...)` annotation. MySQL defaults to `VARCHAR(255)`. Intent is not documented. Any future change to code generation (e.g., longer UUID-based codes) would silently be constrained by this undocumented default.

**Recommendation:** Add `@Column(length = 10)` to document intent and enforce the constraint.

---

### PR-028 · Medium · Database — High-Traffic Columns Missing Explicit Index Declarations

**Location:**
```
File: src/main/java/com/example/Blink/url/entity/Url.java
Lines: 30-36
```

**Problem:** `shortUrl` and `customAlias` have `@Column(unique = true)` which creates implicit unique indexes. However, `findByShortUrl()` is called on **every redirect** — the highest-traffic operation. Implicit indexes from unique constraints exist but are not named or explicitly managed. With `ddl-auto=update`, if the table existed before the unique constraint was added, the index may be absent.

**Recommendation:** Add `@Table(indexes={@Index(name="idx_url_short_url", columnList="shortUrl")})` explicitly. Verify index existence with `SHOW INDEX FROM Urls` post-deployment.

---

### PR-029 · Medium · Configuration — Localhost Origins in Production CORS Config

**Location:**
```
File: src/main/resources/application.properties
Lines: 36-38
```

**Problem:** Even if the CORS config were correctly wired (see PR-004), it allows localhost origins in what is intended as a production configuration file:
```properties
app.cors.allowed-origin-patterns[0]=http://localhost:4200
app.cors.allowed-origin-patterns[1]=http://localhost:3000
```

**Recommendation:** Use Spring profiles. Move production CORS to `application-prod.properties`. Remove all localhost origins from production config.

---

### PR-030 · Medium · Rate Limiting — Uses getRemoteAddr() Instead of X-Forwarded-For

**Location:**
```
File: src/main/java/com/example/Blink/rate_limit/RateLimitFilter.java
Method: resolveKey()
Line: 114
```

**Problem:**
```java
return "IP_" + request.getRemoteAddr();
```

Behind a load balancer, `getRemoteAddr()` returns the load balancer's IP — not the actual client IP. All unauthenticated users share a single rate limit bucket, meaning one legitimate user exhausting the login rate limit blocks ALL users.

Contrast with `UrlClickService.extractClientIp()` which correctly reads `X-Forwarded-For`.

**Recommendation:** Apply the same `X-Forwarded-For` parsing logic from `UrlClickService.extractClientIp()` in `resolveKey()`.

---

### PR-031 · Medium · Performance — Async Click Tracking Can Cascade Failures to Redirect

**Location:**
```
File: src/main/java/com/example/Blink/url_click/service/UrlClickService.java
Method: handleClickTrackingEvent()
Lines: 78-106
File: src/main/java/com/example/Blink/config/AsyncConfig.java
Lines: 17-20
```

**Problem:** The `clickTrackingExecutor` has a bounded queue of 100 events. No `RejectedExecutionHandler` is set → defaults to `AbortPolicy` (throws `RejectedExecutionException`).

When the queue fills under burst traffic (URL going viral), `eventPublisher.publishEvent()` in `trackClick()` propagates the rejection back to the redirect thread, causing `GET /{shortCode}` to fail with a 500 error.

**Why it fails in production:** Under burst traffic, analytics tracking failure cascades to redirect failure — the most user-visible action.

**Recommendation:** Set `executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy())`. Analytics loss during spikes is acceptable; redirect failure is not.

**Evidence:**
```java
// AsyncConfig.java lines 19-20 — no RejectedExecutionHandler; defaults to AbortPolicy
executor.setQueueCapacity(100);
// executor.setRejectedExecutionHandler(...); // MISSING

// UrlClickService.java line 75 — publishEvent before returning from redirect
eventPublisher.publishEvent(new ClickTrackingEvent(...));
```

---

### PR-032 · Medium · Security — getUserByIdentifier PreAuthorize References Undefined Parameter

**Location:**
```
File: src/main/java/com/example/Blink/user/controller/UserController.java
Method: getUserByIdentifier()
Lines: 41-44
```

**Problem:**
```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
@GetMapping("/find/{identifier}")
public ResponseEntity<BaseResponse> getUserByIdentifier(@PathVariable String identifier) {
```

The SpEL expression references `#userId`, but the method parameter is named `identifier`. `#userId` evaluates to `null`, making `null == authentication.principal.userId` always `false`. Regular users are always denied — only ADMINs can access this endpoint, which breaks the intended self-service lookup.

**Recommendation:** Remove the broken SpEL condition and restrict to admins only, or redesign ownership check in the service layer.

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

### PR-034 · Medium · Email — No SMTP Timeouts Configured

**Location:**
```
File: src/main/resources/application.properties
Lines: 67-73
```

**Problem:** No SMTP connection, read, or write timeout is configured. If Gmail SMTP is slow or unreachable, `javaMailSender.send(message)` blocks the RabbitMQ consumer thread indefinitely. With limited consumer threads, all consumer threads can be stuck waiting for SMTP, halting all email processing for the entire application.

**Recommendation:**
```properties
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
```

---

### PR-035 · Medium · Database — UrlClick Records Orphaned When URLs Are Deleted

**Location:**
```
File: src/main/java/com/example/Blink/scheduler/service/UrlCleanupService.java
```

**Problem:** When expired URLs are deleted, associated `UrlClick` records are NOT deleted. No `@OneToMany(cascade=CascadeType.ALL)` on `Url`, no explicit delete in cleanup, no database `ON DELETE CASCADE`. The `url_clicks` table grows indefinitely with orphaned rows referencing deleted URL IDs, causing referential integrity issues and wasted storage.

**Recommendation:** Add `urlClickRepository.deleteByUrl_UrlId(url.getUrlId())` before `urlRepository.delete(url)` in `removeExpiredUrls()`, or configure cascade at the DB level via migration.

---

### PR-036 · Medium · Performance — Click Count Incremented with DB Write on Every Redirect

**Location:**
```
File: src/main/java/com/example/Blink/url/service/UrlService.java
Method: getOriginalUrl()
Lines: 110-124
```

**Problem:**
```java
urlRepository.incrementClickCount(url.getUrlId());  // synchronous DB UPDATE
urlClickService.trackClick(url, request);
```

A `@Modifying` UPDATE runs synchronously on every redirect, holding a write lock on the URL row. For popular URLs, concurrent redirects contend on the same row's `clickCount` column, increasing latency and creating potential deadlock scenarios.

**Recommendation:** Use a Redis counter for click counts with periodic database sync. Or defer the update to the async click tracking path.

---

### PR-037 · Medium · Configuration — Thymeleaf Template Location Check Disabled

**Location:**
```
File: src/main/resources/application.properties
Line: 76
```

**Problem:**
```properties
spring.thymeleaf.check-template-location=false
```

This disables startup-time validation that all referenced templates exist. A missing or misspelled template file will not fail at startup — it will fail silently at runtime when an email is first sent.

**Recommendation:** Remove this property (default is `true`). Fix any missing templates that caused this to be disabled in the first place.

---

### PR-038 · Medium · Security — isDomainBlocked() Cache Key Calls normalizeDomain() Twice

**Location:**
```
File: src/main/java/com/example/Blink/blocked_url/service/BlockedUrlService.java
Method: isDomainBlocked()
Line: 78
```

**Problem:**
```java
@Cacheable(value = "blockedDomains", key = "#root.target.normalizeDomain(#domain)")
public boolean isDomainBlocked(String domain){
    return blockedUrlRepository.existsByDomain(normalizeDomain(domain));
}
```

The cache key SpEL expression calls `normalizeDomain()` on the target bean. If `normalizeDomain()` throws (null, blank, or invalid domain), the exception propagates before the method body is reached, causing confusing error paths through the caching framework. `normalizeDomain()` is also called twice per invocation (once in SpEL key, once in method body).

**Recommendation:** Use `@Cacheable(key = "#domain.trim().toLowerCase()")` and handle normalization inside the method body once.

---

### PR-039 · Medium · QR Code — Race Condition Between existsByUrl_urlId() and save()

**Location:**
```
File: src/main/java/com/example/Blink/qr_code/service/QrCodeService.java
Method: generateQrCode()
Lines: 47-65
```

**Problem:**
```java
if(qrCodeRepository.existsByUrl_urlId(urlId)){
    throw new QrCodeAlreadyExistsException();
}
// ... Cloudinary upload ...
QrCode qrCode = qrCodeRepository.save(...);
```

Two concurrent requests can both pass the `existsByUrl_urlId()` check, both upload to Cloudinary, and both attempt `save()`. The second save fails with a DB unique constraint violation — not specifically handled, surfaces as unhandled 500. If the second Cloudinary upload succeeds but the save fails, the image is orphaned in Cloudinary.

**Recommendation:** Handle `DataIntegrityViolationException` with a 409 response. Wrap `save()` in a try-catch to delete the Cloudinary image if the DB save fails.

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

---

### PR-041 · Low · Code Quality — Debug Typo in Production Entry Point

See PR-010. `"Successfullysssssssssss"` typo confirms this code was not reviewed before production deployment.

---

### PR-042 · Low · Database — searchByUserAndKeyword Returns Unbounded List

**Location:**
```
File: src/main/java/com/example/Blink/url/repository/UrlRepository.java
Lines: 65-74
```

**Problem:**
```java
List<Url> searchByUserAndKeyword(User user, String keyword);
```

No pagination. A user with 10,000 URLs receives all of them in a single response, loading everything into JVM heap. No call site found in `UrlService` — appears to be unused dead code.

**Recommendation:** Add `Pageable` parameter or remove if unused.

---

### PR-043 · Low · API Design — regenerateCode and forgetPassword Missing @Valid

**Location:**
```
File: src/main/java/com/example/Blink/auth/controller/AuthController.java
Lines: 64, 71
```

**Problem:**
```java
public ResponseEntity<BaseResponse> regenerateCode(@RequestBody EmailRequestDTO emailRequestDTO)
public ResponseEntity<BaseResponse> forgetPassword(@RequestBody EmailRequestDTO emailRequestDTO)
```

Both methods are missing `@Valid`, so Bean Validation constraints on `EmailRequestDTO` are not triggered.

**Recommendation:** Add `@Valid` to both parameters.

---

### PR-044 · Low · Code Quality — Duplicate Cache Entry (see PR-016)

The duplicate `qrCodes` cache definition in `CacheConfig.java` line 30 is dead code and a maintenance hazard.

---

### PR-045 · Low · Testing — Near-Zero Test Coverage

**Location:**
```
File: src/test/java/com/example/Blink/BlinkApplicationTests.java
File: src/test/java/com/example/Blink/url_click/service/UrlClickServiceTest.java
```

**Problem:** Only two test files exist:
- `BlinkApplicationTests.java` — context load test only
- `UrlClickServiceTest.java` — only `UrlClickService` tested

Zero coverage for: `AuthService`, `UrlService`, `UserService`, `QrCodeService`, `BlockedUrlService`, all controllers, `RateLimitFilter`, `JwtAuthenticationFilter`, security config.

**Impact:** Any regression in auth, URL shortening, or security logic reaches production undetected.

**Recommendation:** Implement unit tests for all service classes. Integration tests for auth flows. Security tests for rate limiting and JWT validation.

---

### PR-046 · Suggestion · Performance — Async Executor Needs Rejection Handler

**Location:**
```
File: src/main/java/com/example/Blink/config/AsyncConfig.java
Lines: 17-20
```

Set `executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy())` to prevent analytics rejection cascading to redirect failures under burst traffic. See PR-031 for full impact.

---

### PR-047 · Suggestion · Observability — No Structured Logging or Distributed Tracing

**Location:**
```
File: src/main/resources/application.properties
Lines: 53, 77-79
```

`logging.level.root=WARN` suppresses application INFO logs. No JSON structured logging, no distributed tracing (Micrometer/Zipkin), no correlation IDs. Diagnosing production issues across async RabbitMQ consumers and async click tracking is extremely difficult without these.

**Recommendation:** Add Logback JSON appender. Add `logging.level.com.example.Blink=INFO`. Add MDC correlation IDs in JWT filter. Add Micrometer Tracing.

---

### PR-048 · Suggestion · Production Readiness — No Spring Profiles

**Location:**
```
File: src/main/resources/application.properties
```

Single `application.properties` for all environments. Development settings (Swagger enabled, DDL auto update, localhost CORS) are identical to production settings. This is a production readiness gap.

**Recommendation:** Create `application-dev.properties` and `application-prod.properties`. Use `spring.profiles.active=prod` in production deployments.

---

### PR-049 · Suggestion · Code Quality — ImageService Uses Field Injection Instead of Final Field

**Location:**
```
File: src/main/java/com/example/Blink/common/service/ImageService.java
Lines: 19-23
```

`private Cloudinary cloudinary` is not `final`. `@Autowired` constructor injection is used inconsistently (all other services use `@RequiredArgsConstructor` with `final` fields). Mutable field reduces testability and thread-safety guarantees.

**Recommendation:** Use `@RequiredArgsConstructor` with `private final Cloudinary cloudinary`.

---

### PR-050 · Suggestion · Scheduler — Monthly Cleanup Too Infrequent

**Location:**
```
File: src/main/java/com/example/Blink/scheduler/UrlCleanupScheduler.java
Line: 14
```

```java
@Scheduled(cron = "0 0 0 1 * * ") // Runs at midnight on the first day of every month
```

Expired URLs remain in the database for up to a month after expiry. Analytics aggregate queries run against an ever-growing expired URL set. Consider running weekly or nightly for better data hygiene.

---

---

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

---


## Module Summaries

### Authentication Module Summary
**Strengths:** Refresh token rotation implemented. SHA-256 hashing of tokens before DB storage. BCrypt password encoding. JWT uses HMAC-SHA256. Logout properly revokes tokens.
**Weaknesses:** Password reset codes have no expiration. `verificationCode` multipurposed with no type discriminator. `LocalDateTime` (timezone unsafe) for token expiry. No verification code type check.
**Production Risks:** Account takeover via non-expiring reset codes. State confusion between verification and reset flows.
**Maintainability Score:** 5/10

---

### Security Module Summary
**Strengths:** Stateless session. `@PreAuthorize` method-level security. CSRF disabled (correct for stateless API). `X-Frame-Options: DENY`.
**Weaknesses:** CORS config broken (wrong property key). JWT filter bypass logic fragile. Swagger publicly exposed. Actuator shows full health details unauthenticated. `UnauthorizedException` returns 409 not 403.
**Production Risks:** CORS breakage → complete frontend outage. Swagger enables reconnaissance. Actuator leaks infrastructure details.
**Maintainability Score:** 4/10

---

### URL Shortener Module Summary
**Strengths:** Soft-delete pattern. Password protection with BCrypt. Custom aliases supported. Expiry enforced at access time. Collision detection with retry.
**Weaknesses:** SSRF in `checkUrl()`. No blocked domain check on creation. No reserved alias validation. Click count increment in hot redirect path. RuntimeException on collision exhaustion.
**Production Risks:** SSRF → cloud credential theft. Malicious URLs can be shortened. Redirect latency under contention.
**Maintainability Score:** 5/10

---

### Rate Limiting Module Summary
**Strengths:** Covers all critical auth endpoints. Per-user and per-IP keying. Rules cleanly defined in `RateLimitConfig`.
**Weaknesses:** In-memory state (OOM risk, non-distributed). `getRemoteAddr()` not `X-Forwarded-For`. Register endpoint path mismatch. No `Retry-After` header in 429 response. No STATS/redirect rate limit.
**Production Risks:** Rate limits ineffective in multi-instance deployment. OOM from unbounded bucket map. All users share one bucket behind load balancer.
**Maintainability Score:** 3/10

---

### Email / RabbitMQ Module Summary
**Strengths:** Async email decoupled via RabbitMQ. Retry configuration present. Separate consumers per event type.
**Weaknesses:** No DLQ → email permanently lost after 3 retries. No SMTP timeouts. Exception swallowed in two consumers. No email failure audit trail.
**Production Risks:** Silent email loss during SMTP or RabbitMQ outages. Consumer thread starvation from SMTP hangs.
**Maintainability Score:** 4/10

---

### Database Module Summary
**Strengths:** HikariCP properly sized with good JDBC URL tuning. Batch inserts configured. `open-in-view=false`. Index on tokens table.
**Weaknesses:** `ddl-auto=update` in production. TOCTOU race conditions on registration and QR code creation. UrlClick orphans on URL deletion. Missing explicit indexes on `shortUrl`.
**Production Risks:** Schema corruption on deployment. Growing orphaned click data. Unhandled `DataIntegrityViolationException`.
**Maintainability Score:** 5/10

---

### QR Code Module Summary
**Strengths:** Cloudinary for externalized storage. One QR per URL enforced by DB constraint.
**Weaknesses:** Race condition between existence check and save. Cloudinary orphan on DB failure. No async generation.
**Production Risks:** Duplicate QR codes under concurrent requests. Orphaned Cloudinary images.
**Maintainability Score:** 6/10

---

### Configuration Module Summary
**Strengths:** Actuator endpoints scoped to health/info/metrics only.
**Weaknesses:** 5+ secrets hardcoded with fallback defaults. No Spring profiles. CORS property key mismatch (production CORS broken). `thymeleaf.check-template-location=false`. `ddl-auto=update`.
**Production Risks:** Complete credential compromise from git history. CORS outage. DDL schema mutation on startup.
**Maintainability Score:** 2/10

---

### Testing Module Summary
**Strengths:** One unit test class for `UrlClickService` with meaningful test cases.
**Weaknesses:** 0% coverage of auth, URL shortening, QR code, rate limiting, security modules. No integration tests. No controller tests. No concurrency tests.
**Production Risks:** Any regression reaches production undetected.
**Maintainability Score:** 1/10

---

## Executive Summary

### Overall Score: 28 / 100

### Production Ready? **NO** ❌

| Severity | Count |
|:---------|:------|
| **Critical** | 10 |
| **High** | 15 |
| **Medium** | 13 |
| **Low** | 5 |
| **Suggestion** | 5 |

---

## Top 10 Most Important Issues

| Rank | ID | Why It Ranks Highest |
|:-----|:---|:--------------------|
| 1 | **PR-002** | 5 credential types committed to git — JWT, Gmail, RabbitMQ, Cloudinary, DB. Every service is compromised. Must rotate and remove before any deployment. |
| 2 | **PR-007** | SSRF on a public, unauthenticated endpoint. In cloud environments, leads directly to IAM credential exfiltration and full cloud account compromise in minutes. |
| 3 | **PR-004** | CORS config reads the wrong property key. The production frontend is completely broken — all browser clients receive CORS errors in any real deployment. |
| 4 | **PR-003** | `ddl-auto=update` can corrupt the production schema on any deployment. One incorrect entity annotation change destroys production data. |
| 5 | **PR-005** | Password reset codes never expire. Any intercepted code from days or weeks ago remains valid for account takeover. |
| 6 | **PR-008** | In-memory rate limiting fails silently in multi-instance deployments and causes OOM under DDoS. The protection mechanism does not work in production. |
| 7 | **PR-009** | Verification code multipurposed — cross-flow replay attacks possible; inactive user password change without going through proper reset flow. |
| 8 | **PR-017** | No dead-letter queue means verification and password reset emails are permanently lost after 3 SMTP failures. Users cannot activate accounts or reset passwords. |
| 9 | **PR-006** | JWT filter bypass regex matches authenticated routes; fragile dual-maintenance creates ongoing authentication bypass risk. |
| 10 | **PR-015** | Every `UnauthorizedException` returns HTTP 409 (Conflict) instead of 403 (Forbidden). All frontend authorization handling is broken. |

---

## Production Checklist

- [ ] **Authentication secure** — Reset codes expire; code type discriminated; token expiry uses UTC Instant
- [ ] **Authorization secure** — CORS config fixed; JWT filter bypass removed; UnauthorizedException returns 403
- [ ] **Password reset secure** — Expiry timestamp added; code type enforced; reuse and cross-flow replay prevented
- [ ] **Rate limiting production-ready** — Redis-backed distributed rate limiter; X-Forwarded-For IP extraction; bounded memory with TTL eviction
- [ ] **Database optimized** — `ddl-auto=validate` with Flyway; explicit indexes; race conditions handled; UrlClick cascade on URL delete
- [ ] **Logging production-ready** — Structured JSON logging; correlation IDs; System.out.println removed; appropriate log levels per package
- [ ] **Exception handling complete** — DataIntegrityViolationException handled; UnauthorizedException returns 403; ExpiredJwtException returns 401
- [ ] **Docker ready** — No Dockerfile found. Docker image, health check CMD, resource limits, and graceful shutdown config needed
- [ ] **Monitoring ready** — Actuator secured; structured metrics; distributed tracing; DLQ alerting configured
- [ ] **CI/CD ready** — No pipeline config found. Test, build, security scan, deploy pipeline needed
- [ ] **Health checks configured** — Actuator `/health` accessible; `show-details=when_authorized` not `always`
- [ ] **Validation complete** — `@Valid` on regenerateCode/forgetPassword; URL scheme allowlist (http/https only); alias reserved keyword check
- [ ] **API production-ready** — HTTP status codes corrected; Swagger disabled by default in production; consistent versioning
- [ ] **Secrets managed** — All hardcoded credentials removed and rotated; vault or environment injection used
- [ ] **SSRF prevented** — `checkUrl()` IP blocklist and scheme allowlist; or endpoint removed/restricted to authenticated users
- [ ] **Email resilience** — Dead-letter queues configured; SMTP timeouts set; DLQ consumer with alerts; failed email recovery
- [ ] **Blocked URL enforcement** — URL creation validates domain against blocked list; aliases validated against reserved paths
- [ ] **QR code race condition** — DataIntegrityViolationException handled; Cloudinary orphan cleanup on DB failure
- [ ] **Cleanup scheduler** — UrlClick orphans cleaned up; Cloudinary calls outside transaction; batched processing; logging added
- [ ] **Test coverage** — Unit tests for all service classes; integration tests for auth flows; security tests

---

## Final Verdict

### I would NOT approve this application for production. This is a firm **NO-GO**.

---

This is an application with **clearly good intentions and thoughtful architectural choices** — async event-driven email, RabbitMQ decoupling, Caffeine caching, Bucket4j rate limiting, BCrypt password hashing, SHA-256 token hashing, refresh token rotation, and GeoIP analytics. The developer has real ability and an impressive feature set for a URL shortener. The architecture is sound.

**However, the gap between intention and production-safe execution is too large to approve.**

---

### Critical Blockers — Must Fix Before Any Production Deployment:

**1. Credential Exposure (PR-001, PR-002):** Five separate credential categories are hardcoded as fallback defaults in a committed file: database password, JWT signing key, Gmail App Password, RabbitMQ broker password (no env var whatsoever), and Cloudinary API secret. This single fact makes the entire system compromised before the first line of business code runs. Every credential must be rotated immediately and removed from git history using `git filter-branch` or BFG Repo Cleaner. This is a Day 0 breach.

**2. SSRF Vulnerability (PR-007):** The publicly accessible `checkUrl()` endpoint opens arbitrary HTTP connections to any URL a user provides, with no IP allowlisting, scheme validation, or authentication gate. In AWS/GCP/Azure, this leads to cloud metadata endpoint access (`169.254.169.254`) and IAM credential exfiltration within seconds of an attacker discovering the endpoint. This must be fixed before the application is exposed to the internet.

**3. CORS Misconfiguration (PR-004):** Due to a property key mismatch (`cors.allowed-origins` vs `app.cors.allowed-origin-patterns`), the production CORS configuration always falls back to `http://localhost:3000`. Every production frontend request fails with a CORS error. The application is non-functional in production for browser clients.

**4. DDL Auto Update (PR-003):** `spring.jpa.hibernate.ddl-auto=update` running against a production database is an irrecoverable schema corruption risk. Every deployment runs unsupervised schema mutations. This must be replaced with Flyway before the first deployment.

**5. Password Reset Without Expiry (PR-005, PR-009):** Reset codes are permanent and multipurposed. An intercepted email from any point in the past is a valid account takeover vector. This is a fundamental authentication security failure.

**6. Rate Limiting Failure (PR-008):** The in-memory rate limiting implementation is: (a) not distributed — bypassed by horizontal scaling, (b) unbounded — causes OOM under DDoS, (c) stateless — reset on every restart. It provides a false sense of security while offering no real protection in any production deployment scenario.

---

### What Can Be Deferred:

All Medium/Low/Suggestion findings can be addressed post-launch in the first sprint, provided the 10 Critical and most High-severity items are resolved first. In particular, the async executor rejection handler (PR-031, PR-046), SMTP timeouts (PR-034), structured logging (PR-047), and Spring profiles (PR-048) are production hygiene items that can be addressed without delaying go-live significantly.

---

> This audit was conducted on 2026-07-04. All findings are based on static code analysis and execution flow tracing of the committed codebase. Every credential identified as exposed should be treated as compromised and rotated immediately regardless of deployment status. No code was modified during this audit.
