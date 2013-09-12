package com.cyanogenmod.settings.device;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class DeviceService extends Service  {

    private static final String TAG = "DeviceService";
    private BroadcastReceiver mPowerKeyReceiver = null;   
        
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }    

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregisterReceiver();
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        Log.d(TAG, "onStart");
        registBroadcastReceiver();
    }
      
    private void registBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mPowerKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();
    
                if (strAction.equals(Intent.ACTION_SCREEN_OFF)){
                    Log.d(TAG, "scren off");
                }
                if (strAction.equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d(TAG, "scren on");
                }
            }
        };

        getApplicationContext().registerReceiver(mPowerKeyReceiver, theFilter);
    }

    private void unregisterReceiver() {
        try {
            getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
        }
        catch (IllegalArgumentException e) {
            mPowerKeyReceiver = null;
        }
    }
}
