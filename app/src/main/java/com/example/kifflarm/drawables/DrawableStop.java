package com.example.kifflarm.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class DrawableStop extends Drawable {
    private final Paint paint;

    public DrawableStop() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.drawRect(0, 0, width, height, paint);
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
        return PixelFormat.OPAQUE;
    }
}