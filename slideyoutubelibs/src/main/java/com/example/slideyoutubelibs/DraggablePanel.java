package com.example.slideyoutubelibs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class DraggablePanel extends FrameLayout {
    private static final int DEFAULT_TOP_FRAGMENT_HEIGHT = 200;
    private static final int DEFAULT_TOP_FRAGMENT_MARGIN = 50;
    private static final float DEFAULT_SCALE_FACTOR = 2;
    private static final boolean DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT = true;
    private static final boolean DEFAULT_ENABLE_CLICK_TO_MAXIMIZE = false;
    private static final boolean DEFAULT_ENABLE_CLICK_TO_MINIMIZE = false;
    private static final boolean DEFAULT_ENABLE_TOUCH_LISTENER = true;

    private DraggableView mDraggableView;
    private DraggableListener mDraggableListener;
    private FragmentManager mFragmentManager;
    private Fragment mTopFragment;
    private Fragment mBottomFragment;
    private int mTopFragmentHeight;
    private int mTopFragmentMarginRight;
    private int mTopFragmentMarginBottom;
    private boolean mIsEnableClickToMaximize;
    private boolean mIsEnableClickToMinimize;
    private boolean mIsEnableTouchListener;
    private int heightTopFragment;

    public DraggablePanel(Context context) {
        super(context);
    }

    public DraggablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttrs(attrs);
    }

    public DraggablePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeAttrs(attrs);
    }

    /**
     * Configure the FragmentManager used to attach top and bottom fragment inside the view.
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * Configure the Fragment that will work as draggable element inside this custom view. This
     * Fragment has to be configured before initialize the view.
     *
     * @param topFragment used as draggable element.
     */
    public void setTopFragment(Fragment topFragment) {
        mTopFragment = topFragment;
    }

    /**
     * Configure the Fragment that will work as secondary element inside this custom view. This
     * Fragment has to be configured before initialize the view.
     *
     * @param bottomFragment used as secondary element.
     */
    public void setBottomFragment(Fragment bottomFragment) {
        mBottomFragment = bottomFragment;
    }

    public void setDraggableListener(DraggableListener draggableListener) {
        mDraggableListener = draggableListener;
    }
    public void setHeightTopFragment(int heightTopFragment) {
        this.heightTopFragment = heightTopFragment;
    }


    public void initializeView() {
        checkFragmentConsistency();
        checkSupportFragmentManagerConsistency();
        inflate(getContext(), R.layout.draggable_panel, this);
        mDraggableView = (DraggableView) findViewById(R.id.draggable_view);
        mDraggableView.setTopViewHeight(heightTopFragment);
        mDraggableView.setFragmentManager(mFragmentManager);
        mDraggableView.attachTopFragment(mTopFragment);
        mDraggableView.setTopViewMarginRight(mTopFragmentMarginRight);
        mDraggableView.setTopViewMarginBottom(mTopFragmentMarginBottom);
        mDraggableView.attachBottomFragment(mBottomFragment);
        mDraggableView.setDraggableListener(mDraggableListener);
        mDraggableView.setClickToMaximizeEnabled(mIsEnableClickToMaximize);
        mDraggableView.setClickToMinimizeEnabled(mIsEnableClickToMinimize);
        mDraggableView.setTouchEnabled(mIsEnableTouchListener);
    }

    /**
     * Checks if the top Fragment is maximized.
     *
     * @return true if the view is maximized.
     */
    public boolean isMaximized() {
        return mDraggableView.isMaximized();
    }

    private void initializeAttrs(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.draggable_panel);
        this.mTopFragmentHeight =
                attributes.getDimensionPixelSize(R.styleable.draggable_panel_top_fragment_height,
                        heightTopFragment);
        this.mTopFragmentMarginRight =
                attributes.getDimensionPixelSize(R.styleable.draggable_panel_top_fragment_margin_right,
                        DEFAULT_TOP_FRAGMENT_MARGIN);
        this.mTopFragmentMarginBottom =
                attributes.getDimensionPixelSize(R.styleable.draggable_panel_top_fragment_margin_bottom,
                        DEFAULT_TOP_FRAGMENT_MARGIN);
        this.mIsEnableClickToMaximize =
                attributes.getBoolean(R.styleable.draggable_panel_enable_click_to_maximize_panel,
                        DEFAULT_ENABLE_CLICK_TO_MAXIMIZE);
        this.mIsEnableClickToMinimize =
                attributes.getBoolean(R.styleable.draggable_panel_enable_click_to_minimize_panel,
                        DEFAULT_ENABLE_CLICK_TO_MINIMIZE);
        this.mIsEnableTouchListener =
                attributes.getBoolean(R.styleable.draggable_panel_enable_touch_listener_panel,
                        DEFAULT_ENABLE_TOUCH_LISTENER);
        attributes.recycle();
    }

    /**
     * Validate FragmentManager configuration. If is not initialized, this method will throw an
     * IllegalStateException.
     */
    private void checkSupportFragmentManagerConsistency() {
        if (mFragmentManager == null) {
            throw new IllegalStateException(
                    "You have to set the support FragmentManager before initialize DraggablePanel");
        }
    }

    /**
     * Validate top and bottom Fragment configuration. If are not initialized, this method will throw
     * an IllegalStateException.
     */
    private void checkFragmentConsistency() {
        if (mTopFragment == null || mBottomFragment == null) {
            throw new IllegalStateException(
                    "You have to set top and bottom fragment before initialize DraggablePanel");
        }
    }

}