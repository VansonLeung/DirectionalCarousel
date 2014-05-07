package com.acbelter.directionalcarousel;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

public class CarouselViewPager extends ViewPager {
    private static final String TAG = "CarouselViewPager";

    private int mViewPagerWidth;
    private int mViewPagerHeight;

    private int mMinOffset = 20;
    private float mVisiblePart = 0.5f;

    private int mPageLimit;
    private int mPageMargin;

    private Resources mResources;
    // FIXME It is static field because ViewPager cache own pages
    private static CarouselConfig sConfig = new CarouselConfig();

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        sConfig.pagerId = getId();
        mResources = context.getResources();

        DisplayMetrics metrics = mResources.getDisplayMetrics();
        mMinOffset *= metrics.density;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselViewPager);
        try {
            if (a != null) {
                sConfig.orientation = a.getInt(R.styleable.CarouselViewPager_android_orientation,
                        CarouselConfig.HORIZONTAL);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public static CarouselConfig getConfig() {
        return sConfig;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        CarouselState ss = new CarouselState(super.onSaveInstanceState());
        ss.position = getCurrentItem();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof CarouselState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        CarouselState ss = (CarouselState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setCurrentItem(ss.position);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewPagerWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewPagerHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (sConfig.orientation == CarouselConfig.VERTICAL) {
            setRotation(90);
            sConfig.scaleX = (float) mViewPagerHeight / mViewPagerWidth;
            sConfig.scaleY = (float) mViewPagerWidth / mViewPagerHeight;
            setScaleX(sConfig.scaleX);
            setScaleY(sConfig.scaleY);
        }

        setMeasuredDimension(mViewPagerWidth, mViewPagerHeight);

        try {
            calculatePageLimitAndMargin();
        } catch (CarouselConfigException e) {
            Log.e(TAG, e.toString());
        }

        setOffscreenPageLimit(mPageLimit);
        setPageMargin(mPageMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void calculatePageLimitAndMargin() throws CarouselConfigException {
        if (sConfig.orientation != CarouselConfig.HORIZONTAL
                && sConfig.orientation != CarouselConfig.VERTICAL) {
            throw new IllegalArgumentException("Invalid orientation.");
        }

        int contentSize, viewSize;
        if (sConfig.orientation == CarouselConfig.HORIZONTAL) {
            contentSize = mResources.getDimensionPixelSize(R.dimen.page_content_width);
            viewSize = mViewPagerWidth;
        } else {
            contentSize = mResources.getDimensionPixelSize(R.dimen.page_content_height);
            viewSize = mViewPagerHeight;
        }

        int minOffset = (int) (CarouselConfig.DIFF_SCALE * contentSize / 2) + mMinOffset;
        contentSize *= CarouselConfig.SMALL_SCALE;

        if (contentSize + 2*minOffset > viewSize) {
            throw new CarouselConfigException("Page content is too large.");
        }

        final float step = 0.1f;
        while (contentSize + 2*contentSize*(mVisiblePart-step) + 2*minOffset > viewSize
                && Math.abs(mVisiblePart-step) > 1e-6) {
            mVisiblePart -= step;
        }

        int fullPages = 0;
        final int s = viewSize - (int) (2*contentSize*mVisiblePart);
        while (minOffset + (fullPages+1)*(contentSize + minOffset) <= s) {
            fullPages++;
        }

        if (fullPages != 0 && fullPages%2 == 0) {
            fullPages--;
        }

        int offset = (s - fullPages * contentSize) / (fullPages + 1);

        if (Math.abs(mVisiblePart) > 1e-6) {
            mPageLimit = (fullPages + 2) - 1;
        } else {
            mPageLimit = fullPages - 1;
        }
        sConfig.visiblePages = mPageLimit + 1;

        if (sConfig.orientation == CarouselConfig.VERTICAL) {
            mPageMargin = -(int) ((viewSize - contentSize - offset) * sConfig.scaleY);
        } else {
            mPageMargin = -(viewSize - contentSize - offset);
        }
    }

    public static class CarouselConfigException extends Exception {
        public CarouselConfigException(String msg) {
            super(msg);
        }
    }

    public static class CarouselState extends BaseSavedState {
        int position;

        public CarouselState(Parcelable superState) {
            super(superState);
        }

        private CarouselState(Parcel in) {
            super(in);
            position = in.readInt();
        }

        public static final Parcelable.Creator<CarouselState> CREATOR =
                new Parcelable.Creator<CarouselState>() {
            @Override
            public CarouselState createFromParcel(Parcel in) {
                return new CarouselState(in);
            }

            @Override
            public CarouselState[] newArray(int size) {
                return new CarouselState[0];
            }
        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
        }
    }
}