package com.example.lsnr;

public class Globals {
    private static Globals instance;

    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();
        return instance;
    }

    private Globals() {
    }

    private boolean recording;

    public boolean getRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
}
