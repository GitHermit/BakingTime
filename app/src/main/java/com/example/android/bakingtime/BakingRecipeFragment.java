package com.example.android.bakingtime;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class BakingRecipeFragment extends Fragment {
    private static final String POSITION ="position" ;
    private String DESCRIPTION = "description";
    private String VIDEO = "videoURL";
    private String mURL;
    private String imageURL;
    private Context mContext;
    private SimpleExoPlayer player;
    private long currentPlace = 0;
    private boolean configChange = false;

    private String mDescription;



    public BakingRecipeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_baking_recipe, container, false);

        if (savedInstanceState != null){
            currentPlace = savedInstanceState.getLong(POSITION);
            mDescription = savedInstanceState.getString(DESCRIPTION);
            mURL = savedInstanceState.getString(VIDEO);
        }
        TextView textView = (TextView) rootView.findViewById(R.id.description_text);


        textView.setText(mDescription);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD),1);
        TextView description = rootView.findViewById(R.id.description_header);
        description.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD),1);
        description.setText(R.string.description);
        PlayerView playerView = rootView.findViewById(R.id.exo_play);
        playerView.setVisibility(View.GONE);

        if (mURL != null) {
            if(mURL.contains("mp4")){


            mContext = getContext();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create the player
            player =
                    ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            playerView.setPlayer(player);


            // Measures bandwidth during playback. Can be null if not required.
//            DefaultBandwidthMeter bandwidtchMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, "BakingTime"), null);
// This is the MediaSource representing the media to be played.

                    Uri mp4VideoUri = Uri.parse(mURL);
                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mp4VideoUri);
// Prepare the player with the source.
                    player.seekTo(currentPlace);
                    player.prepare(videoSource,false,false);
                playerView.setVisibility(View.VISIBLE);
            }
            else {
                if (imageURL != null) {
                    if (imageURL.contains(".jpeg") || imageURL.contains(".png")){
                        ImageView imageView = rootView.findViewById(R.id.recipe_step_image);
                        Picasso.with(mContext)
                                .load(imageURL)
                                .into(imageView);
                    }
                }

            }

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            currentPlace = savedInstanceState.getLong(POSITION);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mURL != null){
            if (!mURL.isEmpty()){
                if (player != null){
                        currentPlace = 0;
                        Log.v("ReleasedPlayer", "Player got released now");
                        releasePlayer();

                }
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mURL != null){
            if (!mURL.isEmpty()){
                if (player != null){
                    configChange = true;
                    currentPlace = player.getCurrentPosition();
                    Log.v("ReleasedPlayer", "Player got released now");
//                    releasePlayer();
                }

            }

        }
    }



//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mURL != null){
//            if (!mURL.isEmpty()){
//                releasePlayer();
//            }
//
//        }
//    }

    private void releasePlayer() {
        player.stop();
        player.release();
        player = null;
    }

    public void setVideoURL(String URL) {mURL = URL;}

    public void setDescription(String description) {mDescription = description; }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putLong(POSITION, currentPlace);
        currentState.putString(DESCRIPTION, mDescription);
        currentState.putString(VIDEO, mURL);
    }


    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
