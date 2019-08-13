package com.example.lsnr;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView textView = findViewById(R.id.mainMessage);
        textView.setText("Press START to listen");


        // Check for permission to use the microphone.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }

        // Click button to start recording
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity", "START CLICKED");
                if (!Globals.getInstance().getRecording()) {
                    //Snackbar.make(view, "Started recording", Snackbar.LENGTH_SHORT)
                    //        .setAction("Action", null).show();

                    Globals.getInstance().setRecording(true);

                    TextView textView = findViewById(R.id.mainMessage);
                    textView.setText("LISTENING");

                    listenAudio();
                } else {
                    Snackbar.make(view, "Already listening", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        // Click button to stop recording
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity", "STOP CLICKED");
                if (Globals.getInstance().getRecording()) {
                    //Snackbar.make(view, "Stopped recording", Snackbar.LENGTH_SHORT)
                    //        .setAction("Action", null).show();

                    Globals.getInstance().setRecording(false);

                    TextView textView = findViewById(R.id.mainMessage);
                    textView.setText("Press START to listen");
                } else {
                    Snackbar.make(view, "Already not listening", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    // Create a thread to record audio. Stop when Globals.recording == false.
    void listenAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!Globals.getInstance().getRecording()) {
                    Log.v("MainActivity", "STARTED RECORDING THREAD BUT GLOBALS.RECORDING == FALSE");
                }

                // Heavily inspired by:
                // https://www.newventuresoftware.com/blog/record-play-and-visualize-raw-audio-data-in-android
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                Log.v("MainActivity", "BUFFER SIZE: " + bufferSize);

                short[] audioBuffer = new short[bufferSize / 2];

                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                record.startRecording();

                long shortsRead = 0;

                while (Globals.getInstance().getRecording()) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;

                    //System.out.print(shortsRead + ": ");
                    //printIt(audioBuffer);

                    WaveView waveView = findViewById(R.id.waveView);
                    waveView.setAudioBuffer(audioBuffer);
                    waveView.invalidate();

                    //System.out.print(averageIt(audioBuffer) + " ");
                    //System.out.flush();
                }

                record.stop();
                record.release();

                Log.v("MainActivity", String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }

    // Print a buffer to the screen.
    void printIt(short[] buffer) {
        for (short value : buffer) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    // Return the average of an array of shorts.
    int averageIt(short[] buffer) {
        int sum = 0;
        for (short value : buffer) {
            sum += value;
        }
        return sum / buffer.length;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
