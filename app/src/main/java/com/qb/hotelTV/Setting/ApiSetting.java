package com.qb.hotelTV.Setting;

public class ApiSetting {

//    LocationHttp
    public static String KEY = "ef1c589fa744404f95c91aa5f87ddc42";

    public static String URL_GEO = "https://geoapi.qweather.com/v2/city/lookup?";
    public static String URL_WEATHER = "https://devapi.qweather.com/v7/weather/now?";



//    BackstageHttp
    private String SERVER = "http://113.106.109.130:23000";
    private String QA = "http://192.168.2.85:48080";
    public static String PAGE_NO = "1";
    public static String PAGE_SIZE = "100";

    public static String PASSWORD = "Ug1d2orQ";

//    public static String PREFIX = "/prod-api";
    public static String PREFIX = "";


//    登录
    public static String LOGIN_API = PREFIX + "/admin-api/system/auth/login";


//    获取信息的接口
    public static String URL_GET_ROOM_MESSAGE = PREFIX + "/admin-api/hotel/room/getNumber";
//    获取apk和播放列表的接口  {"pageNo": 1,  "pageSize": 100}
    public static String URL_GET_APK = PREFIX + "/admin-api/hotel/apk-config/page";
    public static String URL_GET_VIDEO = PREFIX + "/admin-api/hotel/playback/page";

    public static String URL_GET_HOTEL_MESSAGE =PREFIX +  "/admin-api/hotel/info/get";

    public static String URL_GET_TV_TEXT = PREFIX + "/admin-api/system/notice/page";

    public static String URL_GET_TV_CHANNEL = PREFIX + "/admin-api/hotel/playback/page";

    public static String URL_GET_HOTEL_LIST = PREFIX + "/admin-api/cms/article-category/page";


    public static String URL_GET_CMS_MESSAGE = PREFIX + "/admin-api/cms/article/page";

    public static String URL_GET_APP_VERSION = PREFIX + "";
}
