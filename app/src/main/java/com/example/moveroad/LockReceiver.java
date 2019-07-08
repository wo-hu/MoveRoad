package com.example.moveroad;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LockReceiver extends BroadcastReceiver {

    static public final String ACTION_RESTART_SERVICE="RestartReceiver.restart";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Intent i = new Intent(context, ResultActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
            try {
                Log.d("","리시브의 트라이입니다");
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("","리시브의 스크린 오프입니다");
        }
        else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("","리시브의 부트컴플리트입니다");
        }
    }
}
