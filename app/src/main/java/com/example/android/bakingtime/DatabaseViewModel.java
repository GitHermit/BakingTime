package com.example.android.bakingtime;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Movie;
import android.support.annotation.NonNull;

import com.example.android.bakingtime.Model.BakingRecipe;
import com.example.android.bakingtime.database.AppDatabase;


import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private LiveData<List<BakingRecipe>> recipe;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        recipe = database.recipeDao().loadOnlyRecipe();
    }

    public LiveData<List<BakingRecipe>> getRecipe() {
        return recipe;
    }
}
