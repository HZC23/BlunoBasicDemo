package com.hzc23.nonocontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CompassView extends View {
    private Paint paint;
    private Path path;
    private float heading = 0;

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY) - 20;

        // Draw compass background
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw compass border
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw heading needle
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        canvas.save();
        canvas.rotate(heading, centerX, centerY);

        path.reset();
        path.moveTo(centerX, centerY - radius + 10);
        path.lineTo(centerX - 20, centerY);
        path.lineTo(centerX + 20, centerY);
        path.close();

        canvas.drawPath(path, paint);

        canvas.restore();

        // Draw center circle
        paint.setColor(Color.RED);
        canvas.drawCircle(centerX, centerY, 10, paint);
    }

    public void setHeading(float heading) {
        this.heading = heading;
        invalidate(); // Redraw the view
    }
}
