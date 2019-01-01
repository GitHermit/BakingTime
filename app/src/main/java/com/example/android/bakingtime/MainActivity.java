package com.example.android.bakingtime;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.Model.BakingRecipe;
import com.example.android.bakingtime.Utilities.NetworkUtilities;
import com.example.android.bakingtime.Utilities.ParseUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final static String recipeUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public static final String EXTRA_POSITION = "extra_position";
    static final String LOG_TAG = MainActivity.class.getSimpleName();
    public ArrayList<BakingRecipe> bakingRecipes = new ArrayList<>();
    private BakingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    int size;


    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ButterKnife.bind(this);
        mLayoutManager = new LinearLayoutManager(this);
        if (findViewById(R.id.main_tablet) != null){
            mLayoutManager = new GridLayoutManager(this,3 );
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int totalWidth = parent.getWidth();
                    int maxCardWidth = getResources().getDimensionPixelOffset(R.dimen.max_card_width);
                    int sidePadding = (totalWidth - maxCardWidth) / 4;
                    sidePadding = Math.max(0, sidePadding);
                    outRect.set( 60, 0, 0, 12);
                }
            });
        }
        else {
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int totalWidth = parent.getWidth();
                    int maxCardWidth = getResources().getDimensionPixelOffset(R.dimen.max_card_width);
                    int sidePadding = (totalWidth - maxCardWidth) / 2;
                    sidePadding = Math.max(0, sidePadding);
                    outRect.set( sidePadding, 0, sidePadding, 0);
                }
            });
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        findRecipe(recipeUrl);

    }


    public class RecipeTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null && !results.equals("")) {
                JSONArray jsonArray = ParseUtility.parseRecipeJson(results);

                for (int i = 0; i < jsonArray.length(); i++){
                    separateJSONData(jsonArray, i);
                }
                size = jsonArray.length();
                createAdapter(size);

            }
        }
    }


    public URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "URL error", exception);
            return null;
        }
        return url;
    }

    public void findRecipe(String pickedUrl) {
        URL url = createUrl(pickedUrl);
        new RecipeTask().execute(url);
    }

    public void createAdapter(int size) {
        mAdapter = new BakingAdapter(size,bakingRecipes);
        mAdapter.setmContext(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void clicked(View view){
        int position = mRecyclerView.getChildAdapterPosition(view);
        launchDetailActivity(position);
    }

    public void separateJSONData(JSONArray array, int k) {


        ArrayList<Double> quantityList = new ArrayList<>();
        ArrayList<String> measureList = new ArrayList<>();
        ArrayList<String> ingredientList = new ArrayList<>();
        ArrayList<String> shortDescriptionList = new ArrayList<>();
        ArrayList<String> descriptionList = new ArrayList<>();
        ArrayList<String> videoURLList = new ArrayList<>();
        ArrayList<String> thumbnailURLList = new ArrayList<>();


        try {
                JSONObject individualRecipe = array.getJSONObject(k);
                String name = individualRecipe.getString("name");
                JSONArray ingredientsArray = individualRecipe.getJSONArray("ingredients");
                int ingredientsSize = ingredientsArray.length();

                for (int j = 0; j < ingredientsSize; j++) {
                    JSONObject ingredientsSorter = ingredientsArray.getJSONObject(j);
                    Double quantity = ingredientsSorter.getDouble("quantity");
                    String measure = ingredientsSorter.getString("measure");
                    String ingredient = ingredientsSorter.getString("ingredient");
                    quantityList.add(quantity);
                    measureList.add(measure);
                    ingredientList.add(ingredient);
                }


                JSONArray stepsArray = individualRecipe.getJSONArray("steps");
                int stepsSize = stepsArray.length();

                for (int j = 0; j < stepsSize; j++) {
                    JSONObject stepsSorter = stepsArray.getJSONObject(j);
                    String shortDescription = stepsSorter.getString("shortDescription");
                    String description = stepsSorter.getString("description");
                    String videoURL = stepsSorter.getString("videoURL");
                    String thumbnailURL = stepsSorter.getString("thumbnailURL");
                    shortDescriptionList.add(shortDescription);
                    descriptionList.add(description);
                    videoURLList.add(videoURL);
                    thumbnailURLList.add(thumbnailURL);
                }

                int servings = individualRecipe.getInt("servings");
                String image = individualRecipe.getString("image");


                BakingRecipe newRecipe = new BakingRecipe(name,quantityList,measureList,ingredientList,shortDescriptionList,descriptionList,videoURLList,thumbnailURLList,servings,image);
                int size = bakingRecipes.size();
                bakingRecipes.add(size, newRecipe);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void launchDetailActivity(int position) {

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.EXTRA_POSITION, position);
        intent.putExtra("BakingRecipe", bakingRecipes.get(position));
        startActivity(intent);
    }


}
