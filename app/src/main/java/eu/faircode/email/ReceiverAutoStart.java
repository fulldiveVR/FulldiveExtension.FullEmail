package eu.faircode.email;

/*

*/

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverAutoStart extends BroadcastReceiver {
    // https://developer.android.com/reference/android/app/AlarmManager#ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action) ||
                Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            EntityLog.log(context, "Received " + intent);

            if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action))
                ApplicationEx.upgrade(context);

            if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) {
                ServiceSynchronize.stop(context);
                ServiceSend.stop(context);
            }

            ServiceSynchronize.boot(context);
            ServiceSend.boot(context);
        }
    }
}
