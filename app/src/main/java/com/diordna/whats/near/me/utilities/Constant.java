package com.diordna.whats.near.me.utilities;

/**
 * Created by Nahid 002345 on 1/15/2017.
 */

public class Constant {
    public static final String KEY_PLACELIST_TYPE_ID = "placelist_type_id";
    public static final String KEY_PLACELIST_TYPE = "placelist_type";
    public static final String KEY_PLACELIST_TYPE_BRIEF = "placelist_brief";

    public static final String KEY_WEAHTER_TEMP_UNIT = "metric";

    public final static String REST_WEATHER_URL = "http://api.openweathermap.org/data/2.5/";
    public final static String REST_WEATHER_API_KEY = "c71104fae2aa2f57f54a4e9b5c086379";

    public final static String REST_GOOGLEMAP_DIRECTION_API_KEY = "AIzaSyC5azLFbTU_fKWE9VDGmtWxCqr73-5BHDk";

    public final static String SYS_WEATHER_UNIT = "metric";
    public enum FileSaveStatus {
        SUCCESS, FAILED
    }
}
