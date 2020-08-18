package org.wc.counterapp;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CounterService extends Service {

    private static final String TAG = "CounterService";
    private int count;
    private boolean isStop=false;


    //메인액티비티에서 binder.getCount하면 여기 값에 접근 가능
    ICounterService.Stub binder = new ICounterService.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        this.count = intent.getIntExtra("count",100);
        Log.d(TAG, "onBind: 실행됨 : "+count);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: count : "+count);
        intent.putExtra("count",count);
        isStop=true;
        return super.onUnbind(intent);
    }

    public CounterService() {
        Log.d(TAG, "CounterService: 생성자 실행됨");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 서비스 시작");
        Thread counterThread = new Thread(new Counter());
        counterThread.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop=true;
    }

    class Counter implements Runnable{
        @Override
        public void run() {
            for (count=count;count<20;count++){
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
