package com.acbelter.directionalcarouseldemo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CoverFlowTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.3f;
    private static final float MIN_ALPHA = 0.3f;
    private int spacing;
    private int selectedSpacing;
    private float rotateFactor;
    private float rotateLimit;
    private float alphaFactor;
    private float scaleFactor;
    private float gravityFactor;
    private float listRadius;

    public int dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int)px;
    }

    public CoverFlowTransformer(Context context, AttributeSet attrs) {

        spacing = dpToPx(context, -300);
        selectedSpacing = dpToPx(context, 0);
        rotateFactor = 70.0f;
        rotateLimit = 70.0f;
        alphaFactor = 0.3f;
        scaleFactor = 0.1f;
        gravityFactor = -5.0f;
        listRadius = 2.0f;
        listRadius *= listRadius;
    }

    @Override
    public void transformPage(View view, float position) {
        if (rotateFactor != 0f) {
            float rotateAngle = Math.min(rotateLimit, Math.abs(position * rotateFactor));
            view.setRotationY((position < 0) ? rotateAngle : -rotateAngle);
        }

        if (spacing != 0) {
            float hMargin = position * (spacing);
            if (selectedSpacing != 0) {
                float ss = Math.min(selectedSpacing, Math.abs(position * selectedSpacing));
                hMargin += (position > 0) ? ss : -ss;
            }
            System.out.println(hMargin);
            view.setTranslationX(hMargin);
        }

        float vMargin = position * position * gravityFactor / listRadius;
        view.setTranslationY(vMargin);


        if (scaleFactor != 0f) {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(position * scaleFactor));
            view.setScaleX(scale);
            view.setScaleY(scale);
        }

        if (alphaFactor != 0f) {
            view.setAlpha(Math.max(MIN_ALPHA, 1 - Math.abs(position * alphaFactor)));
        }
    }
}
