package com.todolist.aladdalo.todolist.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("aaa", "Service -> onBind");
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        Log.v("aaa", "Authenticator -> " + authenticator.toString());
        return authenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
