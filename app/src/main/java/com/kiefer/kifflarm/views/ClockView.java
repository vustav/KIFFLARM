package com.kiefer.kifflarm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
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
import java.util.Random;

public class ClockView extends View {
    private Context context;
    private SetAlarmPopup setAlarmPopup;
    private Path hourPath, minutePath, shadowPath, triangleMarkerPath;
    private Paint handPaint, timeMarkerPaint, touchMarkerPaint, textPaint, snapTxtPaint, shadowMarkerPaint, shadowHandPaint, shadowTrianglePaint, shadowTxtPaint;
    private int width, height;
    private int snaptHour=-1, snapMinute=-1;
    private Point midPoint;
    private float clockRadius, shortHand, smallHourRadius, longHand, timeMarkerRadius = 40, touchMarkerRadius = 60;
    private boolean initTimeSet = false;

    private int visualTimeOffset = 2; // when rotating the minutes the snap between numbers the chosen number feels off. This offsets that
    private ArrayList<Marker> bigHourMarkers, smallHourMarkers, minuteMarkers;
    private Random r;
    private int shadowOffset;
    public static int CIRCLE = 0, RECTANGLE = 1, TRIANGLE = 2;

    //private int handPaintColor = Color.BLACK;

    public ClockView(Context context, SetAlarmPopup setAlarmPopup) {
        super(context);
        this.context = context;
        this.setAlarmPopup = setAlarmPopup;
        setup();
    }

    private void setup() {
        r = new Random();

        hourPath = new Path();
        minutePath = new Path();
        shadowPath = new Path();
        triangleMarkerPath = new Path();

        int color = Utils.getRandomColor();
        int contrastColor = Utils.getContrastColor(color);

        shadowOffset = (int) getResources().getDimension(R.dimen.shadowOffset);

        handPaint = new Paint();
        //handPaint.setColor(handPaintColor);
        handPaint.setAntiAlias(true);
        handPaint.setStrokeWidth(10);
        handPaint.setStyle(Paint.Style.STROKE);
        handPaint.setStrokeJoin(Paint.Join.ROUND);
        handPaint.setStrokeCap(Paint.Cap.ROUND);

        shadowHandPaint = new Paint();
        shadowHandPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowHandPaint.setAntiAlias(true);
        shadowHandPaint.setStrokeWidth(10);
        shadowHandPaint.setStyle(Paint.Style.STROKE);
        shadowHandPaint.setStrokeJoin(Paint.Join.ROUND);
        shadowHandPaint.setStrokeCap(Paint.Cap.ROUND);
        shadowHandPaint.setAlpha(127);

        timeMarkerPaint = new Paint();
        timeMarkerPaint.setColor(color);
        timeMarkerPaint.setAntiAlias(true);
        timeMarkerPaint.setStyle(Paint.Style.FILL);
        timeMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        touchMarkerPaint = new Paint();
        //touchMarkerPaint.setColor(handPaintColor);
        touchMarkerPaint.setAntiAlias(true);
        touchMarkerPaint.setStyle(Paint.Style.FILL);
        touchMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        shadowTrianglePaint = new Paint();
        //shadowTrianglePaint.setStrokeWidth(2);
        shadowTrianglePaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowTrianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        shadowTrianglePaint.setAntiAlias(true);
        shadowTrianglePaint.setAlpha(127);

        shadowMarkerPaint = new Paint();
        shadowMarkerPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowMarkerPaint.setAntiAlias(true);
        shadowMarkerPaint.setAlpha(127); //0.5
        shadowMarkerPaint.setStyle(Paint.Style.FILL);
        shadowMarkerPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(contrastColor);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(60);
        Typeface italic = ResourcesCompat.getFont(context, R.font.the_italic_font);
        textPaint.setTypeface(italic);

        snapTxtPaint = new Paint();
        snapTxtPaint.setAntiAlias(true);
        snapTxtPaint.setStrokeWidth(3);
        snapTxtPaint.setTextSize(60);
        snapTxtPaint.setTypeface(italic);

        shadowTxtPaint = new Paint();
        shadowTxtPaint.setColor(ContextCompat.getColor(context, R.color.shadowColor));
        shadowTxtPaint.setAntiAlias(true);
        shadowTxtPaint.setStrokeWidth(3);
        shadowTxtPaint.setTextSize(60);
        shadowTxtPaint.setTypeface(italic);
        shadowTxtPaint.setAlpha(127);
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
            bigHourMarkers.add(new Marker(getPointFromPoint(midPoint, turnClockwise(convertHourToAngle(hour), 90), clockRadius)));
        }

        smallHourMarkers = new ArrayList<>();
        for(int hour = 0; hour < 12; hour++){
            smallHourMarkers.add(new Marker(getPointFromPoint(midPoint, turnClockwise(convertHourToAngle(hour), 90), smallHourRadius)));
        }

        minuteMarkers = new ArrayList<>();
        for(int minute = 0; minute < 60; minute+=5){
            minuteMarkers.add(new Marker(getPointFromPoint(midPoint, turnClockwise(convertMinuteToAngle(minute), 90), clockRadius)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!initTimeSet) {
            setInitTime();
            initTimeSet = true;
        }

        int handColor = Utils.getRandomColor();
        handPaint.setColor(handColor);
        handPaint.setColor(handColor);
        touchMarkerPaint.setColor(handColor);

        if (setAlarmPopup.getSelectedTimeUint() == SetAlarmPopup.HOUR) {

            //12-23 markers
            for(int hour = 0; hour < smallHourMarkers.size(); hour++){
                if(hour != snaptHour - 12 || snaptHour >= 12) { // snapHour gets another color and is drawn further down
                    Marker m = smallHourMarkers.get(hour);
                    Point p = m.point;
                    timeMarkerPaint.setColor(m.color);
                    textPaint.setColor(Utils.getContrastColor(m.color));

                    if(m.shape == CIRCLE) {
                        drawCircleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                    }
                    else if(m.shape == RECTANGLE){
                        drawRectangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                    }
                    else{
                        drawTriangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowTrianglePaint);
                    }
                    canvas.drawText(Integer.toString(hour+12), p.x-m.xOffset, p.y+m.yOffset, textPaint);
                }
            }

            //0-11 markers
            for(int hour = 0; hour < bigHourMarkers.size(); hour++){
                if(hour != snaptHour || snaptHour <= 12) {
                    Marker m = bigHourMarkers.get(hour);
                    Point p = m.point;
                    timeMarkerPaint.setColor(m.color);
                    textPaint.setColor(Utils.getContrastColor(m.color));

                    if(m.shape == CIRCLE) {
                        drawCircleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                    }
                    else if(m.shape == RECTANGLE){
                        drawRectangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                    }
                    else{
                        drawTriangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowTrianglePaint);
                    }
                    canvas.drawText(Integer.toString(hour), p.x-m.xOffset, p.y+m.yOffset, textPaint);
                }
            }

            //12-23 hand and touch
            if(snaptHour >= 12){

                //hand shadow
                canvas.drawPath(shadowPath, shadowHandPaint);

                //touch shadow
                drawCircleShadow(canvas, endPoint, touchMarkerRadius, shadowMarkerPaint);

                //hand
                canvas.drawPath(hourPath, handPaint);

                //touch
                drawCircle(canvas, endPoint, touchMarkerRadius, touchMarkerPaint);

                //draw snapHour marker and text to match the hand
                snapTxtPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
                Marker m = smallHourMarkers.get(snaptHour-12);
                Point p = m.point;

                if(m.shape == CIRCLE) {
                    drawCircle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                else if(m.shape == RECTANGLE){
                    drawRectangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                else{
                    drawTriangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                canvas.drawText(Integer.toString(snaptHour), p.x-m.xOffset, p.y+m.yOffset, snapTxtPaint);
            }

            //0-11 hand and touch
            else{
                //hand shadow
                canvas.drawPath(shadowPath, shadowHandPaint);

                //touch shadow
                drawCircleShadow(canvas, endPoint, touchMarkerRadius, shadowMarkerPaint);

                //hand
                canvas.drawPath(hourPath, handPaint);

                //touch
                drawCircle(canvas, endPoint, touchMarkerRadius, touchMarkerPaint);

                //redraw the og marker with flashing colors
                snapTxtPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
                Marker m = bigHourMarkers.get(snaptHour);
                Point p = m.point;
                if(m.shape == CIRCLE) {
                    drawCircle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                else if(m.shape == RECTANGLE){
                    drawRectangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                else{
                    drawTriangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
                }
                canvas.drawText(Integer.toString(snaptHour), p.x-m.xOffset, p.y+m.yOffset, snapTxtPaint);
            }

        }
        else {
            for(int minute = 0; minute < minuteMarkers.size(); minute++){
                Marker m = minuteMarkers.get(minute);
                Point p = m.point;
                timeMarkerPaint.setColor(m.color);
                textPaint.setColor(Utils.getContrastColor(m.color));

                if(m.shape == CIRCLE) {
                    drawCircleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                }
                else if(m.shape == RECTANGLE){
                    drawRectangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowMarkerPaint);
                }
                else{
                    drawTriangleWithShadow(canvas, p, timeMarkerRadius, timeMarkerPaint, shadowTrianglePaint);
                }
                canvas.drawText(Integer.toString(minute * 5), p.x-m.xOffset, p.y+m.yOffset, textPaint);
            }

            //hand shadow
            canvas.drawPath(shadowPath, shadowHandPaint);

            //touch shadow
            drawCircleShadow(canvas, endPoint, touchMarkerRadius, shadowMarkerPaint);

            //hand
            canvas.drawPath(minutePath, handPaint);

            //touch
            drawCircle(canvas, endPoint, touchMarkerRadius, touchMarkerPaint);

            //redraw the og marker with flashing colors
            snapTxtPaint.setColor(Utils.getContrastColor(touchMarkerPaint.getColor()));
            Marker m = minuteMarkers.get(snapMinute / 5);
            Point p = m.point;
            if(m.shape == CIRCLE) {
                drawCircle(canvas, p, timeMarkerRadius, touchMarkerPaint);
            }
            else if(m.shape == RECTANGLE){
                drawRectangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
            }
            else{
                drawTriangle(canvas, p, timeMarkerRadius, touchMarkerPaint);
            }
            canvas.drawText(Integer.toString(5*(Math.round((float)(snapMinute - visualTimeOffset)/5))), p.x - m.xOffset, p.y + m.yOffset, snapTxtPaint);
        }
    }

    private void drawCircleWithShadow(Canvas canvas, Point p, float radius, Paint paint, Paint shadowPaint){
        drawCircleShadow(canvas, p, radius, shadowPaint);
        drawCircle(canvas, p, radius, paint);
    }

    private void drawCircle(Canvas canvas, Point p, float radius, Paint paint){
        canvas.drawCircle(p.x, p.y, radius, paint);
    }

    private void drawCircleShadow(Canvas canvas, Point p, float radius, Paint shadowPaint){
        canvas.drawCircle(p.x+shadowOffset, p.y+shadowOffset, radius, shadowPaint);
    }

    private void drawRectangleWithShadow(Canvas canvas, Point p, float radius, Paint paint, Paint shadowPaint){
        drawRectangleShadow(canvas, p, radius, shadowPaint);
        drawRectangle(canvas, p, radius, paint);
    }

    private void drawRectangle(Canvas canvas, Point p, float radius, Paint paint){
        canvas.drawRect(p.x-radius, p.y-radius, p.x+radius, p.y+radius, paint);
    }

    private void drawRectangleShadow(Canvas canvas, Point p, float radius, Paint shadowPaint){
        canvas.drawRect(p.x-radius+shadowOffset, p.y-radius+shadowOffset, p.x+radius+shadowOffset, p.y+radius+shadowOffset, shadowPaint);
    }

    private void drawTriangleWithShadow(Canvas canvas, Point p, float radius, Paint paint, Paint shadowPaint){
        drawTriangleShadow(canvas, p, radius, shadowPaint);
        drawTriangle(canvas, p, radius, paint);
    }

    private void drawTriangle(Canvas canvas, Point p, float radius, Paint paint){
        triangleMarkerPath.reset();
        triangleMarkerPath.setFillType(Path.FillType.EVEN_ODD);
        triangleMarkerPath.moveTo(p.x-radius, p.y+radius);
        triangleMarkerPath.lineTo(p.x+radius, p.y+radius);
        triangleMarkerPath.lineTo(p.x, p.y-radius);
        triangleMarkerPath.close();
        canvas.drawPath(triangleMarkerPath, paint);
    }

    private void drawTriangleShadow(Canvas canvas, Point p, float radius, Paint shadowPaint){
        triangleMarkerPath.reset();
        triangleMarkerPath.setFillType(Path.FillType.EVEN_ODD);
        triangleMarkerPath.moveTo(p.x-radius+shadowOffset, p.y+radius+shadowOffset);
        triangleMarkerPath.lineTo(p.x+radius+shadowOffset, p.y+radius+shadowOffset);
        triangleMarkerPath.lineTo(p.x+shadowOffset, p.y-radius+shadowOffset);
        triangleMarkerPath.close();
        canvas.drawPath(triangleMarkerPath, shadowPaint);
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
                        int offsetTime = snapMinute - visualTimeOffset;
                        if(offsetTime < 0){
                            offsetTime = 60 + offsetTime; // to avoid time: -1 and -2
                        }
                        setAlarmPopup.setMinute(offsetTime);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                //handPaint.setColor(handPaintColor);
                //touchMarkerPaint.setColor(handPaintColor);
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
            shadowPath.moveTo(midPoint.x+shadowOffset, midPoint.y+shadowOffset);
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

    /** CLASS MARKER **/
    private class Marker{
        private Point point;
        private int shape, xOffset, yOffset;
        private int color;

        private Marker(Point point){
            this.point = point;
            shape = r.nextInt(3);
            color = Utils.getRandomColor();
            xOffset = Utils.getRandomPositiveOffset(13, 43);
            yOffset = Utils.getRandomPositiveOffset(12, 42);
        }
    }
}
