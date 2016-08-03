package com.example.slideyoutubelibs;

public interface DraggableListener {

    /**
     * Called when the view is maximized.
     */
    void onMaximized();

    /**
     * Called when the view is minimized.
     */
    void onMinimized();

    /**
     * Called when the view is closed to the left.
     */
    void onClosedToLeft();

    /**
     * Called when the view is closed to the right.
     */
    void onClosedToRight();
}
