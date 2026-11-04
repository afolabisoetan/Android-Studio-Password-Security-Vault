package com.example.secureprogrammanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class VaultAlarmManager {

    public static void scheduleVaultAutoLock(Context context) {

        // Trigger time = now + 30 seconds
        long triggerMillis = System.currentTimeMillis() + 30_000;

        // Intent that will fire when alarm triggers
        Intent intent = new Intent(context, VaultLockReceiver.class);

        // Clear task so user cannot go back without reauthentication
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent launches an ACTIVITY
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0, // single vault lock alarm (all will have same request code of 0)
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                    );
                }
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                );
            }
        }
    }

    public static void cancelVaultAutoLock(Context context) {
        Intent intent = new Intent(context, VaultLockReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // cancels the alarm (if the user navigates back to the unlock screen before the timer fires)
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // cancels the first alarm with the corresponding pending intent
        }
    }

}
