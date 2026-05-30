package com.example.Blink.common.messages;

public class SwaggerMessages {

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

}
