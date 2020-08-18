package org.wc.counterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private TextView tvCount;
    private Button btnStart, btnStop;

    private ICounterService binder;

    //버튼 눌렀을때 쓰임
    private boolean running = true;

    //카운트 값을 계속 그리기 위해서 Handler추가
    private Handler handler = new Handler();

    //Bind할때 파라미터 중간 넘어감
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //함수를 넘겨서 콜백받음
            //바인더가 시작되는 순간부터 들고있는 서비스 객체임
            binder = ICounterService.Stub.asInterface(iBinder); //캐스팅하는법 (문법)
            //binder 타이밍을 잘 잡아야한다.

            //연결확인
            try {
                Log.d(TAG, "onServiceConnected: binder count : " + binder.getCount());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initObject();
        initListener();
    }

    private void initObject(){
        tvCount = findViewById(R.id.tv_count);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
    }


    //BIND_AUTO_CREATE : Component와 연결되어 있는 동안 비정상적으로 종료시 자동으로 다시 시작
    //BIND_DEBUG_UNBIND : 비정상적으로 연결이 끊어지면 로그를 남긴다 (디버깅용)
    //BIND_NOT_FOREGROUND : 백그라운드로만 동작한다. 만약 Activity에서 생성한 경우 Activity와 생성주기를 같이 한다.
    private void initListener(){
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CounterService.class);

                
                //이 밑에가 실행되야 binder가 활성화됨
                bindService(intent, connection, BIND_AUTO_CREATE);
                //쓰레드가 실행된다 = 컨텍스트 스위칭된다.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(running){
                            if(binder !=null){//위 주석들 설명을 읽으면 이 if가 true가 된것은 연결이 되었다는 뜻이다.
                                //여기서 핸들러가 필요하다 runonUI,AscynTask,retropit 필요없고
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            tvCount.setText(binder.getCount()+"");
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                                try {
                                    Thread.sleep(500);//binder를 찾은 후 2초마다 확인
                                    //이 타이밍을 잡는게 어려움!
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
                //여기서 binder에 접근할때 바로 접근하면 생성이 안되어있음!
                //새로운 쓰레드를 만들어서 접근해야한다!

                try {
                    Log.d(TAG, "onClick count : "+binder.getCount());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CounterService.class);
                //stopService(intent); ->
                unbindService(connection);
                running=false;
            }
        });
    }
}