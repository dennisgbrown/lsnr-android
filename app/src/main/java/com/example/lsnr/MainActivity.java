package com.example.lsnr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    Snackbar.make(view, "Started recording", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    Globals.getInstance().setRecording(true);
                    recordAudio();
                } else {
                    Snackbar.make(view, "Already recording", Snackbar.LENGTH_SHORT)
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
                    Snackbar.make(view, "Stopped recording", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    Globals.getInstance().setRecording(false);
                } else {
                    Snackbar.make(view, "Already not recording", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    // Create a thread to record audio. Stop when Globals.recording == false.
    void recordAudio() {
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

                    System.out.print(shortsRead + ": ");
                    printIt(audioBuffer);

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
