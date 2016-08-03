package com.example.slideyoutubelibs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;


public class DraggableView extends RelativeLayout {
    private static final int DEFAULT_SCALE_FACTOR = 3;
    private static final int DEFAULT_TOP_VIEW_MARGIN = 30;
    private static final int DEFAULT_TOP_VIEW_HEIGHT = -1;
    private static final float SLIDE_TOP = 0f;
    private static final float SLIDE_BOTTOM = 1f;
    private static final float MIN_SLIDE_OFFSET = 0.15f;
    private static final boolean DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT = true;
    private static final boolean DEFAULT_ENABLE_CLICK_TO_MAXIMIZE = true;
    private static final boolean DEFAULT_ENABLE_CLICK_TO_MINIMIZE = true;
    private static final boolean DEFAULT_ENABLE_TOUCH_LISTENER = true;
    private static final int MIN_SLIDING_DISTANCE_ON_CLICK = 10;
    private static final int ONE_HUNDRED = 100;
    private static final float SENSITIVITY = 1f;
    private static final boolean DEFAULT_TOP_VIEW_RESIZE = false;
    private static final int INVALID_POINTER = -1;

    private int mActivePointerId = INVALID_POINTER;
    private float mLastTouchActionDownXPosition;

    private View mDragView;
    private View mSecondView;

    private FragmentManager mFragmentManager;
    private ViewDragHelper mViewDragHelper;
    private Transformer mTransformer;

    private boolean mIsEnableHorizontalAlphaEffect;
    private boolean mIsTopViewResize;
    private boolean mIsEnableClickToMaximize;
    private boolean mIsEnableClickToMinimize;
    private boolean mIsTouchEnabled;

    private DraggableListener mListener;
    private int mTopViewHeight;
    private float mScaleFactorX;
    private float mScaleFactorY;
    private int mMarginBottom;
    private int mMarginRight;
    private int mDragViewId;
    private int mSecondViewId;

    private float mXActionDown = 0;
    private int mYActionDown = 0;

    private float mMoveHorizontal = 0;


    public DraggableView(Context context) {
        super(context);
    }

    public DraggableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(attrs);
    }

    public DraggableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeAttributes(attrs);
    }

    /**
     * Return if user can maximize minimized view on click.
     */
    public boolean isClickToMaximizeEnabled() {
        return mIsEnableClickToMaximize;
    }

    /**
     * Enable or disable click to maximize view when dragged view is minimized
     * If your content have a touch/click mListener (like YoutubePlayer), you
     * need disable it to active this feature.
     *
     * @param isEnableClickToMaximize to enable or disable the click.
     */
    void setClickToMaximizeEnabled(boolean isEnableClickToMaximize) {
        mIsEnableClickToMaximize = true;
    }

    /**
     * Return if user can minimize maximized view on click.
     */
    public boolean isClickToMinimizeEnabled() {
        return mIsEnableClickToMinimize;
    }

    /**
     * Enable or disable click to minimize view when dragged view is maximized
     * If your content have a touch/click mListener (like YoutubePlayer), you
     * need disable it to active this feature.
     *
     * @param isEnableClickToMinimize to enable or disable the click.
     */
    public void setClickToMinimizeEnabled(boolean isEnableClickToMinimize) {
        mIsEnableClickToMinimize = isEnableClickToMinimize;
    }

    /**
     * Return if touch mListener is enable or disable
     */
    private boolean isTouchEnabled() {
        return mIsTouchEnabled;
    }

    /**
     * Enable or disable the touch mListener
     *
     * @param isTouchEnabled to enable or disable the touch event.
     */
    public void setTouchEnabled(boolean isTouchEnabled) {
        mIsTouchEnabled = isTouchEnabled;
    }

    /**
     * Configure the horizontal scale factor applied when the view is dragged to the bottom of the
     * custom view.
     */
    public void setXTopViewScaleFactor(float xScaleFactor) {
        mTransformer.setXScaleFactor(xScaleFactor);
    }

    /**
     * Configure the vertical scale factor applied when the view is dragged to the bottom of the
     * custom view.
     */
    public void setYTopViewScaleFactor(float yScaleFactor) {
        mTransformer.setYScaleFactor(yScaleFactor);
    }

    /**
     * Configure the dragged view margin right applied when the dragged view is minimized.
     *
     * @param topFragmentMarginRight in pixels.
     */
    public void setTopViewMarginRight(int topFragmentMarginRight) {
        mTransformer.setMarginRight(topFragmentMarginRight);
    }

    /**
     * Configure the mDragView margin bottom applied when the mDragView is minimized.
     */
    public void setTopViewMarginBottom(int topFragmentMarginBottom) {
        mTransformer.setMarginBottom(topFragmentMarginBottom);
    }

    /**
     * Configure the dragged view height.
     *
     * @param topFragmentHeight in pixels
     */
    public void setTopViewHeight(int topFragmentHeight) {
        mTransformer.setViewHeight(topFragmentHeight);
    }

    /**
     * Configure the DraggableListener notified when the view is minimized, maximized, closed to the
     * right or closed to the left.
     */
    public void setDraggableListener(DraggableListener listener) {
        mListener = listener;
    }

    /**
     * Configure DraggableView to resize top view instead of scale it.
     */
    public void setTopViewResize(boolean isTopViewResize) {
        mIsTopViewResize = isTopViewResize;
        initializeTransformer();
    }

    /**
     * To ensure the animation is going to work this method has been override to call
     * postInvalidateOnAnimation if the view is not settled yet.
     */
    @Override
    public void computeScroll() {
        if (!isInEditMode() && mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Maximize the custom view applying an animation to return the view to the initial position.
     */
    public void maximize() {
        smoothSlideTo(SLIDE_TOP);
        notifyMaximizeToListener();
    }

    /**
     * Minimize the custom view applying an animation to put the top fragment on the bottom right
     * corner of the screen.
     */
    public void minimize() {
        smoothSlideTo(SLIDE_BOTTOM);
        notifyMinimizeToListener();
        mMoveHorizontal = 0;
    }

    /**
     * Close the custom view applying an animation to close the view to the right side of the screen.
     */
    public void closeToRight() {
        int left = mTransformer.getOriginalWidth();
        int top = getHeight() - mTransformer.getMinHeightPlusMargin();
        if (mViewDragHelper.smoothSlideViewTo(mDragView, left, top)) {
            ViewCompat.postInvalidateOnAnimation(this);
            notifyCloseToRightListener();
        }
    }

    /**
     * Close the custom view applying an animation to close the view to the left side of the screen.
     */
    public void closeToLeft() {
        int left = -mTransformer.getOriginalWidth();
        int top = getHeight() - mTransformer.getMinHeightPlusMargin();
        if (mViewDragHelper.smoothSlideViewTo(mDragView, left, top)) {
            ViewCompat.postInvalidateOnAnimation(this);
            notifyCloseToLeftListener();
        }
    }

    /**
     * Checks if the top view is minimized.
     *
     * @return true if the view is minimized.
     */
    public boolean isMinimized() {
        return isDragViewAtBottom() && isDragViewAtRight();
    }

    /**
     * Checks if the top view is maximized.
     *
     * @return true if the view is maximized.
     */
    public boolean isMaximized() {
        return isDragViewAtTop();
    }

    /**
     * Checks if the top view closed at the right place.
     *
     * @return true if the view is closed at right.
     */
    public boolean isClosedAtRight() {
        return mDragView.getLeft() >= getWidth();
    }

    /**
     * Checks if the top view is closed at the left place.
     *
     * @return true if the view is closed at left.
     */
    public boolean isClosedAtLeft() {
        return mDragView.getRight() <= 0;
    }

    /**
     * Checks if the top view is closed at the right or left place.
     *
     * @return true if the view is closed.
     */
    public boolean isClosed() {
        return isClosedAtLeft() || isClosedAtRight();
    }

    /**
     * Override method to intercept only touch events over the drag view and to cancel the drag when
     * the action associated to the MotionEvent is equals to ACTION_CANCEL or ACTION_UP.
     *
     * @param ev captured.
     * @return true if the view is going to process the touch event or false if not.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        switch (MotionEventCompat.getActionMasked(ev) & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mViewDragHelper.cancel();
                return false;
            case MotionEvent.ACTION_DOWN:
                int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
            default:
                break;
        }
        boolean interceptTap = mViewDragHelper.isViewUnder(mDragView, (int) ev.getX(), (int) ev.getY());
        return mViewDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    /**
     * Override method to dispatch touch event to the dragged view.
     *
     * @param ev captured.
     * @return true if the touch event is realized over the drag or second view.
     */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // for maximized
        boolean interceptTap = mViewDragHelper.isViewUnder(mDragView, (int) ev.getX(), (int) ev.getY());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXActionDown = (int) ev.getX();
                mYActionDown = (int) ev.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int xActionUp = (int) ev.getX();
                int yActionUp = (int) ev.getY();
                if (interceptTap
                        && mTransformer.isViewBottom()
                        && mXActionDown == xActionUp
                        && mYActionDown == yActionUp)
                    maximize();
                break;
            // for move horizontal
            case MotionEvent.ACTION_MOVE:
                if (interceptTap && mTransformer.isViewBottom()) {
                    float xActionMove = ev.getX();
                    mMoveHorizontal = xActionMove - mXActionDown;
//                    for low volume
//                    if(mMoveHorizontal > 0){
//                        mDragView.setVolume((mDragView.getWidth() - mMoveHorizontal)/mDragView.getWidth());
//                    }else if(mMoveHorizontal < 0){
//                        mDragView.setVolume((mDragView.getWidth() - mMoveHorizontal*(-1))/mDragView.getWidth());
//                    } else  mDragView.setVolume(1);

                    if (mMoveHorizontal > mDragView.getWidth() * 0.5) {
                        final Animation translate = AnimationUtils.loadAnimation(getContext(),
                                R.anim.translate_mini_youtube_right);
                        mDragView.startAnimation(translate);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                        closeToRight();
                        mDragView.setVisibility(GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    } else if
                            (-mMoveHorizontal > mDragView.getWidth() * 0.5) {
                        final Animation translate = AnimationUtils.loadAnimation(getContext(),
                                R.anim.translate_mini_youtube_left);
                        mDragView.startAnimation(translate);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                closeToLeft();
                                mDragView.setVisibility(GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                }
        }
        int actionMasked = MotionEventCompat.getActionMasked(ev);
        if ((actionMasked & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mActivePointerId = MotionEventCompat.getPointerId(ev, actionMasked);
        }
        if (mActivePointerId == INVALID_POINTER) {
            return false;
        }
        mViewDragHelper.processTouchEvent(ev);
        if (isClosed()) {
            return false;
        }
        boolean isDragViewHit = isViewHit(mDragView, (int) ev.getX(), (int) ev.getY());
        boolean isSecondViewHit = isViewHit(mSecondView, (int) ev.getX(), (int) ev.getY());
//        analyzeTouchToMaximizeIfNeeded(ev, isDragViewHit);
        if (isMaximized()) {
            mDragView.dispatchTouchEvent(ev);
        } else {
            mDragView.dispatchTouchEvent(cloneMotionEventWithAction(ev, MotionEvent.ACTION_CANCEL));
        }
        return isDragViewHit || isSecondViewHit;
    }

    public boolean shouldMaximizeOnClick(MotionEvent ev, float deltaX, boolean isDragViewHit) {
        return (Math.abs(deltaX) < MIN_SLIDING_DISTANCE_ON_CLICK)
                && ev.getAction() != MotionEvent.ACTION_MOVE
                && isDragViewHit;
    }

    /**
     * Clone given motion event and set specified action. This method is useful, when we want to
     * cancel event propagation in child views by sending event with {@link
     * android.view.MotionEvent#ACTION_CANCEL}
     * action.
     *
     * @param event  event to clone
     * @param action new action
     * @return cloned motion event
     */
    private MotionEvent cloneMotionEventWithAction(MotionEvent event, int action) {
        return MotionEvent.obtain(
                event.getDownTime(),
                event.getEventTime(),
                action,
                event.getX(),
                event.getY(),
                event.getMetaState());
    }

    /**
     * Override method to configure the dragged view and mSecondView layout properly.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (isInEditMode()) {
            super.onLayout(changed, left, top, right, bottom);
        } else if (isDragViewAtTop()) {
            mDragView.layout(left, top, right, mTransformer.getOriginalHeight());
            mSecondView.layout(left, mTransformer.getOriginalHeight(), right, bottom);
            ViewHelper.setY(mDragView, top);
            ViewHelper.setY(mSecondView, mTransformer.getOriginalHeight());
        } else {
            mSecondView.layout(left, mTransformer.getOriginalHeight(), right, bottom);
        }
    }

    /**
     * Override method to map dragged view, mSecondView to view objects, to configure dragged
     * view height and to initialize DragViewHelper.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            mapGUI();
            initializeTransformer();
            initializeViewDragHelper();
        }
    }

    private void mapGUI() {
        mDragView = findViewById(mDragViewId);
        mSecondView = findViewById(mSecondViewId);
    }

    /**
     * Configure the FragmentManager used to attach top and bottom Fragments to the view. The
     * FragmentManager is going to be provided only by DraggablePanel view.
     */
    void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    /**
     * Attach one fragment to the dragged view.
     *
     * @param topFragment to be attached.
     */
    void attachTopFragment(Fragment topFragment) {
        addFragmentToView(R.id.drag_view_player, topFragment);
    }

    /**
     * Attach one fragment to the mSecondView.
     *
     * @param bottomFragment to be attached.
     */
    void attachBottomFragment(Fragment bottomFragment) {
        addFragmentToView(R.id.second_view, bottomFragment);
    }

    /**
     * Modify dragged view pivot based on the dragged view vertical position to simulate a horizontal
     * displacement while the view is dragged.
     */
    void changeDragViewPosition() {
        mTransformer.updatePosition(getVerticalDragOffset());
    }

    void changeDragViewHorizontalPosition() {
        mTransformer.moveHorizont((int) mMoveHorizontal);
    }

    /**
     * Modify mSecondView position to be always below dragged view.
     */
    void changeSecondViewPosition() {
        ViewHelper.setY(mSecondView, mDragView.getBottom());
    }

    /**
     * Modify dragged view scale based on the dragged view vertical position and the scale factor.
     */
    void changeDragViewScale() {
        mTransformer.updateScale(getVerticalDragOffset());
    }

    /**
     * Modify the background alpha if has been configured to applying an alpha effect when the view
     * is dragged.
     */
    void changeBackgroundAlpha() {
        Drawable background = getBackground();
        if (background != null) {
            int newAlpha = (int) (ONE_HUNDRED * (1 - getVerticalDragOffset()));
            background.setAlpha(newAlpha);
        }
    }

    /**
     * Modify the second view alpha based on dragged view vertical position.
     */
    void changeSecondViewAlpha() {
        ViewHelper.setAlpha(mSecondView, 1 - getVerticalDragOffset());
    }

    /**
     * Check if dragged view is above the middle of the custom view.
     *
     * @return true if dragged view is above the middle of the custom view or false if is below.
     */
    boolean isDragViewAboveTheMiddle() {
        return mTransformer.isAboveTheMiddle();
    }

    /**
     * Check if dragged view is at the top of the custom view.
     *
     * @return true if dragged view top position is equals to zero.
     */
    boolean isDragViewAtTop() {
        return mTransformer.isViewAtTop();
    }

    /**
     * Check if dragged view is at the right of the custom view.
     *
     * @return true if dragged view right position is equals to custom view width.
     */
    boolean isDragViewAtRight() {
        return mTransformer.isViewAtRight();
    }

    /**
     * Check if dragged view is at the bottom of the custom view.
     *
     * @return true if dragged view bottom position is equals to custom view height.
     */
    boolean isDragViewAtBottom() {
        return (getVerticalDragOffset() == 1f);
    }

    /**
     * Calculate if one position is above any view.
     *
     * @param view to analyze.
     * @param x    position.
     * @param y    position.
     * @return true if x and y positions are below the view.
     */
    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.getWidth()
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.getHeight();
    }

    /**
     * Use FragmentManager to attach one fragment to one view using the viewId.
     *
     * @param viewId   used to obtain the view.
     * @param fragment to be attached.
     */
    private void addFragmentToView(final int viewId, final Fragment fragment) {
        mFragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    /**
     * Initialize the mViewDragHelper.
     */
    private void initializeViewDragHelper() {
        mViewDragHelper = ViewDragHelper.create(this, SENSITIVITY, new DraggableViewCallback(this, mDragView));
    }

    /**
     * Initialize Transformer with a scalable or change width/height implementation.
     */
    private void initializeTransformer() {
        mTransformer = new ResizeTransformer(mDragView, this);
        mTransformer.setViewHeight(mTopViewHeight);
        mTransformer.setXScaleFactor(mScaleFactorX);
        mTransformer.setYScaleFactor(mScaleFactorY);
        mTransformer.setMarginRight(mMarginRight);
        mTransformer.setMarginBottom(mMarginBottom);
    }

    /**
     * Initialize XML attributes.
     *
     * @param attrs to be analyzed.
     */
    private void initializeAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.draggable_view);
        this.mIsEnableClickToMaximize =
                attributes.getBoolean(R.styleable.draggable_view_enable_click_to_maximize_view,
                        DEFAULT_ENABLE_CLICK_TO_MAXIMIZE);
        this.mIsEnableClickToMinimize =
                attributes.getBoolean(R.styleable.draggable_view_enable_click_to_minimize_view,
                        DEFAULT_ENABLE_CLICK_TO_MINIMIZE);
        this.mIsTopViewResize =
                attributes.getBoolean(R.styleable.draggable_view_top_view_resize, DEFAULT_TOP_VIEW_RESIZE);
        this.mTopViewHeight = attributes.getDimensionPixelSize(R.styleable.draggable_view_top_view_height,
                DEFAULT_TOP_VIEW_HEIGHT);
        this.mScaleFactorX = attributes.getFloat(R.styleable.draggable_view_top_view_x_scale_factor,
                DEFAULT_SCALE_FACTOR);
        this.mScaleFactorY = attributes.getFloat(R.styleable.draggable_view_top_view_y_scale_factor,
                DEFAULT_SCALE_FACTOR);
        this.mMarginBottom = attributes.getDimensionPixelSize(R.styleable.draggable_view_top_view_margin_bottom,
                DEFAULT_TOP_VIEW_MARGIN);
        this.mMarginRight = attributes.getDimensionPixelSize(R.styleable.draggable_view_top_view_margin_right,
                DEFAULT_TOP_VIEW_MARGIN);
        this.mDragViewId =
                attributes.getResourceId(R.styleable.draggable_view_top_view_id, R.id.drag_view);
        this.mSecondViewId =
                attributes.getResourceId(R.styleable.draggable_view_bottom_view_id, R.id.second_view);
        attributes.recycle();
    }

    /**
     * Realize an smooth slide to an slide offset passed as argument. This method is the base of
     * maximize, minimize and close methods.
     *
     * @param slideOffset to apply
     * @return true if the view is slided.
     */
    private boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int x = (int) (slideOffset * (getWidth() - mTransformer.getMinWidthPlusMarginRight()));
        int y = (int) (topBound + slideOffset * getVerticalDragRange());
        if (mViewDragHelper.smoothSlideViewTo(mDragView, x, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    /**
     * @return configured dragged view margin right configured.
     */
    private int getDragViewMarginRight() {
        return mTransformer.getMarginRight();
    }

    /**
     * @return configured dragged view margin bottom.
     */
    private int getDragViewMarginBottom() {
        return mTransformer.getMarginBottom();
    }

    /**
     * Calculate the dragged view  top position normalized between 1 and 0.
     *
     * @return dragged view top divided by vertical drag range.
     */
    private float getVerticalDragOffset() {
        return mDragView.getTop() / getVerticalDragRange();
    }

    /**
     * Calculate the vertical drag range between the custom view and dragged view.
     *
     * @return the difference between the custom view height and the dragged view height.
     */
    private float getVerticalDragRange() {
        return getHeight() - mTransformer.getMinHeightPlusMargin();
    }

    /**
     * Notify te view is maximized to the DraggableListener
     */
    private void notifyMaximizeToListener() {
        if (mListener != null) {
            mListener.onMaximized();
        }
    }

    /**
     * Notify te view is minimized to the DraggableListener
     */
    private void notifyMinimizeToListener() {
        if (mListener != null) {
            mListener.onMinimized();
        }
    }

    /**
     * Notify te view is closed to the right to the DraggableListener
     */
    private void notifyCloseToRightListener() {
        if (mListener != null) {
            mListener.onClosedToRight();
        }
    }

    /**
     * Notify te view is closed to the left to the DraggableListener
     */
    private void notifyCloseToLeftListener() {
        if (mListener != null) {
            mListener.onClosedToLeft();
        }
    }

    public int getDraggedViewHeightPlusMarginTop() {
        return mTransformer.getMinHeightPlusMargin();
    }

}