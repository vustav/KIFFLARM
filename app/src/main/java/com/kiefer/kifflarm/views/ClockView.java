package com.kiefer.kifflarm.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.popups.SetAlarmPopup;

public class ClockView extends View {
    private SetAlarmPopup setAlarmPopup;
    private Path hourPath, minutePath;
    private Paint hourPaint, minutePaint;
    //private Paint canvasPaint;
    private Bitmap canvasBitmap;
    //private Canvas drawCanvas;
    private int width, height;
    private Point midPoint;
    private float radius;

    public ClockView(Context context, SetAlarmPopup setAlarmPopup) {
        super(context);
        this.setAlarmPopup = setAlarmPopup;
        setup();
    }

    private void setup() {
        hourPath = new Path();
        minutePath = new Path();

        hourPaint = new Paint();
        hourPaint.setColor(Utils.getRandomColor());
        hourPaint.setAntiAlias(true);
        hourPaint.setStrokeWidth(20);
        hourPaint.setStyle(Paint.Style.STROKE);
        hourPaint.setStrokeJoin(Paint.Join.ROUND);
        hourPaint.setStrokeCap(Paint.Cap.ROUND);

        minutePaint = new Paint();
        minutePaint.setColor(Utils.getRandomColor());
        minutePaint.setAntiAlias(true);
        minutePaint.setStrokeWidth(14);
        minutePaint.setStyle(Paint.Style.STROKE);
        minutePaint.setStrokeJoin(Paint.Join.ROUND);
        minutePaint.setStrokeCap(Paint.Cap.ROUND);

        //canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getWidth();
        height = getHeight();

        float midX = width / 2f;
        float midY = height / 2f;
        midPoint = new Point(midX, midY);

        radius = Math.min(width, height) / 2f;

        // Create bitmap and canvas for persistent drawing
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //drawCanvas = new Canvas(canvasBitmap);

        //Log.e("ClockView ZZZ", "onSizeChanged");
    }

    private boolean initTimeSet = false;
    @Override
    protected void onDraw(Canvas canvas) {

        if(!initTimeSet){
            setInitTime();
            initTimeSet = true;
        }
        // Draw the persistent bitmap first
        //canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        // Then draw the current path being traced

        if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
            canvas.drawPath(hourPath, hourPaint);
        }
        else{
            canvas.drawPath(minutePath, minutePaint);
        }
        //Log.e("ClockView ZZZ", "onDraw");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point touch = new Point(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
                    hourPath.reset();
                    hourPath.moveTo(midPoint.x, midPoint.y);

                    Point endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), radius);
                    hourPath.lineTo(endPoint.x, endPoint.y);

                    setAlarmPopup.setHour(convertAngleToHour(turn90AntiClock(getAngleBetweenPoints(midPoint, touch))));
                }
                else{
                    minutePath.reset();
                    minutePath.moveTo(midPoint.x, midPoint.y);

                    Point endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), radius);
                    minutePath.lineTo(endPoint.x, endPoint.y);

                    setAlarmPopup.setMinute(convertAngleToMinute(turn90AntiClock(getAngleBetweenPoints(midPoint, touch))));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
                    hourPath.reset();
                    hourPath.moveTo(midPoint.x, midPoint.y);

                    Point endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), radius);
                    hourPath.lineTo(endPoint.x, endPoint.y);

                    setAlarmPopup.setHour(convertAngleToHour(turn90AntiClock(getAngleBetweenPoints(midPoint, touch))));
                }
                else{
                    minutePath.reset();
                    minutePath.moveTo(midPoint.x, midPoint.y);

                    Point endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), radius);
                    minutePath.lineTo(endPoint.x, endPoint.y);

                    setAlarmPopup.setMinute(convertAngleToMinute(turn90AntiClock(getAngleBetweenPoints(midPoint, touch))));
                }
                break;

            case MotionEvent.ACTION_UP:
                if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
                    setAlarmPopup.switchTimeUnit();
                }
                break;

            default:
                return false;
        }

        // Trigger redraw
        invalidate();
        return true;
    }

    public void setInitTime(){

        hourPath.reset();
        hourPath.moveTo(midPoint.x, midPoint.y);

        float timeAngle = turn90Clock(convertHourToAngle(setAlarmPopup.getHour()));
        Point endPoint = getPointFromPoint(midPoint, timeAngle, radius);
        hourPath.lineTo(endPoint.x, endPoint.y);

        minutePath.reset();
        minutePath.moveTo(midPoint.x, midPoint.y);
        timeAngle = turn90Clock(convertMinuteToAngle(setAlarmPopup.getMinute()));
        endPoint = getPointFromPoint(midPoint, timeAngle, radius);
        minutePath.lineTo(endPoint.x, endPoint.y);

        invalidate();
    }

    public void updateTimeUnit(){
        initTimeSet = false;
        invalidate();
    }

    /** MATH **/
    private int convertAngleToHour(float angle){
        float angleProc = angle / 360f;
        return (int) (angleProc * 12f);
    }
    private float convertHourToAngle(int time){
        return (time * 360f) / 12f;
    }
    private int convertAngleToMinute(float angle){
        float angleProc = angle / 360f;
        return (int) (angleProc * 60f);
    }
    private float convertMinuteToAngle(int time){
        return (time * 360f) / 60f;
    }
    private float getAngleBetweenPoints(Point p1, Point p2) throws IllegalArgumentException{

        // Step 1: Compute the vector from A to B
        float deltaX = p2.x - p1.x;
        float deltaY = p2.y - p1.y;

        // Handle edge case: identical points (vector is zero, angle undefined)
        if (deltaX == 0 && deltaY == 0) {
            throw new IllegalArgumentException("Points A and B are identical; angle is undefined.");
        }

        // Step 2: Calculate raw angle in radians using atan2(deltaY, deltaX)
        float angleRadians = (float) Math.atan2(deltaY, deltaX);

        // Step 3: Convert radians to degrees
        float angleDegrees = (float) Math.toDegrees(angleRadians);

        // Step 4: Adjust to 0-359° range
        float adjustedAngle = (angleDegrees + 360) % 360;

        //return turn90AntiClock(adjustedAngle);
        return adjustedAngle;
    }

    private float turn90AntiClock(float oldDeg){
        float deg;
        if(oldDeg > 360 - 90) {
            deg = oldDeg + 90 - 360;
        }
        else{
            deg = oldDeg + 90;
        }
        return deg;
    }

    private float turn90Clock(float oldDeg){
        float deg;
        if(oldDeg > 360 - 90) {
            deg = oldDeg - 90;
        }
        else{
            deg = oldDeg - 90;
        }
        return deg;
    }

    private Point getPointFromPoint(Point oldPoint, float angle, float distance){
        double radians = Math.toRadians(angle);
        double x = oldPoint.x + distance * Math.cos(radians);
        double y = oldPoint.y + distance * Math.sin(radians);
        return new Point((float) x, (float) y);
    }

    /** CLASS POINT **/
    private class Point{
        private float x, y;
        private Point(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
}
