package com.qb.hotelTV.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GsonUtils {

    public GsonUtils() {
    }

    /**
     * 用来封装一个json数据
     *
     * @param object
     * @return
     */
    public static String createGsonString(Object object) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(object);
        return gsonString;
    }

    /**
     * 可以对任何类型bean进行解析，只限于单个对象
     *
     * @param gsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String gsonString, Type cls) {
        //替换null为空值
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new MyTypeAdapterFactory()).create();
        //异常判断
        try {
            return gson.fromJson(gsonString, cls);
        } catch (JsonSyntaxException e) {
            Log.i("GsonUtils_jsonToObject", "json装换错误：" + e.getMessage());
            return null;
        }
    }

    /**
     * 适用与json中保存的是list
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> changeGsonToList(String json, Class<T> cls) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);// 反序列化出ArrayList<JsonObject>，
        final ArrayList<T> listOfT = new ArrayList<T>();
        for (JsonObject jsonObj : jsonObjs) {
            listOfT.add(new Gson().fromJson(jsonObj, cls));
        }
        return listOfT;
    }

    /**
     * 适用于json数据的格式为list<map>
     *
     * @param gsonString
     * @param <T>
     * @return
     */
    public static <T> List<Map<String, T>> changeGsonToListMaps(
            String gsonString) {
        List<Map<String, T>> list = null;
        Gson gson = new Gson();
        list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    /**
     * 适用于json格式为map类型
     *
     * @param gsonString
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> changeGsonToMaps(String gsonString) {
        Map<String, T> map = null;
        Gson gson = new Gson();
        map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    /**
     * 处理字段中本来是int的，api传null,string,boolean
     */
    static class IntegerAdapter extends TypeAdapter<Integer> {

        @Override
        public void write(JsonWriter out, Integer value) throws IOException {

        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            if(in.peek() == JsonToken.NULL){
                in.nextNull();
                return 0;
            }
            if(in.peek() == JsonToken.STRING){
                in.nextString();
                return -1;
            }
            if(in.peek() == JsonToken.BOOLEAN){
                in.nextBoolean();
                return -1;
            }
            return in.nextInt();
        }
    }
    static class StringAdapter extends TypeAdapter<String>{
        String replace = "";
        @Override
        public void write(JsonWriter out, String value) throws IOException {

        }

        @Override
        public String read(JsonReader in) throws IOException {
            if(in.peek() == JsonToken.NULL){
                in.nextNull();
                return replace;
            }
            if(in.peek() == JsonToken.BOOLEAN){
                in.nextBoolean();
                return replace;
            }
            return in.nextString();
        }
    }
    static class MyTypeAdapterFactory implements TypeAdapterFactory{

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if(rawType == String.class)
                return (TypeAdapter<T>) new StringAdapter();
            if(rawType == int.class)
                return (TypeAdapter<T>) new IntegerAdapter();
            return null;
        }
    }


}