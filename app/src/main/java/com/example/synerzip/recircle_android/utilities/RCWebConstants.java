package com.example.synerzip.recircle_android.utilities;

import retrofit2.http.PUT;

/**
 * Created by Prajakta Patil on 7/3/17.
 * Copyright © 2016 Synerzip. All rights reserved
 */
public class RCWebConstants {

    public static final String RC_SHARED_PREFERENCES_ACCESS_TOKEN="accessToken";

    public static final String RC_SHARED_PREFERENCES_LOGIN_FIRST_USERNAME="firstname";

    public static final String RC_SHARED_PREFERENCES_LOGIN_USERNAME="username";

    public static final String RC_SHARED_PREFERENCES_PASSWORD="password";

    public static final String RC_SHARED_PREFERENCES_LOGIN_STATUS="isLoggedIn";

    public static final String RC_SHARED_PREFERENCES_USERID="userId";

    public static final String RC_SHARED_PREFERENCES_FILE_NAME="recircle";

    public static final int RC_SUCCESS_CODE = 200;

    public static final int RC_ERROR_CODE_BAD_REQUEST = 400;

    public static final int RC_ERROR_CODE_FORBIDDEN = 403;

    public static final int RC_ERROR_CODE_NOT_FOUND = 404;

    public static final int RC_ERROR_CODE_CONFLICT = 409;

    public static final int RC_ERROR_CODE_INTERNAL_SERVER_ERROR = 500;

    public static final int RC_ERROR_CODE_SERVICE_UNAVAILABLE = 503;

    public static final String RC_GET_PRODUCT_DETAILS = "/api/products";

    public static final String RC_SEARCH_PRODUCT = "/api/products/searchProd?city=austin";

    public static final String RC_PRODUCT_NAMES = "/api/products/prodNames";

    public static final String RC_PRODUCT_DETAILS = "/api/products/{userProductId}";

    public static final String RC_USER_SIGN_UP="/api/users";

    public static final String RC_USER_LOG_IN="/api/users/signin";

    public static final String RC_FORGOT_PWD ="/api/users/forgotPwd";

    public static final String RC_GET_OTP="/api/users/otp?";

    public static final int RC_ERROR_UNAUTHORISED = 401;

    public static final String RC_GOOGLE_ZIPCODES="https://maps.googleapis.com/maps/api/geocode/json?";


}
