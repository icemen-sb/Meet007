package ru.relastic.meet007;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MyService extends Service {
    public final static String MSG_SERVICE_RNDVALUE = "rnd_value";
    private final static int INTERVAL = 3000;
    private final Random  myRandom = new Random();
    private volatile boolean interrupted = false;
    private final List<Handler> mClients = new ArrayList<>();
    private final IBinder mBinder = new LocalBinder();
    private final ProtectedService protectedService = new ProtectedService () {
        @Override
        public void addListener(Handler handler) {
            mClients.add(handler);
        }

        @Override
        public void removeListener(Handler handler) {
            mClients.remove(handler);
        }

        @Override
        public String getNextValue() {
            return getNextRNDValue();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.v("LOG:", "SERVICE MyService: Started");
                while (!isInterrupted()) {
                    String messageInt = getNextRNDValue();
                    for (Handler client: mClients) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString(MSG_SERVICE_RNDVALUE,messageInt);
                        msg.setData(bundle);
                        client.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.v("LOG:", "ERROR: "+e.toString());
                    }
                }
                Log.v("LOG:", "SERVICE MyService: Stopped");
                stopSelf();
            }
        });
        t.setName("WorkThread");
        t.start();
    }


    private String getNextRNDValue(){
        return ((Integer)(myRandom.nextInt(899)+100)).toString();
    }
    private boolean isInterrupted(){
        return interrupted;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        interrupt();
        super.onDestroy();
    }

    private void interrupt(){
        interrupted = true;
    }
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context,MyService.class);
        return intent;
    }
    public class LocalBinder extends Binder {
        ProtectedService getProtectedService(){
            return protectedService;
        }
    }
    public interface ProtectedService {
        public void addListener(Handler handler);
        public void removeListener(Handler handler);
        public String getNextValue();
    }
}
