package com.mutualmobile.cardstack;

/**
 * Init by Tushar Acharya on 10/9/15.
 */
public class Config {
    public static final boolean IS_RELEASE = BuildConfig.BUILD_TYPE.equals("release");
    public static final boolean IS_DEBUG = BuildConfig.BUILD_TYPE.equals("debug");

    public static final boolean LOG_DETAILED = !IS_RELEASE;

    public static final int LOG_LEVEL_NONE = 0x00000000;
    public static final int LOG_LEVEL_ERROR = 0x00000001;
    public static final int LOG_LEVEL_DEBUG = 0x00000010;
    public static final int LOG_LEVEL_ALL = 0x00000011;

    public static final int LOG_LEVEL = IS_RELEASE ? LOG_LEVEL_NONE : LOG_LEVEL_ALL;

}
