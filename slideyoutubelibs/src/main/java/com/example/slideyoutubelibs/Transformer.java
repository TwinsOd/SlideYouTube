package com.example.slideyoutubelibs;

import android.view.View;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

public abstract class Transformer {

    private final View mView;
    private final View mParent;

    private int mMarginRight;
    private int mMarginBottom;

    private float mXScaleFactor;
    private float mYScaleFactor;

    private int mOriginalHeight;
    private int mOriginalWidth;

    Transformer(View view, View parent) {
        mView = view;
        mParent = parent;
    }

    float getXScaleFactor() {
        return mXScaleFactor;
    }

    public void setXScaleFactor(float xScaleFactor) {
        mXScaleFactor = xScaleFactor;
    }

    float getYScaleFactor() {
        return mYScaleFactor;
    }

    public void setYScaleFactor(float yScaleFactor) {
        mYScaleFactor = yScaleFactor;
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public void setMarginRight(int marginRight) {
        mMarginRight = marginRight;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        mMarginBottom = marginBottom;
    }

    /**
     * Change mView height using the LayoutParams of the mView.
     *
     * @param newHeight to change..
     */
    public void setViewHeight(int newHeight) {
        if (newHeight > 0) {
            mOriginalHeight = newHeight;
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) mView.getLayoutParams();
            layoutParams.height = newHeight;
            mView.setLayoutParams(layoutParams);
        }
    }

    protected View getView() {
        return mView;
    }

    View getParentView() {
        return mParent;
    }

    public abstract void updatePosition(float verticalDragOffset);

    public abstract void moveHorizont(int move);

    public abstract void updateScale(float verticalDragOffset);

    public abstract void moveUp();

    public abstract boolean isViewBottom();

    /**
     * @return height of the mView before it has change the size.
     */
    public int getOriginalHeight() {
        if (mOriginalHeight == 0) {
            mOriginalHeight = mView.getMeasuredHeight();
        }
        return mOriginalHeight;
    }

    int getParentHeight() {
        return mParent.getMeasuredHeight();
    }

    /**
     * @return width of the mView before it has change the size.
     */
    public int getOriginalWidth() {
        if (mOriginalWidth == 0) {
            mOriginalWidth = mView.getMeasuredWidth();
        }
        return mOriginalWidth;
    }

    public boolean isViewAtTop() {
        return mView.getTop() == 0;
    }

    public boolean isAboveTheMiddle() {
        int parentHeight = mParent.getHeight();
        float viewYPosition = ViewHelper.getY(mView) + (mView.getHeight() * 0.5f);
        return viewYPosition < (parentHeight * 0.5);
    }

    public abstract boolean isViewAtRight();

    /**
     * @return min possible height, after apply the transformation, plus the margin right.
     */
    public abstract int getMinHeightPlusMargin();

    /**
     * @return min possible width, after apply the transformation.
     */
    public abstract int getMinWidthPlusMarginRight();
}