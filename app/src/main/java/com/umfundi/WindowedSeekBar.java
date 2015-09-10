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
    private int totalAmount, lastAmount, middleAmount, firstAmount;
    private int barWidth;
    private float offset;
    private float minWindow;
    private int minAmount;
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

        minWindow = barWidth * minAmount / totalAmount;

        thumblX = (((float) firstAmount / totalAmount) * barWidth) + thumbl.getWidth();
        thumbrX = (((float) firstAmount + middleAmount) / totalAmount) * barWidth + thumbl.getWidth();
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
        if (selectedThumb == 1) {

            middleAmount = (int) (thumbrX - thumblX) * totalAmount / barWidth;
            lastAmount = (int) (barWidth + thumbl.getWidth() - thumbrX) * totalAmount / barWidth;
            firstAmount = (int) (thumblX - thumbl.getWidth()) * totalAmount / barWidth;

            if (totalAmount - firstAmount - middleAmount - lastAmount > 0) {
                firstAmount = totalAmount - middleAmount - lastAmount;
            }

            if (middleAmount < minAmount) {
                middleAmount = 0;
                thumblX = thumbrX;
                firstAmount = totalAmount - middleAmount - lastAmount;
            }

            middleText.setText(getFormattedAmount(middleAmount));
            lastText.setText(getFormattedAmount(lastAmount));
            firstText.setText(getFormattedAmount(firstAmount));

        } else if (selectedThumb == 2) {

            firstAmount = (int) (thumblX - thumbl.getWidth()) * totalAmount / barWidth;
            middleAmount = (int) (thumbrX - thumblX) * totalAmount / barWidth;
            lastAmount = (int) (barWidth + thumbl.getWidth() - thumbrX) * totalAmount / barWidth;

            if (totalAmount - firstAmount - middleAmount - lastAmount > 0) {
                lastAmount = totalAmount - firstAmount - middleAmount;
            }

            if(middleAmount < minAmount) {
                middleAmount = 0;
                lastAmount = totalAmount - middleAmount - firstAmount;
                thumbrX = thumblX;
            }

            firstText.setText(getFormattedAmount(firstAmount));
            middleText.setText(getFormattedAmount(middleAmount));
            lastText.setText(getFormattedAmount(lastAmount));

        } else {
            firstText.setText(getFormattedAmount(firstAmount));
            middleText.setText(getFormattedAmount(middleAmount));
            lastText.setText(getFormattedAmount(lastAmount));
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

    public void updateBar(float firstAmount, float middleAmount, float lastAmount, float totalAmount, float minMidValue) {

        this.firstAmount = (int) firstAmount;
        this.middleAmount = (int) middleAmount;
        this.lastAmount = (int) lastAmount;
        this.totalAmount = (int) totalAmount;
        this.minAmount = (int) minMidValue;
        updateView();
    }

    public void updateViews(TextView firstEt, TextView middleEt, TextView lastEt) {
        firstText = firstEt;
        middleText = middleEt;
        lastText = lastEt;
    }

    private String getFormattedAmount(int amount) {
        DecimalFormat formatter;
        formatter = new DecimalFormat("##,##,##,###");
        String mAmount = formatter.format(amount);
        return mAmount;
    }

}
