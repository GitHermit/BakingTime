package com.example.android.bakingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bakingtime.Model.BakingRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientListFragment extends Fragment {

    public static final String INGREDIENTS = "ingredients";
    public static final String MEASURE = "measurements";
    public static final String RECIPE = "recipe";
    public static final String POSITION = "position";

    private List<String> mIngredients;
    private List<String> mMeasurements;
    private List<Double> mQuantity;

    private Context mContext;
    private BakingRecipe mRecipe;
    private int mPosition = 0;

    public IngredientListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.ingredients_option);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(POSITION);
                mRecipe = savedInstanceState.getParcelable(RECIPE);
                mIngredients = savedInstanceState.getStringArrayList(INGREDIENTS);
                mQuantity = mRecipe.getQuantity();
                mMeasurements = savedInstanceState.getStringArrayList(MEASURE);


        }

        View rootView = inflater.inflate(R.layout.fragment_ingredients_list, container, false);

        ListView listView = rootView.findViewById(R.id.ingredients_list_view_fragment);

        mContext = getActivity();

        IngredientListAdapter adapter = new IngredientListAdapter(mContext, mIngredients, mMeasurements, mQuantity);
        listView.setAdapter(adapter);





            return rootView;
    }

    public void setIngredients(List<String> ingredients) { mIngredients = ingredients;}

    public void setMeasurements(List<String> measurements) {mMeasurements = measurements;}

    public void setQuantity(List<Double> quantity) {mQuantity = quantity;}


    public void setRecipe(BakingRecipe recipe) {
        mRecipe = recipe;

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelable(RECIPE, mRecipe);
        outState.putStringArrayList(INGREDIENTS, (ArrayList<String>) mIngredients);
        outState.putStringArrayList(MEASURE, (ArrayList<String>) mMeasurements);
        outState.putInt(POSITION, mPosition);
    }
}
