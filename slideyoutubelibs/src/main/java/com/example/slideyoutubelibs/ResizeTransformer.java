package com.example.slideyoutubelibs;

import android.view.View;
import android.widget.RelativeLayout;

public class ResizeTransformer extends Transformer {

    private final RelativeLayout.LayoutParams mLayoutParams;

    public ResizeTransformer(View view, View parent) {
        super(view, parent);
        mLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
    }

    /**
     * Changes view scale using view's LayoutParam.
     *
     * @param verticalDragOffset used to calculate the new size.
     */
    @Override
    public void updateScale(float verticalDragOffset) {
        mLayoutParams.width = (int) (getOriginalWidth() * (1 - verticalDragOffset / getXScaleFactor()));
        mLayoutParams.height = (int) (getOriginalHeight() * (1 - verticalDragOffset / getYScaleFactor()));

        getView().setLayoutParams(mLayoutParams);
    }

    /**
     * Changes X view position using layout() method.
     *
     * @param verticalDragOffset used to calculate the new X position.
     */
    @Override
    public void updatePosition(float verticalDragOffset) {
        int right = getViewRightPosition(verticalDragOffset);
        int left = right - mLayoutParams.width;
        int top = getView().getTop();
        int newTop = getParentHeight() - mLayoutParams.height;
        if (top > newTop) top = newTop;
        int bottom = top + mLayoutParams.height;
        getView().layout(left, top, right, bottom);
    }

    @Override
    public void moveUp() {
        mLayoutParams.width = getOriginalWidth();
        mLayoutParams.height = getOriginalHeight();
        getView().setLayoutParams(mLayoutParams);
        int right = getOriginalWidth();
        int bottom = mLayoutParams.height;
        getView().layout(0, 0, right, bottom);
    }

    @Override
    public void moveHorizont(int move) {
        int right = getViewRightPosition(1) + move;
        int left = right - mLayoutParams.width;
        int top = getView().getTop();
        int newTop = getParentHeight() - mLayoutParams.height;
        if (top > newTop) top = newTop;
        int bottom = top + mLayoutParams.height;
        getView().layout(left, top, right, bottom);
    }

    /**
     * @return true if the right position of the view plus the right margin is equals to the parent
     * width.
     */
    @Override
    public boolean isViewAtRight() {
//        ViewHelper.setAlpha(getView(),getParentView().getWidth()/getView().getLeft());
        return getView().getRight() + getMarginRight() == getParentView().getWidth();
    }

    public boolean isViewBottom() {
        return getView().getBottom() + getMarginBottom() >= getParentView().getHeight();
    }

    @Override
    public int getMinHeightPlusMargin() {
        return (int) (getOriginalHeight() * (1 - 1 / getYScaleFactor()) + getMarginBottom());
    }

    /**
     * Uses the X scale factor to calculate the min possible width.
     */
    @Override
    public int getMinWidthPlusMarginRight() {
        return (int) (getOriginalWidth() * (1 - 1 / getXScaleFactor()) + getMarginRight());
    }

    /**
     * Calculate the current view right position for a given verticalDragOffset.
     *
     * @param verticalDragOffset used to calculate the new right position.
     */
    private int getViewRightPosition(float verticalDragOffset) {
        return (int) ((getOriginalWidth()) - getMarginRight() * verticalDragOffset);
    }

}