# PRODUCTION READINESS AUDIT — Blink URL Shortener (Re-Audit)

**Auditor:** Senior Backend Security Engineer  
**Date:** 2026-07-06  
**Scope Exclusion:** Redis, distributed rate limiting, horizontal scaling

---

## Executive Summary

| Metric | Value |
|:-------|:------|
| Total Findings | 38 |
| Critical | 5 |
| High | 10 |
| Medium | 14 |
| Low | 6 |
| Suggestion | 3 |
| Production Ready? | NO — 5 critical blockers |

---

## Findings Summary Table

| ID | Severity | Category | Title |
|:---|:---------|:---------|:------|

| C-01 | Medium | Validation | customAlias allows empty string |
| C-02 | Medium | Validation | UpdateUrlRequest.expireAt accepts past dates |
| C-03 | Medium | Validation | UpdateUrlRequest fields unconstrained |
| C-04 | Medium | Validation | Unlock password NPE risk |
| C-05 | Medium | Security | Custom alias not validated against reserved paths |
| C-06 | Medium | Authorization | getQrCode() has no ownership check |
| C-07 | Medium | Exception | VerificationCodeExpiredException returns HTTP 400 |
| C-08 | Medium | Database | Mixed Instant vs Instant in entities |
| C-09 | Medium | Performance | Unnecessary role JOIN in forgetPassword/reGenerateCode |
| C-10 | Medium | Security | Swagger permitted unconditionally — no profile guard |
| C-11 | Medium | Database | isDomainBlocked() SpEL cache key throws before method body |
| C-12 | Medium | Config | No Spring profiles — dev settings mixed with prod |
| C-13 | Medium | Observability | DLQ consumer has no alerting — only a TODO comment |
| C-14 | Medium | Testing | Near-zero test coverage |
| D-01 | Low | Config | logging.level.root=WARN suppresses application INFO logs |
| D-02 | Low | Database | User.verificationCode has no @Column(length) |
| D-03 | Low | Security | Deactivated users hold valid JWTs for 7 days |
| D-04 | Low | Security | isAccountNonLocked() always true — no lockout |
| D-05 | Low | Scheduler | Cleanup runs monthly; expired URLs in DB up to 59 days |
| D-06 | Low | Database | Url.expireAt uses Instant — timezone sensitive |
| E-01 | Suggestion | Performance | hashToken() creates MessageDigest on every call |
| E-02 | Suggestion | Observability | No structured logging or correlation IDs |
| E-03 | Suggestion | Security | No Content-Security-Policy header |

---


## HIGH FINDINGS




## MEDIUM FINDINGS




### C-06 — getQrCode() Has No Ownership Check

**File:** QrCodeService.java:77-91

Any authenticated user can retrieve any other user's QR code by URL ID, including the Cloudinary publicId. generateQrCode() and deleteQrCode() enforce ownership; getQrCode() does not.

**Fix:** Add getCurrentUser() and ownership check using findByIdWithUser() before returning QR data.

---

### C-07 — VerificationCodeExpiredException Returns HTTP 400

**File:** GlobalExceptionHandler.java:90-93

An expired code is an auth/session concept. HTTP 400 implies malformed request. Should be 401 or 410.

**Fix:** Change to HttpStatus.UNAUTHORIZED.

---

### C-08 — Mixed Instant vs Instant in Entities

**File:** User.java:49-56, Token.java:38-41, Url.java:50, UrlClick.java:53

User and Url use Instant. Token and UrlClick use Instant. In cloud deployments where JVM and DB timezones differ, Instant comparisons produce incorrect expiry behavior.

**Fix:** Standardize all timestamp fields on Instant.

---

### C-09 — Unnecessary Role JOIN in forgetPassword/reGenerateCode

**File:** AuthService.java:145, 169

findByUsernameOrEmailWithRole() issues JOIN FETCH u.role. Neither method accesses role — only verificationCode, active, email, username are needed.

**Fix:** Use a simpler query without the role JOIN in these methods.

---

### C-10 — Swagger Permitted Unconditionally; No Profile Guard

**File:** SecurityConfig.java:49, application.properties:75

Swagger disabled by default via env var, but SecurityConfig unconditionally adds permitAll() for /swagger-ui/**. If SWAGGER_ENABLED=true is accidentally set in production, full API docs are immediately public.

**Fix:** Add @Profile("!prod") guard to Swagger security configuration.

---

### C-11 — isDomainBlocked() SpEL Cache Key Throws Before Method Body

**File:** BlockedUrlService.java:78

@Cacheable(key = "#root.target.normalizeDomain(#domain)") invokes normalizeDomain() during cache key computation. Null/blank domain throws DomainEmptyException through Spring caching infrastructure before the method body runs. normalizeDomain() also runs twice per cache miss.

**Fix:** Use @Cacheable(key = "#domain?.toLowerCase()?.trim()"). Normalize once inside the method body.

---

### C-12 — No Spring Profiles

**File:** application.properties

Single file for all environments. Localhost CORS origins, ddl-auto=update, and Swagger config coexist with production settings. No application-dev.properties or application-prod.properties.

**Fix:** Create profile-specific property files. Use spring.profiles.active=prod in production.

---

### C-13 — DLQ Consumer Has No Alerting

**File:** DeadLetterQueueConsumer.java:42-43

Failed emails only produce log entries. Commented-out TODO for alerting integration. No operational notification for silently lost password reset or verification emails.

**Fix:** Implement Slack webhook or email notification on DLQ message receipt.

---

### C-14 — Near-Zero Test Coverage

**File:** src/test/

Only BlinkApplicationTests (context load) and one UrlClickService unit test exist. Zero coverage for AuthService, UrlService, QrCodeService, BlockedUrlService, all controllers, all security filters.

**Fix:** Implement unit tests for all service classes and integration tests for auth flows.

---

## LOW FINDINGS

---

### D-01 — Root Log Level Suppresses Application INFO Logs

`logging.level.root=WARN` with no com.example.Blink override silently discards all log.info() calls in application code.

**Fix:** Add `logging.level.com.example.Blink=INFO`.

---

### D-02 — User.verificationCode Has No Column Length

User.java:42: `private String verificationCode;` defaults to VARCHAR(255). Actual values are 6 chars.

**Fix:** Add @Column(length=10).

---

### D-03 — Deactivated Users Hold Valid JWTs for 7 Days

UserService.deleteUser() sets active=false but existing JWTs (7-day expiry) remain valid. JwtAuthenticationFilter only checks signature and expiry, not user.isActive().

**Fix:** After loading userDetails in JwtAuthenticationFilter, check userDetails.isEnabled() and return 401 if false.

---

### D-04 — No Account Lockout Mechanism

CustomUserDetails.isAccountNonLocked() always returns true. 5 logins/minute rate limit allows approximately 7,200 password attempts per day.

**Fix:** Track failed login attempts. Lock account after N consecutive failures.

---

### D-05 — Expired URLs Remain in DB Up to 59 Days

Scheduler runs monthly on the 1st. Query filters for URLs expired more than 1 month ago (Instant.now().minusMonths(1)). A URL expired today stays in DB until the 1st of the month after next — potentially 59 days.

**Fix:** Run weekly. Query for expireAt < NOW() instead of < NOW() minus 1 month.

---

### D-06 — Url.expireAt Uses Instant — Timezone Sensitive

validateUrl() calls Instant.now() for comparison. If JVM and MySQL timezones differ, URLs expire at incorrect UTC moments.

**Fix:** Use Instant.now(ZoneOffset.UTC) consistently, or migrate expireAt to Instant.

---

## SUGGESTIONS

---

### E-01 — hashToken() Creates MessageDigest on Every Call

AuthService.java:232: MessageDigest.getInstance("SHA-256") on every login/logout/refresh.

**Fix:** Use ThreadLocal or DigestUtils.sha256Hex() from Apache Commons Codec.

---

### E-02 — No Structured Logging or Correlation IDs

No JSON logging. No MDC correlation IDs. Tracing a request across JWT filter, service, RabbitMQ consumer, and async click tracker is not possible.

**Fix:** Add MDC correlation ID in JwtAuthenticationFilter.doFilterInternal(). Configure Logback JSON appender.

---

### E-03 — No Content-Security-Policy Header

SecurityConfig sets X-Frame-Options and X-Content-Type-Options but omits CSP. Any HTML response surface lacks XSS containment.

**Fix:** Add contentSecurityPolicy("default-src 'self'") to headers configuration.

---

## Fixed Since Last Review

| Finding | Status |
|:--------|:-------|
| Hardcoded secrets (DB, JWT, SMTP, RabbitMQ, Cloudinary) | FIXED — all use env vars, no fallback defaults |
| Debug System.out.println in BlinkApplication | FIXED — removed |
| Password reset codes never expiring | FIXED — 5-minute verificationCodeExpiresAt added |
| UnauthorizedException returning HTTP 409 | FIXED — returns 403 Forbidden |
| ExpiredJwtException returning HTTP 400 | FIXED — returns 401 Unauthorized |
| No Dead-Letter Queue | FIXED — full DLX/DLQ topology with DLQ consumer |
| Duplicate qrCodes cache entry | FIXED |
| Async executor missing RejectedExecutionHandler | FIXED — DiscardOldestPolicy set |
| SMTP timeouts not configured | FIXED — 5000ms timeouts added |
| DataIntegrityViolationException not handled | FIXED — 409 handler added |
| Actuator show-details=always | FIXED — when_authorized, separate port 8081 |
| SSRF: no scheme or IP validation | PARTIALLY FIXED — IP blocklist added; DNS rebinding gap remains (A-05) |
| Blocked domain check absent from URL creation | FIXED — isDomainBlocked() called in generateShortUrl() |
| QR code race DataIntegrityViolationException unhandled | FIXED — caught; Cloudinary image deleted on failure |
| UrlClick records orphaned on cleanup | FIXED — deleteByUrl_UrlId() called |
| Click tracking rejection cascades to redirect | FIXED — wrapped in try/catch |
| Instant for refresh token expiry | FIXED — Token.expiresAt now uses Instant |

---

## Remaining Critical Blockers

All five must be resolved before production deployment:

1. A-01 — CORS broken: property key mismatch; setAllowedOrigins() rejects wildcards
2. A-02 — Code type confusion: verifyAccount() has no expiry check; cross-flow replay possible
3. A-03 — reGenerateCode() runs for active users — enables password reset bypass
4. A-04 — ddl-auto=update — schema corruption risk on every deployment
5. A-05 — DNS rebinding SSRF: two independent DNS resolutions in checkUrl()

---

Audit Date: 2026-07-06 | All findings reference exact file locations and line numbers | No code was modified during this audit
