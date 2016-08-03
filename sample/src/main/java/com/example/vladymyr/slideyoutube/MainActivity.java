package com.example.vladymyr.slideyoutube;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.slideyoutubelibs.DescriptionYouTubeFragment;
import com.example.slideyoutubelibs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{
    private static final String YOUTUBE_API_KEY = "AIzaSyDWdz76IuzaT_OPNZGfya_A2yUIjFCuM-Q";
    private static final String VIDEO_ID = "WuzhdmBcCL4";
    DraggablePanel mYouTubeMiniDraggablePanel;
    private int heightTopFragment = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.list_view);

        ArrayList<String> data = new ArrayList<>();

        for (int i=1; i <= 23; i++)
            data.add("video number " + i);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showVideo();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        heightTopFragment = 9*width/16;
    }

    private void showVideo() {
        mYouTubeMiniDraggablePanel = (DraggablePanel) findViewById(R.id.draggablePanel);
        mYouTubeMiniDraggablePanel.setFragmentManager(getSupportFragmentManager());

        YouTubePlayerSupportFragment youTubeVideoFragment = new YouTubePlayerSupportFragment();
        youTubeVideoFragment.initialize(YOUTUBE_API_KEY, this);
//        DescriptionYouTubeFragment descriptionYouTubeFragment = DescriptionYouTubeFragment.newInstance(YOUTUBE_API_KEY, VIDEO_ID);


        mYouTubeMiniDraggablePanel.setTopFragment(youTubeVideoFragment);
        mYouTubeMiniDraggablePanel.setHeightTopFragment(heightTopFragment);
        mYouTubeMiniDraggablePanel.setBottomFragment(DescriptionYouTubeFragment.newInstance(YOUTUBE_API_KEY, VIDEO_ID));
        mYouTubeMiniDraggablePanel.initializeView();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(VIDEO_ID);
        youTubePlayer.setShowFullscreenButton(true);
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
