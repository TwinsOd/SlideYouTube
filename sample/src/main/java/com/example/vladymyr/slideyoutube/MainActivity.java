package com.example.vladymyr.slideyoutube;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private   String videoId;
    DraggablePanel mYouTubeMiniDraggablePanel;
    private int heightTopFragment = 400;
    ArrayList<VideoModel> data = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.list_view);
        mYouTubeMiniDraggablePanel = (DraggablePanel) findViewById(R.id.draggablePanel);

        initData();

        ArrayList<String> dataTitle = new ArrayList<>();
        for (VideoModel v:data)dataTitle.add(v.getTitle());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataTitle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                showVideo(i);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        heightTopFragment = 9*width/16;
    }

    private void initData() {
        data.add(new VideoModel("Google I/O 2016 - Keynote", "862r3XS2YB0"));
        data.add(new VideoModel("Google I/O 2016 Wylsacom Special LIVE - 20:00 МСК", "20DzPqhxW3k"));
        data.add(new VideoModel("Итоги презентации Google I/O 2016", "TthesF3EsOw"));
        data.add(new VideoModel("The biggest Google I/O 2016 news in 10 minutes", "f7_4QoZwH54"));
        data.add(new VideoModel("Презентация Google I/O 2016 18 мая", "-ytUluF6teE"));
        data.add(new VideoModel("Google I/O 2016 за 10 минут: Wear 2.0, Google Home, Daydream и Android N", "sP2-mBgNxqs"));
    }

    private void showVideo(int id) {
        videoId = data.get(id).getId();
        if (mYouTubeMiniDraggablePanel != null)mYouTubeMiniDraggablePanel.removeAllViews();
        mYouTubeMiniDraggablePanel.setFragmentManager(getSupportFragmentManager());

        YouTubePlayerSupportFragment youTubeVideoFragment = new YouTubePlayerSupportFragment();
        youTubeVideoFragment.initialize(YOUTUBE_API_KEY, this);

        mYouTubeMiniDraggablePanel.setTopFragment(youTubeVideoFragment);
        mYouTubeMiniDraggablePanel.setHeightTopFragment(heightTopFragment);
        mYouTubeMiniDraggablePanel.setBottomFragment(DescriptionYouTubeFragment.newInstance(YOUTUBE_API_KEY, videoId));
        mYouTubeMiniDraggablePanel.initializeView();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(videoId);
        youTubePlayer.setShowFullscreenButton(true);
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
