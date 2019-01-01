package com.example.android.bakingtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.Model.BakingRecipe;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;


public class BakingAdapter extends RecyclerView.Adapter<BakingAdapter.CardViewHolder> {

    private int mNumberRecipes;
    private ArrayList<BakingRecipe> mRecipes;
    private Context mContext;

    public BakingAdapter(int numberOfRecipes, ArrayList<BakingRecipe> recipes){
        mNumberRecipes = numberOfRecipes;
        mRecipes = recipes;
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForRecipes = R.layout.baking_list_card;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForRecipes, parent, false);
        CardViewHolder viewHolder = new CardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mNumberRecipes;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
         TextView listRecipeName;
         ImageView listRecipeImage;

        public CardViewHolder(View itemView) {
            super(itemView);

            listRecipeImage = itemView.findViewById(R.id.recipe_name_iv);
            listRecipeName = (TextView) itemView.findViewById(R.id.recipe_name_tv);




        }

        void bind(int position) {
            listRecipeName.setText(mRecipes.get(position).getName());
            String image = mRecipes.get(position).getImage();
            if (image.contains(".png") || image.contains(".jpeg")){
                listRecipeImage.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.with(mContext)
                        .load(image)
                        .into(listRecipeImage);
            }


        }
    }

    public void setmContext(Context context) {
        mContext = context;
    }
}
