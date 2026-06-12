package com.example.Blink.common.messages;

public class Messages {

    //========================= URL =========================

    public static final String ALIAS_ALREADY_USED = "Alias already taken";
    public static final String URL_NOT_FOUND = "URL not found";
    public static final String URL_CREATED = "URL created successfully";
    public static final String URL_UPDATED = "URL updated successfully";
    public static final String URL_DELETED = "URL deleted successfully";
    public static final String URL_DEACTIVATED = "URL deactivated successfully";
    public static final String URL_ACTIVATED = "URL activated successfully";
    public static final String URL_UNLOCKED = "URL unlocked successfully";
    public static final String URL_STATS = "URL statistics retrieved successfully";
    public static final String URL_EXPIRED = "This URL has expired";
    public static final String URL_INACTIVE = "This URL is inactive";
    public static final String URL_LOCKED = "This URL is password protected";
    public static final String WRONG_PASSWORD = "Incorrect password";
    public static final String INVALID_NEW_PASSWORD = "Invalid new password";
    public static final String URL_CLICKS_FETCHED = "URL clicks retrieved successfully";
    public static final String TOTAL_CLICKS_FETCHED = "Total clicks retrieved successfully";
    public static final String CLICKS_TODAY_FETCHED = "Today's clicks retrieved successfully";
    public static final String TOP_COUNTRIES_FETCHED = "Top countries retrieved successfully";
    public static final String TOP_BROWSERS_FETCHED = "Top browsers retrieved successfully";
    public static final String URL_TOGGLED = "URL status toggled successfully";
    public static final String URL_FETCHED = "URL retrieved successfully";
    public static final String URLS_FETCHED = "URLs retrieved successfully";
    public static final String PASSWORD_REMOVED = "Password removed successfully";
    public static final String DASHBOARD_FETCHED = "Dashboard data retrieved successfully";
    public static final String PASSWORD_CHANGED = "Password changed successfully";
    //========================= User =========================

    public static final String USER_CREATED = "User created successfully";
    public static final String USER_UPDATED = "User updated successfully";
    public static final String USER_DELETED = "User deactivated successfully";
    public static final String USER_ACTIVATED = "User activated successfully";
    public static final String USER_FETCHED = "User retrieved successfully";
    public static final String USERS_FETCHED = "Users retrieved successfully";
    public static final String PROFILE_PICTURE_UPDATED = "Profile picture updated successfully";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_ACTIVE = "This user account is deactivated";
    public static final String USER_ALREADY_DEACTIVATED = "This user account is already deactivated";
    public static final String USER_ALREADY_ACTIVATED = "This user account is already activated";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

    //========================= Role =========================

    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String ROLE_ALREADY_EXISTS = "Role name already exists";
    public static final String ROLE_CREATED = "Role created successfully";
    public static final String ROLE_UPDATED = "Role updated successfully";
    public static final String ROLE_DELETED = "Role deleted successfully";
    public static final String ROLE_FETCHED = "Role retrieved successfully";
    public static final String ROLES_FETCHED = "Roles retrieved successfully";

    //========================= Auth =========================

    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logged out successfully";
    public static final String ALREADY_LOGGED_OUT = "You are already logged out";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    public static final String INVALID_TOKEN = "Invalid or expired refresh token";
    public static final String BAD_CREDENTIALS = "Invalid username or password";
    public static final String AUTH_FAILED = "Authentication failed";
    public static final String ACCESS_DENIED = "You do not have permission to perform this action";
    public static final String SESSION_EXPIRED = "Your session has expired, please log in again";
    public static final String UNAUTHORIZED = "Unauthorized access";

    //========================= Mail =========================

    public static final String FAILED_EMAIL = "Failed to send email, please try again later";

    //========================= Image =========================

    public static final String IMAGE_UPLOAD_FAILED = "Failed to upload image, please try again later";
    public static final String IMAGE_NULL = "Image must not be null or empty";
    public static final String IMAGE_DELETED_FAILED = "Failed to delete image, please try again later";


    // ==================== QR Code Messages ====================
    public static final String QR_GENERATED = "QR code generated successfully.";
    public static final String QR_GENERATION_FAILED = "Failed to generate the QR code. Please try again.";
    public static final String QR_ALREADY_EXISTS = "Qr Code already exists";
    public static final String QR_NOT_FOUND = "QR Code not found";

    //========================= Blocked URL =========================

    public static final String DOMAIN_ALREADY_BLOCKED = "Domain is already blocked";
    public static final String BLOCKED_URL_NOT_FOUND = "Blocked URL not found";
    public static final String DOMAIN_NOT_IN_BLOCKED_LIST = "Domain not found in blocked list";
    public static final String INVALID_DOMAIN = "Invalid domain";
    public static final String INVALID_DOMAIN_FORMAT = "Invalid domain format";
    public static final String DOMAIN_EMPTY = "Domain cannot be empty";
    public static final String DOMAIN_BLOCKED = "Domain blocked successfully";
    public static final String BLOCKED_URL_UPDATED = "Blocked URL updated successfully";
    public static final String BLOCKED_URL_FETCHED = "Blocked URL retrieved successfully";
    public static final String BLOCKED_URLS_FETCHED = "Blocked URLs retrieved successfully";
    public static final String DOMAIN_UNBLOCKED = "Domain unblocked successfully";
    public static final String DOMAIN_CHECK_RESULT = "Domain block status retrieved successfully";

    //========================= General =========================

    public static final String INVALID_DATA = "Invalid or malformed request body";
    public static final String INVALID_URL = "The provided URL is not reachable or invalid";
    public static final String URL_VALID = "URL is valid and reachable";
    public static final String URL_INVALID = "URL is not reachable or invalid";
    public static final String REQUEST_NOT_SUPPORTED = "HTTP method not supported for this endpoint";

    //========================= Rate Limit =========================
    public static final String TOO_MANY_REQUESTS = "Too many requests";
}
