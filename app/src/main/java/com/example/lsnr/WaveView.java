package com.example.lsnr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

// View for drawing an audio waveform.
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

    // Set the audioBuffer to draw for the next onDraw().
    public void setAudioBuffer(short[] audioBuffer) {
        this.audioBuffer = audioBuffer;
    }

    // Draw the audioBuffer that an outside class has set, if it's not null.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //Log.v("WaveView", "WIDTH: " + width + " HEIGHT: " + height);

        // Clear canvas
        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);

        // Draw center line
        paint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawLine(0, height / 2, (width - 1), height / 2, paint);

        // Determine ratio to scale an audio buffer value to the display area
        float heightScaleValue = (float) ((height / 2.0) / Short.MAX_VALUE);
        //Log.v("WaveView", "heightScaleValue: " + heightScaleValue);

        if (audioBuffer != null) {
            //Log.v("WaveView", "audioBuffer length: " + audioBuffer.length);

            // Length of audioBuffer is probably not the same as the width of the canvas,
            // so determine value to increment audioBuffer index as we iterate across the canvas width.
            float incrementValue = (float) (audioBuffer.length / width);

            paint.setColor(getResources().getColor(R.color.colorPrimary));

            // For each column of the canvas, draw the audioBuffer value that maps to that column
            // scaled to fit the height of the canvas.
            for (int i = 0; i < width; i++) {
                canvas.drawLine(i, height / 2, i,
                        (height / 2) + (int) (audioBuffer[(int) (i * incrementValue)] * heightScaleValue),
                        paint);
            }
        }
    }
}
