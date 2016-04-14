package com.zxon.myaidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zxon.myaidldemo.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_start_service)
    Button pBtnStartService;
    @Bind(R.id.btn_stop_service)
    Button pBtnStopService;
    @Bind(R.id.btn_bind_service)
    Button pBtnBindService;
    @Bind(R.id.btn_unbind_service)
    Button pBtnUnbindService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("connected MyService");
            MyService.MyBinder binder = (MyService.MyBinder) service;
            binder.startTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("disconnected MyService");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        pStartServiceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MyService.class);
//                startService(intent);
//            }
//        });
//
//        pStopServiceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MyService.class);
//                stopService(intent);
//            }
//        });
    }


    @OnClick({R.id.btn_start_service, R.id.btn_stop_service, R.id.btn_bind_service, R.id.btn_unbind_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                startMyService();
                break;
            case R.id.btn_stop_service:
                stopMyService();
                break;
            case R.id.btn_bind_service:
                bindMyService();
                break;
            case R.id.btn_unbind_service:
                unBindMyService();
                break;
        }
    }

    private void unBindMyService() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        unbindService(mConnection);
    }

    private void bindMyService() {
        Intent intent = new Intent(MainActivity.this, MyService.class);

        boolean result = bindService(intent, mConnection, BIND_AUTO_CREATE);
        LogUtil.d(result + " is the result.");
    }

    private void stopMyService() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
    }

    private void startMyService() {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);
    }

}
