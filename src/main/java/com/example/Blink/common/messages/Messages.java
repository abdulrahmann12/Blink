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

    //========================= Role =========================

    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String ROLE_ALREADY_EXISTS = "Role name already exists";
    public static final String ROLE_CREATED = "Role created successfully";
    public static final String ROLE_UPDATED = "Role updated successfully";
    public static final String ROLE_DELETED = "Role deleted successfully";
    public static final String ROLE_FETCHED = "Role retrieved successfully";
    public static final String ROLES_FETCHED = "Roles retrieved successfully";

    //========================= Auth =========================

    public static final String BAD_CREDENTIALS = "Invalid username or password";
    public static final String AUTH_FAILED = "Authentication failed";
    public static final String ACCESS_DENIED = "You do not have permission to perform this action";
    public static final String SESSION_EXPIRED = "Your session has expired, please log in again";
    public static final String UNAUTHORIZED = "Unauthorized access";

    //========================= Mail =========================

    public static final String FAILED_EMAIL = "Failed to send email, please try again later";

    //========================= General =========================

    public static final String INVALID_DATA = "Invalid or malformed request body";
    public static final String INVALID_URL = "The provided URL is not reachable or invalid";
    public static final String URL_VALID = "URL is valid and reachable";
    public static final String URL_INVALID = "URL is not reachable or invalid";
    public static final String REQUEST_NOT_SUPPORTED = "HTTP method not supported for this endpoint";

}
