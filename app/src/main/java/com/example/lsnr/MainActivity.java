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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar that isn't used for anything yet.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the on-screen instructions.
        final TextView textView = findViewById(R.id.mainMessage);
        textView.setText(getResources().getString(R.string.instructions));

        // Check for permission to use the microphone.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }

        // Click button to start listening by setting the global "isListening" to true.
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity", "START CLICKED");
                if (!Globals.getInstance().getIsListening()) {
                    Globals.getInstance().setIsListening(true);

                    TextView textView = findViewById(R.id.mainMessage);
                    textView.setText(getResources().getString(R.string.listening));

                    // Kick off the listening thread.
                    listenAudio();
                } else {
                    Snackbar.make(view, getResources().getString(R.string.already_listening),
                            Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        // Click button to stop listening by setting the global "isListening" to false.
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity", "STOP CLICKED");
                if (Globals.getInstance().getIsListening()) {
                    Globals.getInstance().setIsListening(false);

                    TextView textView = findViewById(R.id.mainMessage);
                    textView.setText(getResources().getString(R.string.instructions));
                } else {
                    Snackbar.make(view, getResources().getString(R.string.already_not_listening),
                            Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    // Create a thread to record audio. Stop when Globals.getIsListening() == false.
    void listenAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!Globals.getInstance().getIsListening()) {
                    Log.v("MainActivity", "STARTED RECORDING THREAD BUT GLOBALS.RECORDING == FALSE -- WHY???");
                }

                // Audio recording method inspired by:
                // https://www.newventuresoftware.com/blog/record-play-and-visualize-raw-audio-data-in-android

                // Set thread priority to support audio recording
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // Determine buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                Log.v("MainActivity", "BUFFER SIZE: " + bufferSize);

                // Allocate audio buffer,
                short[] audioBuffer = new short[bufferSize / 2];

                // Create audio listener.
                AudioRecord audioListener = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                // Start listening.
                audioListener.startRecording();

                long shortsReadTotal = 0;

                // Keep listening while global isListening is true.
                while (Globals.getInstance().getIsListening()) {

                    // Read into the audio buffer.
                    int shortsRead = audioListener.read(audioBuffer, 0, audioBuffer.length);
                    shortsReadTotal += shortsRead;

                    // Send the audio buffer to the wave view and force it to redraw.
                    WaveView waveView = findViewById(R.id.waveView);
                    waveView.setAudioBuffer(audioBuffer);
                    waveView.invalidate();
                }

                // Once the STOP button sets global isListening to false, the while loop ends and we stop listening.
                audioListener.stop();
                audioListener.release();

                Log.v("MainActivity", String.format("Recording stopped. Samples read: %d", shortsReadTotal));
            }
        }).start();
    }

    // Print an array of shorts to the screen.
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
