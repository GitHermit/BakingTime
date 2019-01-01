package com.example.android.bakingtime.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.bakingtime";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    public static final String PATH_INGREDIENTS = "Ingredients";


    public static final class RecipeEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String TABLE_NAME = "Recipe";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_MEASUREMENT = "Measurement";
        public static final String COLUMN_INGREDIENT = "Ingredient";
    }
}
