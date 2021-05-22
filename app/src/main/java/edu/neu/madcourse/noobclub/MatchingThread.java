package edu.neu.madcourse.noobclub;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

public class MatchingThread extends HandlerThread {
    private static final String TAG = "MatchingThread";

    public Handler handler;


    public MatchingThread() {
        super(TAG, Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler();
    }

    public Handler getHandler() {
        return handler;
    }
}
