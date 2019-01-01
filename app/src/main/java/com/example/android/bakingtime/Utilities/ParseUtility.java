package com.example.android.bakingtime.Utilities;




import org.json.JSONArray;
import org.json.JSONException;


public class ParseUtility {


    public static JSONArray parseRecipeJson(String data){


        JSONArray recipeInfo = null;


        try {

            JSONArray recipeParser = new JSONArray(data);
            recipeInfo = recipeParser;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipeInfo;
    }

}
