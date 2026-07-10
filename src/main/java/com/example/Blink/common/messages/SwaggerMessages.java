package com.example.Blink.common.messages;

public class SwaggerMessages {

    //========================= Auth Tag =========================

    public static final String TAG_AUTH = "Auth";
    public static final String TAG_AUTH_DESC = "Endpoints for authentication and token management";

    public static final String LOGIN = "Login";
    public static final String LOGIN_DESC = "Authenticate with username/email and password, returns access and refresh tokens";

    public static final String REFRESH_TOKEN = "Refresh Token";
    public static final String REFRESH_TOKEN_DESC = "Exchange a valid refresh token for a new access token and rotated refresh token";

    public static final String LOGOUT = "Logout";
    public static final String LOGOUT_DESC = "Revoke the refresh token and end the session";

    public static final String VERIFY_ACCOUNT = "Verify Account";
    public static final String VERIFY_ACCOUNT_DESC = "Verify a user's account using a verification code sent via email";
    public static final String REGENERATE_CODE = "Regenerate Verification Code";
    public static final String REGENERATE_CODE_DESC = "Regenerate a new verification code for a user and send it via email";

    public static final String FORGOT_PASSWORD = "Forgot Password";
    public static final String FORGOT_PASSWORD_DESC = "Send a password reset email to the user with a reset link or code";

    public static final String RESET_PASSWORD = "Reset Password";
    public static final String RESET_PASSWORD_DESC = "Send a password reset email to the user with a reset link or code";

    public static final String CHANGE_PASSWORD = "Change Password";
    public static final String CHANGE_PASSWORD_DESC = "Change the password of the authenticated user by providing the current and new password";
    //========================= URL Tag =========================

    public static final String TAG_URL = "URL";
    public static final String TAG_URL_DESC = "Endpoints for creating, redirecting, and managing short URLs";

    //========================= Endpoints =========================

    public static final String CREATE_SHORT_URL = "Create Short URL";
    public static final String CREATE_SHORT_URL_DESC = "Generates a short URL from the provided original URL with optional alias, password, and expiry";

    public static final String REDIRECT_URL = "Redirect to Original URL";
    public static final String REDIRECT_URL_DESC = "Redirects to the original URL using the short code. Returns 423 if password protected";

    public static final String UNLOCK_URL = "Unlock Password-Protected URL";
    public static final String UNLOCK_URL_DESC = "Verifies the password and redirects to the original URL";

    public static final String CHECK_URL = "Check URL Reachability";
    public static final String CHECK_URL_DESC = "Checks whether the provided URL is reachable and returns true or false";

    public static final String GET_URL_STATS = "Get URL Statistics";
    public static final String GET_URL_STATS_DESC = "Returns details and statistics for a given short URL code";

    public static final String TOGGLE_URL_STATUS = "Toggle URL Status";
    public static final String TOGGLE_URL_STATUS_DESC = "Toggles the active/inactive status of a URL owned by the authenticated user";

    public static final String GET_URL_BY_ID = "Get URL By ID";
    public static final String GET_URL_BY_ID_DESC = "Returns URL details by its UUID. Accessible by the owner or an admin";

    public static final String GET_USER_URLS = "Get My URLs";
    public static final String GET_USER_URLS_DESC = "Returns a paginated list of all URLs created by the authenticated user";

    public static final String UPDATE_URL = "Update URL";
    public static final String UPDATE_URL_DESC = "Updates URL properties such as title, expiry, active status, and password";

    public static final String REMOVE_URL_PASSWORD = "Remove URL Password";
    public static final String REMOVE_URL_PASSWORD_DESC = "Removes the password protection from a URL owned by the authenticated user";

    public static final String GET_DASHBOARD = "Get Dashboard";
    public static final String GET_DASHBOARD_DESC = "Returns dashboard statistics including total URLs, active URLs, expired URLs, and total clicks";

    public static final String CHANGE_URL_PASSWORD = "Change URL Password";
    public static final String CHANGE_URL_PASSWORD_DESC = "Changes the password of a password-protected URL owned by the authenticated user";

    //========================= Admin URL Tag =========================

    public static final String TAG_ADMIN_URL = "Admin URL";
    public static final String TAG_ADMIN_URL_DESC = "Admin endpoints for managing and monitoring all short URLs";

    public static final String ADMIN_GET_ALL_URLS = "Get All URLs";
    public static final String ADMIN_GET_ALL_URLS_DESC = "Returns a paginated list of all URLs in the system";

    public static final String ADMIN_SEARCH_URLS = "Search URLs";
    public static final String ADMIN_SEARCH_URLS_DESC = "Searches URLs by keyword across short code, original URL, and title";

    public static final String ADMIN_GET_INACTIVE_URLS = "Get Inactive URLs";
    public static final String ADMIN_GET_INACTIVE_URLS_DESC = "Returns a paginated list of all inactive URLs";

    public static final String ADMIN_GET_EXPIRED_URLS = "Get Expired URLs";
    public static final String ADMIN_GET_EXPIRED_URLS_DESC = "Returns a paginated list of all expired URLs";

    public static final String ADMIN_GET_TOP_URLS = "Get Top URLs";
    public static final String ADMIN_GET_TOP_URLS_DESC = "Returns a paginated list of URLs ordered by click count descending";

    public static final String ADMIN_GET_DASHBOARD = "Get Admin Dashboard";
    public static final String ADMIN_GET_DASHBOARD_DESC = "Returns admin dashboard statistics including total, active, inactive, expired URLs and total clicks";

    //========================= URL Click Tag =========================

    public static final String TAG_URL_CLICK = "URL Clicks";
    public static final String TAG_URL_CLICK_DESC = "Endpoints for retrieving URL click analytics";

    public static final String GET_URL_CLICKS = "Get URL Clicks";
    public static final String GET_URL_CLICKS_DESC = "Returns all click records for a given URL ID. Only the URL owner can access this data";

    public static final String GET_TOTAL_CLICKS = "Get Total Clicks";
    public static final String GET_TOTAL_CLICKS_DESC = "Returns the total number of clicks for a given URL ID";

    public static final String GET_CLICKS_TODAY = "Get Clicks By Date";
    public static final String GET_CLICKS_TODAY_DESC = "Returns the total number of clicks for a given URL ID on a specific date (format: yyyy-MM-dd)";

    public static final String GET_TOP_COUNTRIES = "Get Top Countries";
    public static final String GET_TOP_COUNTRIES_DESC = "Returns the top 5 countries by click count for a given URL ID";

    public static final String GET_TOP_BROWSERS = "Get Top Browsers";
    public static final String GET_TOP_BROWSERS_DESC = "Returns the top 5 browsers by click count for a given URL ID";

    //========================= Role Tag =========================

    public static final String TAG_ROLE = "Role";
    public static final String TAG_ROLE_DESC = "Endpoints for managing roles";

    public static final String CREATE_ROLE = "Create Role";
    public static final String CREATE_ROLE_DESC = "Creates a new role with a unique name";

    public static final String UPDATE_ROLE = "Update Role";
    public static final String UPDATE_ROLE_DESC = "Updates an existing role by its ID";

    public static final String GET_ALL_ROLES = "Get All Roles";
    public static final String GET_ALL_ROLES_DESC = "Returns a paginated list of all roles";

    public static final String GET_ROLE_BY_ID = "Get Role By ID";
    public static final String GET_ROLE_BY_ID_DESC = "Returns a single role by its numeric ID";

    public static final String GET_ROLE_BY_NAME = "Get Role By Name";
    public static final String GET_ROLE_BY_NAME_DESC = "Returns a single role by its name";

    public static final String DELETE_ROLE = "Delete Role";
    public static final String DELETE_ROLE_DESC = "Deletes a role by its ID";

    //========================= User Tag =========================

    public static final String TAG_USER = "User";
    public static final String TAG_USER_DESC = "Endpoints for managing users";

    public static final String CREATE_USER = "Register User";
    public static final String CREATE_USER_DESC = "Registers a new user with default USER role";

    public static final String UPDATE_USER = "Update User";
    public static final String UPDATE_USER_DESC = "Updates username, email, fullName and profile info";

    public static final String GET_USER_BY_ID = "Get User By ID";
    public static final String GET_USER_BY_ID_DESC = "Returns a single active user by their ID";

    public static final String GET_USER_BY_IDENTIFIER = "Get User By Username or Email";
    public static final String GET_USER_BY_IDENTIFIER_DESC = "Finds an active user by username or email";

    public static final String GET_ALL_USERS = "Get All Users";
    public static final String GET_ALL_USERS_DESC = "Returns a paginated list of all users";

    public static final String DELETE_USER = "Deactivate User";
    public static final String DELETE_USER_DESC = "Soft-deletes a user by setting active = false";

    public static final String ACTIVATE_USER = "Activate User";
    public static final String ACTIVATE_USER_DESC = "Reactivates a previously deactivated user";

    public static final String SEARCH_USERS = "Search Users";
    public static final String SEARCH_USERS_DESC = "Searches users by username, email, or full name";

    public static final String GET_USERS_BY_ROLE = "Get Users By Role";
    public static final String GET_USERS_BY_ROLE_DESC = "Returns paginated users filtered by role ID";

    public static final String UPDATE_PROFILE_PICTURE = "Update Profile Picture";
    public static final String UPDATE_PROFILE_PICTURE_DESC = "Uploads and sets a new profile picture via Cloudinary";

    public static final String GET_DEACTIVATED_USERS = "Get Deactivated Users";
    public static final String GET_DEACTIVATED_USERS_DESC = "Returns a paginated list of all deactivated users";

    //========================= QR Code Tag =========================

    public static final String TAG_QR_CODE = "QR Code";
    public static final String TAG_QR_CODE_DESC = "Endpoints for generating, retrieving, and deleting QR codes";

    public static final String GENERATE_QR_CODE = "Generate QR Code";
    public static final String GENERATE_QR_CODE_DESC = "Generates a QR code image for the specified URL ID";

    public static final String GET_QR_CODE = "Get QR Code";
    public static final String GET_QR_CODE_DESC = "Retrieves the QR code associated with the specified URL ID";

    public static final String DELETE_QR_CODE = "Delete QR Code";
    public static final String DELETE_QR_CODE_DESC = "Deletes the QR code associated with the specified URL ID";

    public static final String QR_CODE_GENERATED = "QR code generated successfully";
    public static final String QR_CODE_RETRIEVED = "QR code retrieved successfully";
    public static final String QR_CODE_DELETED = "QR code deleted successfully";

    //========================= Blocked URL Tag =========================

    public static final String TAG_BLOCKED_URL = "Blocked URL";
    public static final String TAG_BLOCKED_URL_DESC = "Endpoints for managing blocked domains (admin only)";

    public static final String BLOCK_DOMAIN = "Block Domain";
    public static final String BLOCK_DOMAIN_DESC = "Blocks a domain to prevent short URL creation for it";

    public static final String UPDATE_BLOCKED_URL = "Update Blocked URL";
    public static final String UPDATE_BLOCKED_URL_DESC = "Updates the reason for a blocked domain entry";

    public static final String GET_BLOCKED_URL_BY_ID = "Get Blocked URL By ID";
    public static final String GET_BLOCKED_URL_BY_ID_DESC = "Returns a blocked URL entry by its ID";

    public static final String GET_ALL_BLOCKED_URLS = "Get All Blocked URLs";
    public static final String GET_ALL_BLOCKED_URLS_DESC = "Returns a paginated list of all blocked domains";

    public static final String CHECK_DOMAIN_BLOCKED = "Check If Domain Is Blocked";
    public static final String CHECK_DOMAIN_BLOCKED_DESC = "Checks whether a given domain is in the blocked list";

    public static final String UNBLOCK_DOMAIN = "Unblock Domain";
    public static final String UNBLOCK_DOMAIN_DESC = "Removes a domain from the blocked list by its ID";

}
