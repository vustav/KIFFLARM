package com.kiefer.kifflarm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.View;

import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.popups.SetAlarmPopup;

import java.util.ArrayList;

public class ClockView extends View {
    private Context context;
    private SetAlarmPopup setAlarmPopup;
    private Path hourPath, minutePath, shadowPath;
    private Paint handPaint, timeMarkerPaint, touchMarkerPaint, textPaint, snapPaint, shadowMarkerPaint, shadowHandPaint, shadowTxtPaint;
    private int width, height;
    private int snaptHour=-1, snapMinute=-1;
    private Point midPoint;
    private float clockRadius, shortHand, smallHourRadius, longHand, timeMarkerRadius = 40, touchMarkerRadius = 60;
    private boolean initTimeSet = false;

    private int visualTimeOffset = 2; // when rotating the minutes the snap between numbers the chosen number feels off. This offsets that
    private ArrayList<Point> bigHourMarkers, smallHourMarkers, minuteMarkers;

    private int shadowOffset;

    public ClockView(Context context, SetAlarmPopup setAlarmPopup) {
        super(context);
        this.context = context;
        this.setAlarmPopup = setAlarmPopup;
        setup();
    }

    private void setup() {
        hourPath = new Path();
        minutePath = new Path();
        shadowPath = new Path();

        int contrastColor = Utils.getContrastColor(setAlarmPopup.getColor());

        shadowOffset = (int) getResources().getDimension(R.dimen.shadowOffset);

        handPaint = new Paint();
        handPaint.setAntiAlias(true);
        handPaint.setStrokeWidth(14);
        handPaint.setStyle(Paint.Style.STROKE);
        handPaint.setStrokeJoin(Paint.Join.ROUND);
        handPaint.setStrokeCap(Paint.Cap.ROUND);

        timeMarkerPaint = new Paint();
        timeMarkerPaint.setColor(contrastColor);
        timeMarkerPaint.setAntiAlias(true);
        //timeMarkerPaint.setStrokeWidth(1);
        timeMarkerPaint.setStyle(Paint.Style.FILL);
        //timeMarkerPaint.setStrokeJoin(Paint.Join.ROUND);
        timeMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        shadowMarkerPaint = new Paint();
        shadowMarkerPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowMarkerPaint.setAntiAlias(true);
        shadowMarkerPaint.setAlpha(127); //0.5
        shadowMarkerPaint.setStyle(Paint.Style.FILL);
        shadowMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        shadowHandPaint = new Paint();
        shadowHandPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowHandPaint.setAntiAlias(true);
        shadowHandPaint.setStrokeWidth(20);
        shadowHandPaint.setStyle(Paint.Style.STROKE);
        shadowHandPaint.setStrokeJoin(Paint.Join.ROUND);
        shadowHandPaint.setStrokeCap(Paint.Cap.ROUND);
        shadowHandPaint.setAlpha(127);

        touchMarkerPaint = new Paint();
        //touchMarkerPaint.setColor(Utils.getRandomColor());
        touchMarkerPaint.setAntiAlias(true);
        //touchMarkerPaint.setStrokeWidth(14);
        touchMarkerPaint.setStyle(Paint.Style.FILL);
        //touchMarkerPaint.setStrokeJoin(Paint.Join.ROUND);
        touchMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        //textPaint.setColor(contrastColor);
        textPaint.setColor(Utils.getContrastColor(contrastColor));
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(60);
        Typeface italic = ResourcesCompat.getFont(context, R.font.the_italic_font);
        textPaint.setTypeface(italic);

        snapPaint = new Paint();
        snapPaint.setColor(Utils.getContrastColor(contrastColor));
        snapPaint.setAntiAlias(true);
        snapPaint.setStrokeWidth(3);
        snapPaint.setTextSize(60);
        snapPaint.setTypeface(italic);

        shadowTxtPaint = new Paint();
        shadowTxtPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowTxtPaint.setAntiAlias(true);
        shadowTxtPaint.setStrokeWidth(3);
        shadowTxtPaint.setTextSize(60);
        italic = ResourcesCompat.getFont(context, R.font.the_italic_font);
        shadowTxtPaint.setTypeface(italic);
        shadowTxtPaint.setAlpha(127);

        //canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int offset = (int) getResources().getDimension(R.dimen.clockOffset);
        int width = getWidth();
        int height = getHeight();

        float midX = width / 2f;
        float midY = height / 2f;
        midPoint = new Point(midX, midY);

        clockRadius = Math.min(width, height) / 2f - offset / 2f;
        longHand = clockRadius;
        shortHand = clockRadius * .7f;
        smallHourRadius = clockRadius * .7f;

        bigHourMarkers = new ArrayList<>();
        for(int hour = 0; hour < 12; hour++){
            bigHourMarkers.add(getPointFromPoint(midPoint, turnClockwise(convertHourToAngle(hour), 90), clockRadius));
        }

        smallHourMarkers = new ArrayList<>();
        for(int hour = 0; hour < 12; hour++){
            smallHourMarkers.add(getPointFromPoint(midPoint, turnClockwise(convertHourToAngle(hour), 90), smallHourRadius));
        }

        minuteMarkers = new ArrayList<>();
        for(int minute = 0; minute < 60; minute+=5){
            minuteMarkers.add(getPointFromPoint(midPoint, turnClockwise(convertMinuteToAngle(minute), 90), clockRadius));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!initTimeSet) {
            setInitTime();
            initTimeSet = true;
        }

        int textOffset = 23;

        //int markerColor = Utils.getRandomColor();
        //timeMarkerPaint.setColor(markerColor);
        //textPaint.setColor(Utils.getContrastColor(markerColor));

        int handColor = Utils.getRandomColor();
        handPaint.setColor(handColor);
        handPaint.setColor(handColor);
        touchMarkerPaint.setColor(handColor);

        if (setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {

            //small markers and numbers
            for(int hour = 0; hour < smallHourMarkers.size(); hour++){
                if(hour != snaptHour - 12 || snaptHour >= 12) { // snapHour gets another color and is drawn further down
                    Point p = smallHourMarkers.get(hour);
                    canvas.drawCircle(p.x+shadowOffset, p.y+shadowOffset, timeMarkerRadius, shadowMarkerPaint);
                    //canvas.drawText(Integer.toString(hour+12), p.x-textOffset+shadowOffset, p.y+textOffset+shadowOffset, shadowTxtPaint);
                    canvas.drawCircle(p.x, p.y, timeMarkerRadius, timeMarkerPaint);
                    canvas.drawText(Integer.toString(hour+12), p.x-textOffset, p.y+textOffset, textPaint);
                }
            }

            //large markers and numbers
            for(int hour = 0; hour < bigHourMarkers.size(); hour++){
                if(hour != snaptHour || snaptHour <= 12) {
                    Point p = bigHourMarkers.get(hour);
                    canvas.drawCircle(p.x+shadowOffset, p.y+shadowOffset, timeMarkerRadius, shadowMarkerPaint);
                    //canvas.drawText(Integer.toString(hour), p.x-textOffset+shadowOffset, p.y+textOffset+shadowOffset, shadowTxtPaint);
                    canvas.drawCircle(p.x, p.y, timeMarkerRadius, timeMarkerPaint);
                    canvas.drawText(Integer.toString(hour), p.x - textOffset, p.y + textOffset, textPaint);
                }
            }

            //small touch and hand
            if(snaptHour >= 12){
                canvas.drawPath(shadowPath, shadowHandPaint);
                canvas.drawCircle(endPoint.x+shadowOffset, endPoint.y+shadowOffset, touchMarkerRadius, shadowMarkerPaint);
                canvas.drawPath(hourPath, handPaint);
                canvas.drawCircle(endPoint.x, endPoint.y, touchMarkerRadius, touchMarkerPaint);

                //draw snapHour marker and text to match the hand
                snapPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
                Point p = smallHourMarkers.get(snaptHour - 12);
                canvas.drawCircle(p.x, p.y, timeMarkerRadius, touchMarkerPaint);
                canvas.drawText(Integer.toString(snaptHour), p.x-textOffset, p.y+textOffset, snapPaint);
            }

            //large touch and hand
            else{
                canvas.drawPath(shadowPath, shadowHandPaint);
                canvas.drawCircle(endPoint.x+shadowOffset, endPoint.y+shadowOffset, touchMarkerRadius, shadowMarkerPaint);
                canvas.drawPath(hourPath, handPaint);
                canvas.drawCircle(endPoint.x, endPoint.y, touchMarkerRadius, touchMarkerPaint);

                snapPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
                Point p = bigHourMarkers.get(snaptHour);
                canvas.drawCircle(p.x, p.y, timeMarkerRadius, touchMarkerPaint);
                canvas.drawText(Integer.toString(snaptHour), p.x - textOffset, p.y + textOffset, snapPaint);
            }

        }
        else {
            int roundedSnapMinute;
            for(int minute = 0; minute < minuteMarkers.size(); minute++){
                Point p = minuteMarkers.get(minute);
                canvas.drawCircle(p.x+shadowOffset, p.y+shadowOffset, timeMarkerRadius, shadowMarkerPaint);
                canvas.drawCircle(p.x, p.y, timeMarkerRadius, timeMarkerPaint);
                canvas.drawText(Integer.toString(minute * 5), p.x - textOffset, p.y + textOffset, textPaint);
            }

            canvas.drawPath(shadowPath, shadowHandPaint);
            canvas.drawCircle(endPoint.x+shadowOffset, endPoint.y+shadowOffset, touchMarkerRadius, shadowMarkerPaint);
            canvas.drawPath(minutePath, handPaint);
            canvas.drawCircle(endPoint.x, endPoint.y, touchMarkerRadius, touchMarkerPaint);

            snapPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
            Point p = minuteMarkers.get(snapMinute / 5);
            canvas.drawCircle(p.x, p.y, timeMarkerRadius, touchMarkerPaint);
            canvas.drawText(Integer.toString(5*(Math.round((float)(snapMinute - visualTimeOffset)/5))), p.x - textOffset, p.y + textOffset, snapPaint);
        }
    }

    private Point endPoint;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point touch = new Point(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
                    hourPath.reset();
                    shadowPath.reset();
                    hourPath.moveTo(midPoint.x, midPoint.y);
                    shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);

                    endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), shortHand);
                    hourPath.lineTo(endPoint.x, endPoint.y);
                    shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);

                    setAlarmPopup.setHour(convertAngleToHour(turnAntiClock(getAngleBetweenPoints(midPoint, touch), 90)));
                }
                else{
                    minutePath.reset();
                    shadowPath.reset();
                    minutePath.moveTo(midPoint.x, midPoint.y);
                    shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);

                    endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), longHand);
                    minutePath.lineTo(endPoint.x, endPoint.y);
                    shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);

                    setAlarmPopup.setMinute(convertAngleToMinute(turnAntiClock(getAngleBetweenPoints(midPoint, touch), 90)));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {

                    //hour-unit and hand size
                    //105 instead of 90 to get the change in hour (and vibration) between numbers and not on them
                    int hour = convertAngleToHour(turnAntiClock(getAngleBetweenPoints(midPoint, touch), 105));
                    float handSize = longHand;
                    if(getDistanceBetweenPoints(midPoint, touch) < smallHourRadius){
                        //if touch is close to the center, add 12 to get hours 12-23
                        hour += 12;
                        handSize = shortHand;
                    }

                    hourPath.reset();
                    shadowPath.reset();
                    hourPath.moveTo(midPoint.x, midPoint.y);
                    shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);

                    endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), handSize);
                    hourPath.lineTo(endPoint.x, endPoint.y);
                    shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);

                    //if not check if ner hour and draw if it is
                    if(hour != snaptHour) {
                        snaptHour = hour;
                        Utils.performHapticFeedback(this);
                        setAlarmPopup.setHour(snaptHour);
                    }
                }
                //minute
                else{
                    minutePath.reset();
                    shadowPath.reset();
                    minutePath.moveTo(midPoint.x, midPoint.y);
                    shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);

                    endPoint = getPointFromPoint(midPoint, getAngleBetweenPoints(midPoint, touch), longHand);
                    minutePath.lineTo(endPoint.x, endPoint.y);
                    shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);

                    //105 instead of 90 to get the change in hour (and vibration) between numbers and not on them
                    int minute = convertAngleToMinute(turnAntiClock(getAngleBetweenPoints(midPoint, touch), 105));
                    if(minute != snapMinute) {

                        snapMinute = minute;
                        Utils.performHapticFeedback(this);
                        //the -2 is to offset the 105 angle above. Feels better this way.
                        setAlarmPopup.setMinute(snapMinute - visualTimeOffset);
                    }
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

        if (snaptHour == -1) {
            snaptHour = setAlarmPopup.getHour();
        }
        if(snapMinute == -1) {
            snapMinute = setAlarmPopup.getMinute();
        }

        float timeAngle;
        if(setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {
            hourPath.reset();
            shadowPath.reset();
            hourPath.moveTo(midPoint.x, midPoint.y);
            shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);

            float handSize = longHand;
            if (snaptHour > 11) {
                handSize = shortHand;
            }
            timeAngle = turnClockwise(convertHourToAngle(snaptHour), 90);
            endPoint = getPointFromPoint(midPoint, timeAngle, handSize);
            hourPath.lineTo(endPoint.x, endPoint.y);
            shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);
        }
        else {
            minutePath.reset();
            shadowPath.reset();
            minutePath.moveTo(midPoint.x, midPoint.y);
            minutePath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);
            timeAngle = turnClockwise(convertMinuteToAngle(snapMinute), 90);
            endPoint = getPointFromPoint(midPoint, timeAngle, longHand);
            minutePath.lineTo(endPoint.x, endPoint.y);
            shadowPath.lineTo(endPoint.x+shadowOffset, endPoint.y+shadowOffset);
        }

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

    private float getDistanceBetweenPoints(Point p1, Point p2){
        return (float) Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
    }

    private float turnAntiClock(float oldDeg, int degreesToTurn){
        float deg;
        if(oldDeg > 360 - degreesToTurn) {
            deg = oldDeg + degreesToTurn - 360;
        }
        else{
            deg = oldDeg + degreesToTurn;
        }
        return deg;
    }

    private float turnClockwise(float oldDeg, int degreesToTurn){
        float deg;
        if(oldDeg > 360 - degreesToTurn) {
            deg = oldDeg - degreesToTurn;
        }
        else{
            deg = oldDeg - degreesToTurn;
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
