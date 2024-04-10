package com.qb.hotelTV.Setting;

public class ApiSetting {

//    请求地区和天气用的key
    public static String KEY = "ef1c589fa744404f95c91aa5f87ddc42";

    public static String URL_GEO = "https://geoapi.qweather.com/v2/city/lookup?";
    public static String URL_WEATHER = "https://devapi.qweather.com/v7/weather/now?";

    public static String SERVER = "http://113.106.109.130:23000";


    public static String URL_GET_ROOM_MESSAGE = SERVER + "/admin-api/hotel/room/getNumber";

}
