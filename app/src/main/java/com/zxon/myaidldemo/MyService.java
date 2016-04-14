package com.zxon.myaidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.zxon.myaidldemo.util.LogUtil;

public class MyService extends Service {

    public static final int MSG_FROM_CLIENT = 1;
    public static final int MSG_FROM_SERVICE = 100;

    // for intent when binding service
    public static final String BUNDLE_EXTRA = "ipc_type";
    public static final String BUNDLE_KEY = "key_type";
    public static final int IPC_TYPE_MESSENGER = 0;
    public static final int IPC_TYPE_AIDL = 1;

    // for bundle in msg
    public static final String BUNDLE_KEY_MSG = "key_between_client_and_service";


    private MyBinder mBinder = new MyBinder();

    private Messenger mMessenger = new Messenger(new MessengerHandler());

    private ICallback mCallback;

    private IMyAidlInterface.Stub mAidlBinder = new IMyAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public CustomBean getServerResult(CustomBean info, ICallback callback) throws RemoteException {

            LogUtil.d("the info from the client is : " + info.getName() + " | " + info.getAge());

            if (callback != null) {
                mCallback = callback;
                mCallback.handleByServer("handle something by server");
            } else {
                LogUtil.d("the callback is null");
            }

            CustomBean resultInfo = new CustomBean();
            resultInfo.setAge(1024);
            resultInfo.setName("your father");
            return resultInfo;
        }
    };

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.d("MyService onCreate");

        LogUtil.d(getClass().getName() + " is the name ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("MyService onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("MyService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle != null) {

            switch (bundle.getInt(BUNDLE_KEY, IPC_TYPE_AIDL)) {
                case IPC_TYPE_AIDL:
                    LogUtil.d("in service, decide to return mAidlBinder");
                    return mAidlBinder;
                case IPC_TYPE_MESSENGER:
                    return mMessenger.getBinder();
                default:
                    return mBinder;
            }

        } else {

            return mBinder;

        }

    }


    class MyBinder extends Binder {
        public void startTask() {
            LogUtil.d("start doing the task");
        }
    }

    static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    LogUtil.d("get msg from client, the content is : " + msg.getData().getString(BUNDLE_KEY_MSG));
                    Messenger toClient = msg.replyTo;
                    Message replyMessage = Message.obtain();
                    replyMessage.what = MSG_FROM_SERVICE;
                    Bundle bundle = new Bundle();
                    bundle.putString(BUNDLE_KEY_MSG, "爹知道了");
                    replyMessage.setData(bundle);
                    try {
                        toClient.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
