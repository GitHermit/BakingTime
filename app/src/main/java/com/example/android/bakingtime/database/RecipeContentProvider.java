package com.example.android.bakingtime.database;

import android.arch.persistence.room.RoomDatabase;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakingtime.Model.BakingRecipe;

public class RecipeContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.android.bakingtime";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_INGREDIENTS = "Ingredients";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = RecipeContentProvider.class.getName();

    public static final int RECIPE = 100;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_INGREDIENTS , RECIPE);

        return uriMatcher;
    }
    private AppDatabase mDb;



    @Override
    public boolean onCreate() {

        Context context = getContext();
        mDb = AppDatabase.getsInstance(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        if (RECIPE == match){
            returnCursor = mDb.recipeDao().singleListRecipe();
        }
        else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }



        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
