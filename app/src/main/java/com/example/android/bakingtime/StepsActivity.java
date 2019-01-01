package com.example.android.bakingtime;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.bakingtime.Model.BakingRecipe;
import com.example.android.bakingtime.database.AppDatabase;
import com.example.android.bakingtime.database.RecipeContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StepsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private static final String POSITION = "Position";
    private BakingRecipe recipe;
    public static boolean duplicateFound;
    private int position;
    private boolean empty;
    private AppDatabase mDb;

    @BindView(R.id.next_button)
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_steps);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        if (savedInstanceState == null) {
            position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        }
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(POSITION);
        }
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
        }
        recipe = intent.getParcelableExtra("recipe");

        ButterKnife.bind(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == recipe.getShortDescription().size()) {
            button.setVisibility(GONE);
        }


        if (position == 0) {
            List<Double> quantity = recipe.getQuantity();
            List<String> measure = recipe.getMeasure();
            List<String> ingredients = recipe.getIngredient();

            IngredientListFragment ingredientListFragment = new IngredientListFragment();
            ingredientListFragment.setIngredients(ingredients);
            ingredientListFragment.setMeasurements(measure);
            ingredientListFragment.setQuantity(quantity);
            ingredientListFragment.setRecipe(recipe);
            fragmentManager.beginTransaction().add(R.id.activity_container, ingredientListFragment).commit();
            setupWidgetText();

        } else {
            String description = recipe.getDescription().get(position - 1);
            String videoURL = recipe.getVideoURL().get(position - 1);
            String thumbnailURL = recipe.getThumbnailURL().get(position - 1);
            BakingRecipeFragment bakingFragment = new BakingRecipeFragment();

            if (thumbnailURL.contains(".mp4") && !videoURL.contains(".mp4")) {
                videoURL = thumbnailURL;
            }
            if (videoURL.contains(".mp4")) {
                bakingFragment.setVideoURL(videoURL);
            }

            if (!description.isEmpty()) {
                bakingFragment.setDescription(description);
                fragmentManager.beginTransaction().replace(R.id.activity_container, bakingFragment).commit();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (position == 0) {
            getMenuInflater().inflate(R.menu.ingredients, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.ingredients_option) {
            WidgetOptionClicked(recipe);
        }
        return true;
    }

    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt(POSITION, position);
    }


    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    public void nextStep(View view) {
        BakingRecipeFragment bakingFragment = new BakingRecipeFragment();
        position = position + 1;
        if (position == recipe.getDescription().size()) {
            button.setVisibility(GONE);
        }
        String description = recipe.getDescription().get(position - 1);
        String videoURL = recipe.getVideoURL().get(position - 1);
        String thumbnailURL = recipe.getThumbnailURL().get(position - 1);

        if (thumbnailURL.contains(".mp4") && !videoURL.contains(".mp4")) {
            videoURL = thumbnailURL;
        }
        if (videoURL.contains(".mp4")) {

            bakingFragment.setVideoURL(videoURL);
        }

        if (!description.isEmpty()) {
            bakingFragment.setDescription(description);
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_container, bakingFragment).commit();
        }
    }

    public void WidgetOptionClicked(BakingRecipe currentRecipe) {

        final String recipeName = currentRecipe.getName();
        final List<Double> quantity = currentRecipe.getQuantity();
        final List<String> measurement = currentRecipe.getMeasure();
        final List<String> ingredient = currentRecipe.getIngredient();
        final BakingRecipe recipe = new BakingRecipe(recipeName, quantity, measurement, ingredient);
        mDb = AppDatabase.getsInstance(getApplicationContext());


        AppExecutors.getInstance().diskIo().execute(new Runnable() {
            @Override
            public void run() {

                if (!duplicateFound && empty) {
                    mDb.recipeDao().insertRecipe(recipe);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(StepsActivity.this, R.string.added_to_widget, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (!duplicateFound && !empty){
                mDb.recipeDao().loadOnlyRecipe();
                int id = mDb.recipeDao().getOnlyId();
                String deleteName = mDb.recipeDao().getName(id);
                BakingRecipe deleteRecipe = new BakingRecipe(id, deleteName);
                mDb.recipeDao().deleteRecipe(deleteRecipe);
                mDb.recipeDao().insertRecipe(recipe);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(StepsActivity.this, R.string.updated_to_widget, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
                else {

                    int id = mDb.recipeDao().getId(recipeName);
                    mDb.recipeDao().loadOnlyRecipe();
                    BakingRecipe deleteRecipe = new BakingRecipe(id, recipeName);
                    mDb.recipeDao().deleteRecipe(deleteRecipe);
                    duplicateFound = false;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(StepsActivity.this, R.string.removed_from_widget, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_QUANTITY,recipe.getQuantity().get(0));
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_MEASUREMENT,recipe.getMeasure().get(0));
        contentValues.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENT,recipe.getIngredient().get(0));
        getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
        RecipeUpdateService.startActionUpdateRecipeWidgets(this);

    }

    public void setupWidgetText() {


        DatabaseViewModel viewModel = ViewModelProviders.of(this).get(DatabaseViewModel.class);
        viewModel.getRecipe().observe(this, new Observer<List<BakingRecipe>>() {
            @Override
            public void onChanged(@Nullable List<BakingRecipe> savedRecipe) {
                if (!savedRecipe.isEmpty()) {
                    empty = false;
                        if (recipe.getName().equals(savedRecipe.get(0).getName())){
                            invalidateOptionsMenu();
                            duplicateFound = true;
                        }
                        else {
                            duplicateFound = false;
                        }
                }else {
                    invalidateOptionsMenu();
                    empty = true;
                    duplicateFound = false;
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (position == 0) {
            if (duplicateFound) {
                menu.findItem(R.id.ingredients_option).setTitle(R.string.widget_removal);
            } else {
                if (empty){
                    menu.findItem(R.id.ingredients_option).setTitle(R.string.add_widget);
                } else{
                    menu.findItem(R.id.ingredients_option).setTitle(R.string.widget_replacement);
                }

            }

            return super.onPrepareOptionsMenu(menu);
        }
    return false;}
}
