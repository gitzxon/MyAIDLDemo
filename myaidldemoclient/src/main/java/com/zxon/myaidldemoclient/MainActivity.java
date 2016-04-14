package com.zxon.myaidldemoclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zxon.myaidldemo.CustomBean;
import com.zxon.myaidldemo.ICallback;
import com.zxon.myaidldemo.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {


    public static final int MSG_FROM_CLIENT = 1;
    public static final int MSG_FROM_SERVICE = 100;

    // for intent when binding service
    public static final String BUNDLE_EXTRA = "ipc_type";
    public static final String BUNDLE_KEY = "key_type";
    public static final int IPC_TYPE_MESSENGER = 0;
    public static final int IPC_TYPE_AIDL = 1;

    // for bundle in msg
    public static final String BUNDLE_KEY_MSG = "key_between_client_and_service";


    @butterknife.Bind(R.id.btn_bind_service_msgr)
    Button pBtnBindService;
    @butterknife.Bind(R.id.btn_unbind_service)
    Button pBtnUnbindService;
    @butterknife.Bind(R.id.btn_bind_service_aidl)
    Button pBtnBindServiceViaAidl;


    private Messenger mServiceWithMessenger;
    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    private IMyAidlInterface mAidlService;
    private ICallback.Stub mCallback = new ICallback.Stub() {

        @Override
        public void handleByServer(String s) throws RemoteException {
            LogUtil.d("successfully getting the result from the service : " + s);
        }

    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("connected MyService from ipc client");

            mServiceWithMessenger = new Messenger(service);

            Bundle data = new Bundle();
            data.putString(BUNDLE_KEY_MSG, "hello, this is client.");

            Message msg = Message.obtain();
            msg.what = MSG_FROM_CLIENT;
            msg.setData(data);
            msg.replyTo = mGetReplyMessenger;

            try {
                mServiceWithMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("disconnected MyService from ipc client");

        }
    };

    private ServiceConnection mConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("connected MyService from ipc client");

            mAidlService = IMyAidlInterface.Stub.asInterface(service);

            CustomBean info = new CustomBean();
            info.setName("client");
            info.setAge(123);
            if (mAidlService != null) {

                try {

                    CustomBean result = mAidlService.getServerResult(info, mCallback);
                    LogUtil.d("the result from the server is : " + result.getName() + " | " + result.getAge());

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("disconnected MyService from ipc client");

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterknife.ButterKnife.bind(this);
    }

    @butterknife.OnClick({R.id.btn_bind_service_msgr, R.id.btn_unbind_service, R.id.btn_bind_service_aidl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_service_msgr:
                bindMyService();
                break;
            case R.id.btn_unbind_service:
                unbindMyService();
                break;
            case R.id.btn_bind_service_aidl:
                bindMyServiceViaAidl();
                break;
        }
    }

    private void bindMyServiceViaAidl() {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY, IPC_TYPE_AIDL); // 0 means messenger 1 means aidl

        Intent intent = new Intent();
        intent.setClassName("com.zxon.myaidldemo", "com.zxon.myaidldemo.MyService");
        intent.putExtra(BUNDLE_EXTRA, bundle);
        boolean result = bindService(intent, mConnection1, BIND_AUTO_CREATE);
        LogUtil.d("the result to bindService is : " + result);
    }

    private void bindMyService() {

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY, IPC_TYPE_MESSENGER); // 0 means messenger 1 means aidl

        Intent intent = new Intent();
        intent.setClassName("com.zxon.myaidldemo", "com.zxon.myaidldemo.MyService");
        intent.putExtra(BUNDLE_EXTRA, bundle);
        boolean result = bindService(intent, mConnection, BIND_AUTO_CREATE);
        LogUtil.d("the result to bindService is : " + result);
    }

    private void unbindMyService() {

    }

    static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_SERVICE:
                    LogUtil.d("the reply message is : " + msg.getData().getString(BUNDLE_KEY_MSG));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
