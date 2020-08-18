package org.wc.counterapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CounterService extends Service {

    private static final String TAG = "CounterService";
    private int count;
    private boolean isStop=false;

    public CounterService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread counterThread = new Thread(new Counter());
        counterThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop=true;
    }

    class Counter implements Runnable{
        @Override
        public void run() {
            for (count=0;count<20;count++){

                if(isStop){
                    break;
                }

                try {
                    Log.d(TAG, "run: count : " + count);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
