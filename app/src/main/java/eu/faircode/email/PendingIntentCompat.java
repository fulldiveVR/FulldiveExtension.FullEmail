package eu.faircode.email;

/*
   
*/

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

public class PendingIntentCompat {
    private PendingIntentCompat() {
    }

    public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return PendingIntent.getActivity(context, requestCode, intent, flags);
        else
            return PendingIntent.getActivity(context, requestCode, intent, flags | PendingIntent.FLAG_IMMUTABLE);
    }

    public static PendingIntent getService(Context context, int requestCode, @NonNull Intent intent, int flags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return PendingIntent.getService(context, requestCode, intent, flags);
        else
            return PendingIntent.getService(context, requestCode, intent, flags | PendingIntent.FLAG_IMMUTABLE);
    }

    static PendingIntent getForegroundService(Context context, int requestCode, @NonNull Intent intent, int flags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return PendingIntent.getService(context, requestCode, intent, flags);
        else
            return PendingIntent.getForegroundService(context, requestCode, intent, flags | PendingIntent.FLAG_IMMUTABLE);
    }
}
