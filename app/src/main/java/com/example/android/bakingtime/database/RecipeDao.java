package com.example.android.bakingtime.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.android.bakingtime.Model.BakingRecipe;

import java.util.List;


@Dao
public interface RecipeDao {

    @Query("SELECT * FROM Ingredients ORDER BY tableId")
    LiveData<List<BakingRecipe>> loadOnlyRecipe();

    @Query("SELECT * FROM Ingredients")
    Cursor singleListRecipe();

    @Insert
    void insertRecipe(BakingRecipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(BakingRecipe recipe);

    @Delete
    void deleteRecipe(BakingRecipe recipe);

    @Query("SELECT tableId FROM Ingredients")
    int getOnlyId();

    @Query("SELECT Recipe FROM Ingredients WHERE tableId = :id")
    String getName(int id);


    @Query("SELECT tableId FROM Ingredients WHERE Recipe = :name")
    int getId(String name);


}
