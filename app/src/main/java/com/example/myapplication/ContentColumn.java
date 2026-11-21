package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/** Caps the width of a centered XML column while allowing it to fill compact windows. */
public final class ContentColumn extends LinearLayout {
    private final int maxWidth;

    public ContentColumn(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.maxWidth});
        maxWidth = attributes.getDimensionPixelSize(0, Integer.MAX_VALUE);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int availableWidth = MeasureSpec.getSize(widthSpec);
        if (MeasureSpec.getMode(widthSpec) != MeasureSpec.UNSPECIFIED && availableWidth > maxWidth) {
            widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.getMode(widthSpec));
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
