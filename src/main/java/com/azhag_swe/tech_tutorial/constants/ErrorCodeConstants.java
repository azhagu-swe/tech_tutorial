package com.azhag_swe.tech_tutorial.constants;

public class ErrorCodeConstants {
    public static final String USERNAME_TAKEN = "AUTH004";
    public static final String EMAIL_IN_USE = "AUTH005";

    // Role error codes
    public static final String ROLE_NOT_FOUND = "ROLE_001";
    public static final String ROLE_CREATE_ERROR = "ROLE_002";
    public static final String ROLE_UPDATE_ERROR = "ROLE_003";
    public static final String ROLE_DELETE_ERROR = "ROLE_004";

    // User error codes
    public static final String USER_NOT_FOUND = "USER_001";
    public static final String USER_CREATION_ERROR = "USER_002";
    public static final String USER_UPDATE_ERROR = "USER_003";
    public static final String USER_DELETE_ERROR = "USER_004";

    // Permission error codes
    public static final String PERMISSION_NOT_FOUND = "PERMISSION_001";
    public static final String PERMISSION_CREATE_ERROR = "PERMISSION_002";
    public static final String PERMISSION_UPDATE_ERROR = "PERMISSION_003";
    public static final String PERMISSION_DELETE_ERROR = "PERMISSION_004";
}