package com.example.android.bakingtime.Model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Entity(tableName = "Ingredients")
public class BakingRecipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int tableId;
    @ColumnInfo(name = "Recipe")
    private String name;
    @TypeConverters(DatabaseTypeConverter.class)
    @ColumnInfo(name = "Quantity")
    private List<Double> quantity = new ArrayList<>();
    @TypeConverters(DatabaseTypeConverter.class)
    @ColumnInfo(name = "Measurement")
    private List<String> measure = new ArrayList<>();
    @TypeConverters(DatabaseTypeConverter.class)
    @ColumnInfo(name = "Ingredient")
    private List<String> ingredient = new ArrayList<>();
    @Ignore
    private List<String> shortDescription = new ArrayList<>();
    @Ignore
    private List<String> description = new ArrayList<>();
    @Ignore
    private List<String> videoURL = new ArrayList<>();
    @Ignore
    private List<String> thumbnailURL = new ArrayList<>();
    @Ignore
    private int servings;
    @Ignore
    private String image;

    @Ignore
    public BakingRecipe() {
    }

    @Ignore
    public BakingRecipe(String name, List<Double> quantity, List<String> measure, List<String> ingredient,
                 List<String> shortDescription, List<String> description, List<String> videoURL,
                 List<String> thumbnailURL, int servings, String image) {
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.servings = servings;
        this.image = image;
    }

    public BakingRecipe(String name,List<Double> quantity, List<String> measure, List<String> ingredient){
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Ignore
    public BakingRecipe(int tableId, String name){
        this.tableId = tableId;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeList(quantity);
        dest.writeList(measure);
        dest.writeList(ingredient);
        dest.writeList(shortDescription);
        dest.writeList(description);
        dest.writeList(videoURL);
        dest.writeList(thumbnailURL);
        dest.writeInt(servings);
        dest.writeString(image);

    }

    //constructor used for parcel
    public BakingRecipe(Parcel parcel){
        name = parcel.readString();
        quantity = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        measure = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        ingredient = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        shortDescription  = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        description  = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        videoURL  = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        thumbnailURL  = parcel.readArrayList(ClassLoader.getSystemClassLoader());
        servings = parcel.readInt();
        image = parcel.readString();
    }

    public static final Parcelable.Creator<BakingRecipe> CREATOR = new Parcelable.Creator<BakingRecipe>(){

        @Override
        public BakingRecipe createFromParcel(Parcel parcel) {
            return new BakingRecipe(parcel);
        }

        @Override
        public BakingRecipe[] newArray(int size) {
            return new BakingRecipe[0];
        }
    };


    public String getName() {
        return name;
    }

    public List<Double> getQuantity() {
        return quantity;
    }

    public List<String> getMeasure() {
        return measure;
    }

    public List<String> getIngredient() {
        return ingredient;
    }


    public List<String> getShortDescription() {
        return shortDescription;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getVideoURL() {
        return videoURL;
    }

    public List<String> getThumbnailURL() {
        return thumbnailURL;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public int getTableId(){return tableId;}

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(List<Double> quantity) {
        this.quantity = quantity;
    }

    public void setMeasure(List<String> measure) {
        this.measure = measure;
    }

    public void setIngredient(List<String> ingredient) {
        this.ingredient = ingredient;
    }

    public void setShortDescription(List<String> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public void setVideoURL(List<String> videoURL) {
        this.videoURL = videoURL;
    }

    public void setThumbnailURL(List<String> thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

}

