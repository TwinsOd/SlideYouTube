package com.example.slideyoutubelibs;

import android.support.v4.widget.ViewDragHelper;
import android.view.View;


class DraggableViewCallback extends ViewDragHelper.Callback {

    //    private static final int MINIMUM_DX_FOR_HORIZONTAL_DRAG = 5;
    private static final int MINIMUM_DY_FOR_VERTICAL_DRAG = 15;
    private static final float X_MIN_VELOCITY = 1500;
    private static final float Y_MIN_VELOCITY = 1000;

    private DraggableView mDraggableView;
    private View mDraggedView;

    /**
     * Main constructor.
     *
     * @param draggableView instance used to apply some animations or visual effects.
     */
    DraggableViewCallback(DraggableView draggableView, View draggedView) {
        mDraggableView = draggableView;
        mDraggedView = draggedView;
    }

    /**
     * Override method used to apply different scale and alpha effects while the view is being
     * dragged.
     *
     * @param left position.
     * @param top  position.
     * @param dx   change in X position from the last call.
     * @param dy   change in Y position from the last call.
     */
    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//        if (mDraggableView.isDragViewAtBottom()) {
//            mDraggableView.changeDragViewScale();
//            mDraggableView.changeDragViewHorizontalPosition();
//            mDraggableView.changeSecondViewAlpha();
//            mDraggableView.changeSecondViewPosition();
//            mDraggableView.changeBackgroundAlpha();
//        } else {
            mDraggableView.changeDragViewScale();
            mDraggableView.changeDragViewPosition();
            mDraggableView.changeSecondViewAlpha();
            mDraggableView.changeSecondViewPosition();
            mDraggableView.changeBackgroundAlpha();
//        }
    }

    /**
     * Override method used to apply different animations when the dragged view is released. The
     * dragged view is going to be maximized or minimized if the view is above the middle of the
     * custom view and the velocity is greater than a constant value.
     *
     * @param releasedChild the captured child view now being released.
     * @param xVel          X velocity of the pointer as it left the screen in pixels per second.
     * @param yVel          Y velocity of the pointer as it left the screen in pixels per second.
     */
    @Override
    public void onViewReleased(View releasedChild, float xVel, float yVel) {
        super.onViewReleased(releasedChild, xVel, yVel);

        if (mDraggableView.isDragViewAtBottom() && !mDraggableView.isDragViewAtRight()) {
            triggerOnReleaseActionsWhileHorizontalDrag(xVel);
        } else {
            triggerOnReleaseActionsWhileVerticalDrag(yVel);
        }
    }

    /**
     * Override method used to configure which is going to be the dragged view.
     *
     * @param view      child the user is attempting to capture.
     * @param pointerId ID of the pointer attempting the capture,
     * @return true if capture should be allowed, false otherwise.
     */
    @Override
    public boolean tryCaptureView(View view, int pointerId) {
        return view.equals(mDraggedView);
    }

    /**
     * Override method used to configure the vertical drag. Restrict the motion of the dragged child
     * view along the vertical axis.
     *
     * @param child child view being dragged.
     * @param top   attempted motion along the Y axis.
     * @param dy    proposed change in position for top.
     * @return the new clamped position for top.
     */
    //smooth slide
    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
        int newTop = mDraggableView.getHeight() - mDraggableView.getDraggedViewHeightPlusMarginTop();
        if (mDraggableView.isMinimized() && Math.abs(dy) >= MINIMUM_DY_FOR_VERTICAL_DRAG
                || (!mDraggableView.isMinimized() && !mDraggableView.isDragViewAtBottom())) {
            final int topBound = mDraggableView.getPaddingTop();
            final int bottomBound = mDraggableView.getHeight()
                    - mDraggableView.getDraggedViewHeightPlusMarginTop()
                    - mDraggedView.getPaddingBottom();
            newTop = Math.min(Math.max(top, topBound), bottomBound);
        }
        return newTop;
    }

    /**
     * Maximize or minimize the DraggableView using the mDraggableView position and the y axis
     * velocity.
     */
    private void triggerOnReleaseActionsWhileVerticalDrag(float yVel) {
        if (yVel < 0 && yVel <= -Y_MIN_VELOCITY) {
            mDraggableView.maximize();
        } else if (yVel > 0 && yVel >= Y_MIN_VELOCITY) {
            mDraggableView.minimize();
        } else {
            if (mDraggableView.isDragViewAboveTheMiddle()) {
                mDraggableView.maximize();
            } else {
                mDraggableView.minimize();
            }
        }
    }

    /**
     * Close the view to the right, to the left or minimize it using the mDraggableView position and
     * the x axis velocity.
     */
    private void triggerOnReleaseActionsWhileHorizontalDrag(float xVel) {
        if (xVel < 0 && xVel <= -X_MIN_VELOCITY) {
            mDraggableView.closeToLeft();
        } else if (xVel > 0 && xVel >= X_MIN_VELOCITY) {
            mDraggableView.closeToRight();
        } else
            mDraggableView.minimize();
    }

}
