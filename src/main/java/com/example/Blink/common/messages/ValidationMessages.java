package com.example.Blink.common.messages;

public class ValidationMessages {

    //========================= URL =========================

    public static final String URL_NOT_BLANK = "Original URL cannot be blank";
    public static final String TITLE_NOT_BLANK = "Title cannot be blank";
    public static final String URL_FORMAT_INVALID = "Must be a valid URL";
    public static final String URL_TOO_LONG = "URL must not exceed 2048 characters";
    public static final String ALIAS_TOO_LONG = "Custom alias must not exceed 100 characters";
    public static final String EXPIRE_DATE_FUTURE = "Expiry date must be in the future";


    //========================= Block URL =========================

    public static final String DOMAIN_NOT_BLANK = "Domain cannot be blank";
    public static final String REASON_NOT_BLANK = "Reason cannot be blank";

    //========================= Password =========================

    public static final String PASSWORD_NOT_BLANK = "Password cannot be blank";
    public static final String CURRENT_PASSWORD_NOT_BLANK = "Password cannot be blank";

    //========================= Role =========================

    public static final String ROLE_NAME_NOT_BLANK = "Role name must not be blank";

    //========================= Auth =========================

    public static final String USERNAME_OR_EMAIL_REQUIRED = "Username or email is required";
    public static final String REFRESH_TOKEN_REQUIRED = "Refresh token is required";

    //========================= User =========================

    public static final String USERNAME_NOT_BLANK = "Username is required";
    public static final String USERNAME_SIZE = "Username must be between 6 and 50 characters";
    public static final String EMAIL_NOT_BLANK = "Email is required";
    public static final String EMAIL_INVALID = "Email should be valid";
    public static final String EMAIL_TOO_LONG = "Email must be less than 100 characters";
    public static final String FULL_NAME_NOT_BLANK = "Full name is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_MIN_SIZE = "Password must be at least 8 characters long";
    public static final String CURRENT_PASSWORD_REQUIRED = "Current password is required";
    public static final String NEW_PASSWORD_REQUIRED = "New password is required";
    public static final String NEW_PASSWORD_MIN_SIZE = "New password must be at least 8 characters long";



}
