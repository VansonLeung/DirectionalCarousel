package com.acbelter.directionalcarouseldemo;

import android.content.Context;
import android.util.AttributeSet;

import com.acbelter.directionalcarousel.fglayout.ForegroundLinearLayout;

/**
 * Created by Vanson on 19/2/16.
 */
public class CoverFlowCard extends ForegroundLinearLayout {
    public CoverFlowCard(Context context) {
        super(context);
    }

    public CoverFlowCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverFlowCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = this.getMeasuredWidth();
        int h = (w * 5) / 7;

        setMeasuredDimension(w, h);
    }
}
