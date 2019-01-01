package com.example.android.bakingtime.Model;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DatabaseTypeConverter {

        static Gson gson = new Gson();

        @TypeConverter
        public static List<Double> stringToQuantityList(String data) {
            if (data == null) {
                return Collections.emptyList();
            }

            Type listType = new TypeToken<List<Double>>() {}.getType();

            return gson.fromJson(data, listType);
        }

        @TypeConverter
        public static String quantityListToString(List<Double> someObjects) {
            return gson.toJson(someObjects);
        }

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String StringListToString(List<String> someObjects) {
        return gson.toJson(someObjects);
    }
    }
