package com.example.android.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingtime.database.RecipeContentProvider.BASE_CONTENT_URI;
import static com.example.android.bakingtime.database.RecipeContentProvider.PATH_INGREDIENTS;

public class ListWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}
 class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Gson gson = new Gson();
    TypeToken<List<String>> token = new TypeToken<List<String>>() {};
    TypeToken<List<Double>> doubleToken = new TypeToken<List<Double>>() {};
    private ArrayList<String> ingredientList;
    private ArrayList<String>measurementList;
    private ArrayList<Double>quantityList;
    Context mContext;
    Cursor mCursor;

    public ListRemoteViewsFactory(Context applicationContext) {

        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

        Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(RECIPE_URI,
                null,
                null,
                null,
                null);
        if (mCursor != null ){
                mCursor.moveToFirst();
                try{
                    int nameIndex = mCursor.getColumnIndex("Recipe");
                    int quantityIndex = mCursor.getColumnIndex("Quantity");
                    int measurementIndex = mCursor.getColumnIndex("Measurement");
                    int ingredientIndex = mCursor.getColumnIndex("Ingredient");
                    String name = mCursor.getString(nameIndex);
                    String quantity = mCursor.getString(quantityIndex);
                    String measurement = mCursor.getString(measurementIndex);
                    String ingredients = mCursor.getString(ingredientIndex);
                    measurementList = gson.fromJson(measurement, token.getType());
                    ingredientList = gson.fromJson(ingredients, token.getType());
                    quantityList = gson.fromJson(quantity, doubleToken.getType());
                }catch (CursorIndexOutOfBoundsException e){
                    Log.d("CursorException", "Caught Cursor Exception" );
                    if (quantityList != null){
                        quantityList.clear();
                        measurementList.clear();
                        ingredientList.clear();
                    }

                }


        }

    }

    @Override
    public void onDestroy() {
        if (mCursor!= null)
        mCursor.close();

    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        if (measurementList != null){
            return measurementList.size();
        }
        else {
            return 1;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        String row = ingredientList.get(position) + ": " +  quantityList.get(position) + " "  +  measurementList.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        views.setTextViewText(R.id.item, row);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
