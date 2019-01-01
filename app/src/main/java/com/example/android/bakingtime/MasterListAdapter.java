package com.example.android.bakingtime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingtime.Model.BakingRecipe;

public class MasterListAdapter extends BaseAdapter {
    private Context mContext;
    private BakingRecipe recipe;

    public MasterListAdapter(Context context, BakingRecipe r) {
        mContext = context;
        recipe = r;
    }
    @Override
    public int getCount() {
        return recipe.getShortDescription().size();
    }

    @Override
    public Object getItem(int position) {
        return recipe.getShortDescription().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        View listItemView = convertView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.steplist, parent, false);
        }
        textView = listItemView.findViewById(R.id.steps);
        if (position == 0) {
            textView.setText(R.string.ingredients);
        } else {
            String text = recipe.getShortDescription().get(position-1);
            textView.setText(text);
        }
        return textView;
    }
}
