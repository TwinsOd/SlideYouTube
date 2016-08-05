package com.example.vladymyr.slideyoutube;

/**
 * Created by Twins on 05.08.2016.
 */

public class VideoModel {
    private String title;
    private String id;

    VideoModel(String title, String id){
        this.title = title;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
