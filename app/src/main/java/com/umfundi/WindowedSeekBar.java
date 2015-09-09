package com.umfundi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WindowedSeekBar extends ImageView {

    private Bitmap thumbl = BitmapFactory.decodeResource(getResources(),
            R.drawable.split_scrube);
    private Bitmap thumbr = BitmapFactory.decodeResource(getResources(),
            R.drawable.split_scrube);
    private Bitmap centre = BitmapFactory.decodeResource(getResources(),
            R.drawable.splitslider_recharge);
    private Bitmap left_end = BitmapFactory.decodeResource(getResources(),
            R.drawable.splitslider_bankaccount);
    private Bitmap right_end = BitmapFactory.decodeResource(getResources(),
            R.drawable.splitslider_scanpay);

    private float thumblX, thumbrX;
    private Paint paint = new Paint();
    private int selectedThumb;
    private float offset;
    private float minWindow;
    private float totalAmount, lastAmount, middleAmount, firstAmount;
    private float barWidth;
    private float minGap = 0;
    private int barMarginTop = 5;

    private TextView firstText, lastText, middleText;

    public WindowedSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WindowedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowedSeekBar(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getHeight() > 0)
            init();
    }

    private void init() {
        if (thumbl.getHeight() > getHeight())
            getLayoutParams().height = thumbl.getHeight();

        barWidth = getWidth() - thumbl.getWidth() - thumbr.getWidth();
        // initial position here ( should be parameterize )
        updateView();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < thumblX; i++)
            canvas.drawBitmap(left_end, i, barMarginTop, paint);

        for (float i = thumblX; i < thumbrX; i++)
            canvas.drawBitmap(centre, i, barMarginTop, paint);

        for (float i = thumbrX; i < getWidth() - 10; i++)
            canvas.drawBitmap(right_end, i, barMarginTop, paint);

        canvas.drawBitmap(thumbl, thumblX - thumbl.getWidth(), 0, paint);
        canvas.drawBitmap(thumbr, thumbrX, 0, paint);
    }

    private void updateView() {
        if (barWidth == 0)
            return;

        minWindow = barWidth * minGap / totalAmount;

        thumblX = firstAmount / totalAmount * barWidth + thumbl.getWidth();
        thumbrX = ((firstAmount + middleAmount) / totalAmount) * barWidth + thumbl.getWidth();
        showPosition();

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int mx = (int) event.getX();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (mx >= thumblX - thumbl.getWidth() && mx <= thumblX) {
                    selectedThumb = 1;
                    offset = mx - thumblX;
                } else if (mx >= thumbrX && mx <= thumbrX + thumbl.getWidth()) {
                    selectedThumb = 2;
                    offset = thumbrX - mx;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (selectedThumb == 1) {
                    thumblX = mx - offset;
                    if (thumblX < thumbl.getWidth())
                        thumblX = thumbl.getWidth();
                } else if (selectedThumb == 2) {
                    thumbrX = mx + offset;
                }
                break;

            case MotionEvent.ACTION_UP:
                selectedThumb = 0;
                break;
        }

        if (selectedThumb == 2) {
            if (thumbrX > getWidth() - thumbr.getWidth())
                thumbrX = getWidth() - thumbr.getWidth();

            if (thumbrX <= thumblX + minWindow)
                thumbrX = thumblX + minWindow;

        } else if (selectedThumb == 1) {

            if (thumblX < thumbl.getWidth())
                thumblX = thumbl.getWidth();

            if (thumblX > thumbrX - minWindow)
                thumblX = thumbrX - minWindow;
        }

        showPosition();
        invalidate();
        return true;
    }

    private void showPosition() {
        middleAmount = Float.valueOf(String.format("%.2f", (thumbrX - thumblX) * totalAmount / barWidth));


        /*if (selectedThumb == 1) {

            lastText.setText(String.valueOf(middleAmount));

            firstAmount = totalAmount - middleAmount - lastAmount;

            Log.d("TAG", "First Amount tab1 " + firstAmount + " Middle Amount " + middleAmount + " Last Amount " + lastAmount);

            firstText.setText(getFormattedAmount(firstAmount, false));

        } else if (selectedThumb == 2) {

            lastText.setText(getFormattedAmount(middleAmount, false));

            lastAmount = totalAmount - middleAmount - firstAmount;

            Log.d("TAG", "First Amount tab2 " + firstAmount + " Middle Amount " + middleAmount + " Last Amount " + lastAmount);

            middleText.setText(String.valueOf(lastAmount));


        } */
        if (selectedThumb == 1) {

            middleAmount = (thumbrX - thumblX) * totalAmount / barWidth;
            middleAmount = Float.valueOf(roundTo2Decimal(middleAmount));

            firstAmount = totalAmount - middleAmount - lastAmount;
            firstAmount = Float.valueOf(roundTo2Decimal(firstAmount));

            Log.d("TAG", "Tab1" + " Total Amount " + totalAmount + " First Amount " + firstAmount + " Middle Amount " + middleAmount + " Last Amount " + lastAmount);

            middleText.setText(String.valueOf(middleAmount));

            firstText.setText(String.valueOf(firstAmount));

        } else if (selectedThumb == 2) {

            middleAmount = (thumbrX - thumblX) * totalAmount / barWidth;
            middleAmount = Float.valueOf(roundTo2Decimal(middleAmount));

            lastAmount = totalAmount - middleAmount - firstAmount;
            lastAmount = Float.valueOf(roundTo2Decimal(lastAmount));

            Log.d("TAG", "Tab2" + " Total Amount " + totalAmount + " First Amount tab " + firstAmount + " Middle Amount " + middleAmount + " Last Amount " + lastAmount);

            middleText.setText(String.valueOf(middleAmount));

            lastText.setText(String.valueOf(lastAmount));

        } else {

//            firstText.setText(getFormattedAmount((thumblX - thumbl.getWidth()) * totalAmount / barWidth, false));
//            middleText.setText(getFormattedAmount((thumbrX - thumblX) * totalAmount / barWidth, false));
//            lastText.setText(getFormattedAmount(totalAmount - ((thumblX - thumbl.getWidth()) * totalAmount / barWidth) - (thumbrX - thumblX) * totalAmount / barWidth, false));
            firstText.setText(roundTo2Decimal(firstAmount));
            lastText.setText(roundTo2Decimal(lastAmount));
            middleText.setText(roundTo2Decimal(middleAmount));
        }

    }

    private String roundTo2Decimal(float input1) {
        try {
            String input = String.valueOf(input1);
            BigDecimal value = new BigDecimal(input);
            value = value.setScale(2, RoundingMode.HALF_DOWN);

            return String.valueOf(value);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }

        return null;
    }

    public void updateBar(float firstAmount, float middleAmount, float lastAmount, float totalAmount) {

        this.firstAmount = firstAmount;
        this.middleAmount = middleAmount;
        this.lastAmount = lastAmount;
        this.totalAmount = totalAmount;
        updateView();
    }

    public void updateViews(TextView firstEt, TextView middleEt, TextView lastEt) {
        firstText = firstEt;
        middleText = middleEt;
        lastText = lastEt;
    }

    private String getFormattedAmount(float amount, boolean isWholeNumber) {
        DecimalFormat formatter;
        if (isWholeNumber)
            formatter = new DecimalFormat("##,##,##,###.##");
        else
            formatter = new DecimalFormat("##,##,##,##0.00");
        String mAmount = formatter.format(amount);
        return mAmount;
    }

}
