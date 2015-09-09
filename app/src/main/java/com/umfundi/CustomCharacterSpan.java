package com.umfundi;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;


public class CustomCharacterSpan extends MetricAffectingSpan {
    private double ratio = 0.2;
    private Context context;
    private int dip;

    public CustomCharacterSpan(Context mContext, int dip) {

        this.context = mContext;
        this.dip = dip;
    }

    public CustomCharacterSpan(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * ratio);
        paint.setTextSize(context.getResources().getDimensionPixelSize(dip));
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setTextSize(context.getResources().getDimensionPixelSize(dip));
    }
}