package com.example.lsnr;

// Singleton class to hold certain global variables.
public class Globals {
    private static Globals instance;

    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();
        return instance;
    }

    private Globals() {
    }

    // Track whether or not we are supposed to be listening; button callbacks set; listening thread gets
    private boolean isListening;

    public boolean getIsListening() {
        return isListening;
    }

    public void setIsListening(boolean isListening) {
        this.isListening = isListening;
    }
}
