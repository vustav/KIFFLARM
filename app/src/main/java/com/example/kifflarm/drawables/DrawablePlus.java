package com.example.kifflarm.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class DrawablePlus extends Drawable {
    private final Paint paint;

    public DrawablePlus() {
        // Set up color and text size
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    @Override
    public void draw(Canvas canvas) {
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();

        //spara för klocka
        //float radius = Math.min(width, height) / 2;

        float off = width/10f;
        canvas.drawRect(width/2f-off, 0, width/2f+off, height, paint);
        canvas.drawRect(0, height/2f-off, width, height/2f+off, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.OPAQUE;
    }
}