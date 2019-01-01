package com.example.android.bakingtime;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.support.annotation.Nullable;




public class RecipeUpdateService extends IntentService {

    public static final String ACTION_REPLACE_WIDGET="com.example.android.bakingtime.action.update_recipe_widget";

    public RecipeUpdateService() {
        super("RecipeUpdateService");
    }

    public static void startActionUpdateRecipeWidgets(Context context){
        Intent intent = new Intent(context, RecipeUpdateService.class);
        intent.setAction(ACTION_REPLACE_WIDGET);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REPLACE_WIDGET.equals(action)){
                handleActionUpdateWidget();
            }
        }

    }

    private void handleActionUpdateWidget() {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds);

    }
}
