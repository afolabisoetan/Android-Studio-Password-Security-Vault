package com.example.secureprogrammanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VaultLockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Lock session immediately
        SessionManager.getInstance().lock();

        // Launch unlock UI explicitly
        Intent unlockIntent = new Intent(context, UnlockActivity.class);
        unlockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(unlockIntent);
    }
}
