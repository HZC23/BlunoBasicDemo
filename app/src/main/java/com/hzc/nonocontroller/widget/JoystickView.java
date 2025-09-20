
package com.hzc.nonocontroller.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent, int angle, int strength);
    }

    private JoystickListener joystickCallback;

    private Paint outerCirclePaint;
    private Paint innerCirclePaint;

    private float innerCirclePositionX;
    private float innerCirclePositionY;
    private float innerCircleRadius;

    private float outerCirclePositionX;
    private float outerCirclePositionY;
    private float outerCircleRadius;

    private float baseRadius;

    public JoystickView(Context context) {
        super(context);
        init(null);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.DKGRAY);
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        outerCirclePositionX = w / 2f;
        outerCirclePositionY = h / 2f;
        innerCirclePositionX = w / 2f;
        innerCirclePositionY = h / 2f;

        int smallerDim = Math.min(w, h);
        outerCircleRadius = smallerDim / 3f;
        innerCircleRadius = smallerDim / 6f;
        baseRadius = outerCircleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(outerCirclePositionX, outerCirclePositionY, outerCircleRadius, outerCirclePaint);
        canvas.drawCircle(innerCirclePositionX, innerCirclePositionY, innerCircleRadius, innerCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateInnerCirclePosition(x, y);
                break;
            case MotionEvent.ACTION_UP:
                resetInnerCirclePosition();
                break;
        }
        return true;
    }

    private void updateInnerCirclePosition(float x, float y) {
        float deltaX = x - outerCirclePositionX;
        float deltaY = y - outerCirclePositionY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > baseRadius) {
            float ratio = baseRadius / (float) distance;
            innerCirclePositionX = outerCirclePositionX + deltaX * ratio;
            innerCirclePositionY = outerCirclePositionY + deltaY * ratio;
        } else {
            innerCirclePositionX = x;
            innerCirclePositionY = y;
        }

        invalidate();

        if (joystickCallback != null) {
            float xPercent = (innerCirclePositionX - outerCirclePositionX) / baseRadius;
            float yPercent = (innerCirclePositionY - outerCirclePositionY) / baseRadius;
            joystickCallback.onJoystickMoved(xPercent, yPercent, getAngle(), getStrength());
        }
    }

    private void resetInnerCirclePosition() {
        innerCirclePositionX = outerCirclePositionX;
        innerCirclePositionY = outerCirclePositionY;
        invalidate();

        if (joystickCallback != null) {
            joystickCallback.onJoystickMoved(0, 0, 0, 0);
        }
    }

    public void setJoystickListener(JoystickListener listener) {
        this.joystickCallback = listener;
    }

    private int getAngle() {
        float deltaX = innerCirclePositionX - outerCirclePositionX;
        float deltaY = innerCirclePositionY - outerCirclePositionY;
        double angleRad = Math.atan2(deltaY, deltaX);
        int angle = (int) Math.toDegrees(angleRad);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    private int getStrength() {
        float deltaX = innerCirclePositionX - outerCirclePositionX;
        float deltaY = innerCirclePositionY - outerCirclePositionY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return (int) (100 * distance / baseRadius);
    }
}
