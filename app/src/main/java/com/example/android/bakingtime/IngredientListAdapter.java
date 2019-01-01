package com.example.android.bakingtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingtime.Model.BakingRecipe;

import java.util.List;

public class IngredientListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mIngredient;
    private List<String> mMeasurement;
    private List<Double> mQuantity;

    public IngredientListAdapter(Context context, List<String> ingredient, List<String> measurements, List<Double> quantity) {
        mContext = context;
        mIngredient = ingredient;
        mMeasurement = measurements;
        mQuantity = quantity;
    }


    @Override
    public int getCount() {
        if (!mIngredient.isEmpty()) {
            return mIngredient.size();
        } else {
            return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        View listItemView = convertView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.steplist, parent, false);
            convertView = listItemView;
            textView = (TextView) convertView.findViewById(R.id.steps);
            convertView.setTag(textView);
        }
        else {
            textView = (TextView) convertView.getTag();
        }

        String ingredientInformation = mIngredient.get(position) + ": " + mQuantity.get(position) + " " + mMeasurement.get(position);


        textView.setText(ingredientInformation);

        return convertView;
    }
}
