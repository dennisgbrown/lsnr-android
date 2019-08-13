package com.example.lsnr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WaveView extends View {
    Paint paint = null;
    protected short[] audioBuffer = null;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    public void setAudioBuffer(short[] audioBuffer) {
        this.audioBuffer = audioBuffer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //Log.v("WaveView", "WIDTH: " + width + " HEIGHT: " + height);

        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);

        // Draw a circle for no real reason
        int radius;
        radius = 100;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        paint.setColor(Color.WHITE);

        if (audioBuffer != null) {
            //Log.v("WaveView", "DRAW SHORTS: " + audioBuffer.length);

            for (int i = 0; i < width; i++) {
                canvas.drawLine(i, height / 2, i, (height / 2) + audioBuffer[i], paint);
            }
        }
    }
}
