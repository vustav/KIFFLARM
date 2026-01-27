package com.kiefer.kifflarm.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class CSeekBar extends View {
    public static final int VERTICAL_DOWN_UP = 0, VERTICAL_UP_DOWN = 1, HORIZONTAL_LEFT_RIGHT = 2, HORIZONTAL_RIGHT_LEFT = 3;

    protected final float INTERNAL_MAX = 1; //for internal calculations
    protected float multiplier = 1; //used to get bigger ranges than 0-1
    protected final int INIT_PROGRESS_COLOR = Color.LTGRAY, INIT_BG_COLOR = Color.WHITE, INIT_THUMB_COLOR = Color.DKGRAY, INIT_BORDER_COLOR = Color.BLACK;

    protected float x, y, oldX, oldY;

    protected float value = 0;

    protected boolean thumb = true;
    protected int thumbSize = 10;

    protected boolean border = true;
    protected int borderSize = 4;

    protected int margin = 5;
    protected int width;
    protected int height;
    protected Bitmap bitmap;
    protected Paint bitmapPaint, progressPaint, backgroundPaint, thumbPaint, borderPaint;

    //default
    private int  orientation = VERTICAL_DOWN_UP;


    public CSeekBar(Context context, int orientation) {
        super(context);

        if(orientation == VERTICAL_DOWN_UP){
            this.orientation = VERTICAL_DOWN_UP;
        }
        else if(orientation == VERTICAL_UP_DOWN){
            this.orientation = VERTICAL_UP_DOWN;
        }
        else if(orientation == HORIZONTAL_LEFT_RIGHT){
            this.orientation = HORIZONTAL_LEFT_RIGHT;
        }
        else if(orientation == HORIZONTAL_RIGHT_LEFT){
            this.orientation = HORIZONTAL_RIGHT_LEFT;
        }

        //PAINTS
        bitmapPaint = new Paint();

        progressPaint = new Paint();
        progressPaint.setColor(INIT_PROGRESS_COLOR);
        progressPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(INIT_BG_COLOR);
        backgroundPaint.setStyle(Paint.Style.FILL);

        thumbPaint = new Paint();
        thumbPaint.setColor(INIT_THUMB_COLOR);
        thumbPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setStrokeWidth(borderSize);
        borderPaint.setColor(INIT_BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(w > 0 && h > 0) { //has crashed when screen goes to sleep without this check
            width = w;
            height = h;
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            //uses width/height and needs an update
            setProgress(value, false);
        }
    }

    @Override
    protected void onDraw(Canvas aCanvas) {
        super.onDraw(aCanvas);

        final Canvas canvas = aCanvas;

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);

        int borderOffset = borderSize / 2;

        if (orientation == VERTICAL_DOWN_UP) {
            canvas.drawRect(0 + margin, 0 + margin, width - margin, y, backgroundPaint);
            canvas.drawRect(0 + margin, y, width - margin, height, progressPaint);

            if(border){
                canvas.drawRect(borderOffset + margin, borderOffset + margin, width - borderOffset - margin, y, borderPaint);
                canvas.drawRect(borderOffset + margin, y, width - borderOffset - margin, height - borderOffset, borderPaint);
            }

            if(thumb){
                if(y < thumbSize){
                    y = thumbSize;
                }
                else if(y > height - thumbSize){
                    y = height - thumbSize;
                }

                canvas.drawRect(0, y - thumbSize, width, y + thumbSize, thumbPaint);
            }
        } else if (orientation == VERTICAL_UP_DOWN) {
            canvas.drawRect(0 + margin, 0 + margin, width - margin, y, progressPaint);
            canvas.drawRect(0 + margin, y, width - margin, height, backgroundPaint);
        } else if (orientation == HORIZONTAL_LEFT_RIGHT) {
            canvas.drawRect(0 + margin, 0 + margin, x, height - margin, progressPaint);
            canvas.drawRect(x, 0 + margin, width - margin, height - margin, backgroundPaint);

            if(border){
                canvas.drawRect(borderOffset + margin, borderOffset + margin, x, height - borderOffset - margin, borderPaint);
                canvas.drawRect(x, borderOffset + margin, width - borderOffset - margin, height - borderOffset - margin, borderPaint);
            }

            if(thumb){
                if(x < thumbSize){
                    x = thumbSize;
                }
                else if(x > width - thumbSize){
                    x = width - thumbSize;
                }

                canvas.drawRect(x - thumbSize, 0, x + thumbSize, height, thumbPaint);
            }
        } else if (orientation == HORIZONTAL_RIGHT_LEFT) {
            canvas.drawRect(0 + margin, 0 + margin, x, height, backgroundPaint);
            canvas.drawRect(x, 0 + margin, width - margin, height - margin, progressPaint);

            if(thumb){
                if(x < thumbSize){
                    x = thumbSize;
                }
                else if(x > width - thumbSize){
                    x = width - thumbSize;
                }

                canvas.drawRect(x - thumbSize, 0, x + thumbSize, height, thumbPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();

        value = eventToValue(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                invalidate(); //calls onDraw()
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //
                break;
        }

        oldX = x;
        oldY = y;
        return true;
    }

    public float eventToValue(MotionEvent event){
        this.x = event.getX();
        this.y = event.getY();

        float value = 0;

        if(orientation == VERTICAL_DOWN_UP) {
            value = INTERNAL_MAX - y / height;
        }
        else if(orientation == VERTICAL_UP_DOWN) {
            value = y / height;
        }
        else if(orientation == HORIZONTAL_LEFT_RIGHT) {
            value = x / width;
        }
        else if(orientation == HORIZONTAL_RIGHT_LEFT) {
            value = INTERNAL_MAX - x / width;
        }

        if(value < 0){
            value = 0;
        }
        else if(value > INTERNAL_MAX){
            value = INTERNAL_MAX;
        }

        return value;
    }

    public boolean isEventOnThumb(MotionEvent event){
        float margin = .1f;
        return eventToValue(event) >= value - margin && eventToValue(event) <= value + margin;
    }

    @Override
    public boolean performClick(){
        super.performClick();
        return true;
    }

    public void setMax(float multiplier){
        this.multiplier = multiplier;
    }

    public float getProgress(){
        return value * multiplier;
    }

    public void setProgress(float value){
        setProgress(value, true);
    }

    private void setProgress(float aValue, boolean useMultiplier){

        if(useMultiplier) {
            this.value = aValue / multiplier;
        }
        else{
            this.value = aValue;
        }

        //x/y are whats used for drawing, update them too
        if(orientation == VERTICAL_DOWN_UP) {
            y = height * (INTERNAL_MAX - value);
        }
        else if(orientation == VERTICAL_UP_DOWN) {
            y = height * value;
        }
        else if(orientation == HORIZONTAL_LEFT_RIGHT) {
            x = width * value;
        }
        else if(orientation == HORIZONTAL_RIGHT_LEFT) {
            x = width * (INTERNAL_MAX - value);
        }
        invalidate();
    }

    public void setProgressColor(int color){
        progressPaint.setColor(color);
        invalidate();
    }

    public void setBgColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setThumbColor(int color){
        thumbPaint.setColor(color);
        invalidate();
    }

    public void setColors(int progressColor, int bgColor){
        setProgressColor(progressColor);
        setBgColor(bgColor);
    }

    public void setColors(int progressColor, int bgColor, int thumbColor){
        setProgressColor(progressColor);
        setBgColor(bgColor);
        setThumbColor(thumbColor);
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setThumb(boolean thumb) {
        this.thumb = thumb;
    }

    public void setBorder(boolean border){
        this.border = border;
    }

    public void setThumbSize(int thumbSize) {
        this.thumbSize = thumbSize;
    }

    public float getMax(){
        return multiplier;
    }
}