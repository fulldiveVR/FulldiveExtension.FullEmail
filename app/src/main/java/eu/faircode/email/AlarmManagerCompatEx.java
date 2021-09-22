package eu.faircode.email;

/*

*/

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.preference.PreferenceManager;

public class AlarmManagerCompatEx {
    static void setAndAllowWhileIdle(
            @NonNull Context context, @NonNull AlarmManager am,
            int type, long trigger, @NonNull PendingIntent pi) {
        if (hasExactAlarms(context))
            try {
                AlarmManagerCompat.setExactAndAllowWhileIdle(am, type, trigger, pi);
            } catch (SecurityException ex) {
                Log.w(ex);
                AlarmManagerCompat.setAndAllowWhileIdle(am, type, trigger, pi);
            }
        else
            AlarmManagerCompat.setAndAllowWhileIdle(am, type, trigger, pi);
    }

    static boolean hasExactAlarms(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean exact_alarms = prefs.getBoolean("exact_alarms", true);
        return (exact_alarms && canScheduleExactAlarms(context));
    }

    static boolean canScheduleExactAlarms(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R)
            return true;
        else {
            // https://developer.android.com/about/versions/12/behavior-changes-12#exact-alarm-permission
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            return am.canScheduleExactAlarms();
        }
    }
}
