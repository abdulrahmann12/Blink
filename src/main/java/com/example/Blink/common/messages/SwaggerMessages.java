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

}
