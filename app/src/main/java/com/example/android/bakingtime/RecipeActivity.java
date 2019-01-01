package com.example.android.bakingtime;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.Model.BakingRecipe;
import com.example.android.bakingtime.database.AppDatabase;
import com.example.android.bakingtime.database.RecipeContract;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements MasterListFragment.OnStepClickListener{

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private static final String INGREDIENTS =  "ingredients";
    private static final String BAKINGSTEP = "baking_steps";
    public static boolean duplicateFound;
    private  Fragment ingredientFragment;
    private static BakingRecipe recipe;
    private static int secondPanePosition;
    private Fragment bakingSteps;
    private AppDatabase mDb;
    private boolean empty;


    private BakingRecipeFragment bakingFragment  = new BakingRecipeFragment();
    private IngredientListFragment ingredientListFragment = new IngredientListFragment();


    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
        }
        recipe = intent.getParcelableExtra("BakingRecipe");
        String name = recipe.getName();
        setTitle(name);
        setContentView(R.layout.activity_recipe);

        if (findViewById(R.id.baking_time_steps_linear_view) != null) {
            mTwoPane = true;


            if (savedInstanceState == null) {
                secondPanePosition = 0;
                FragmentManager fragmentManager = getSupportFragmentManager();
                ingredientListFragment.setQuantity(recipe.getQuantity());
                ingredientListFragment.setMeasurements(recipe.getMeasure());
                ingredientListFragment.setIngredients(recipe.getIngredient());
                ingredientListFragment.setRecipe(recipe);

                fragmentManager.beginTransaction().add(R.id.activity_container, ingredientListFragment, INGREDIENTS)
                        .commit();
                getSupportFragmentManager().executePendingTransactions();
                setupWidgetText();
            }
        }
        else {
            mTwoPane = false;
            secondPanePosition = 1;
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    public static BakingRecipe getRecipe() {
        return recipe;
    }


    private void launchStepActivity(int position) {

        Intent intent = new Intent(this, StepsActivity.class);
        intent.putExtra(StepsActivity.EXTRA_POSITION, position);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }


    @Override
    public void onStepSelected(int position) {

        if (!mTwoPane){
            launchStepActivity(position);
        }
        else if (position == 0) {
            setupWidgetText();
            secondPanePosition = position;
            if (bakingSteps != null){
                getSupportFragmentManager().beginTransaction().
                        remove(bakingFragment).commit();
            }
            ingredientFragment = getSupportFragmentManager().findFragmentByTag(INGREDIENTS);
            ingredientListFragment.setQuantity(recipe.getQuantity());
            ingredientListFragment.setMeasurements(recipe.getMeasure());
            ingredientListFragment.setIngredients(recipe.getIngredient());
            ingredientListFragment.setRecipe(recipe);
            if (ingredientFragment != null) {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.activity_container, ingredientListFragment, INGREDIENTS).commit();
            } else {
                getSupportFragmentManager().beginTransaction().
                        add(R.id.activity_container, ingredientListFragment, INGREDIENTS).commit();
            }

        }
        else {
            secondPanePosition = position;
            ingredientFragment = getSupportFragmentManager().findFragmentByTag(INGREDIENTS);
            if (ingredientFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(ingredientFragment).commit();
            }

            getSupportFragmentManager().executePendingTransactions();


            bakingFragment.setDescription(recipe.getDescription().get(position - 1));
            String videoURL = recipe.getVideoURL().get(position-1);
            String thumbnailURL = recipe.getThumbnailURL().get(position-1);


            if(thumbnailURL.contains(".mp4") && !videoURL.contains(".mp4")){
                videoURL = thumbnailURL;
            }

            bakingFragment.setImageURL(thumbnailURL);
            bakingFragment.setVideoURL(videoURL);
            getSupportFragmentManager().executePendingTransactions();
            bakingSteps = getSupportFragmentManager().findFragmentByTag(BAKINGSTEP);
            if (bakingSteps == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.activity_container, bakingFragment, BAKINGSTEP).commit();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_container, bakingFragment, BAKINGSTEP).commit();
            }
            getSupportFragmentManager().executePendingTransactions();
            bakingSteps = getSupportFragmentManager().findFragmentByTag(BAKINGSTEP);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(bakingSteps);

            fragmentTransaction.attach(bakingSteps);
            fragmentTransaction.commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (secondPanePosition == 0 && mTwoPane) {
            getMenuInflater().inflate(R.menu.ingredients, menu);
        }
        return true;
    }
    public void WidgetOptionClicked(BakingRecipe currentRecipe) {

        final String recipeName = currentRecipe.getName();
        List<Double> quantity = currentRecipe.getQuantity();
        final List<String> measurement = currentRecipe.getMeasure();
        final List<String> ingredient = currentRecipe.getIngredient();
        final BakingRecipe recipe = new BakingRecipe(recipeName, quantity, measurement, ingredient);
        mDb = AppDatabase.getsInstance(getApplicationContext());


        AppExecutors.getInstance().diskIo().execute(new Runnable() {
            @Override
            public void run() {

                if (!duplicateFound && empty ) {
                    mDb.recipeDao().insertRecipe(recipe);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(RecipeActivity.this, R.string.added_to_widget, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RecipeActivity.this, R.string.updated_to_widget, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RecipeActivity.this, R.string.removed_from_widget, Toast.LENGTH_SHORT).show();
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
                    empty = true;
                    invalidateOptionsMenu();
                    duplicateFound = false;
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (secondPanePosition == 0) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.ingredients_option) {
            WidgetOptionClicked(recipe);
        }
        return true;
    }


}
