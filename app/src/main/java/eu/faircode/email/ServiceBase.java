package eu.faircode.email;

/*

*/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.lifecycle.LifecycleService;

import java.util.HashMap;
import java.util.Map;

abstract class ServiceBase extends LifecycleService {
    @Override
    public void onCreate() {
        Map<String, String> crumb = new HashMap<>();
        crumb.put("state", "create");
        Log.breadcrumb(this.getClass().getSimpleName(), crumb);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Map<String, String> crumb = new HashMap<>();
        if (intent != null) {
            crumb.put("action", intent.getAction());
            Bundle data = intent.getExtras();
            if (data != null)
                for (String key : data.keySet()) {
                    Object value = data.get(key);
                    crumb.put(key, value == null ? null : value.toString());
                }
        }
        Log.breadcrumb(this.getClass().getSimpleName(), crumb);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Map<String, String> crumb = new HashMap<>();
        crumb.put("state", "destroy");
        Log.breadcrumb(this.getClass().getSimpleName(), crumb);

        super.onDestroy();
    }

    Handler getMainHandler() {
        return ApplicationEx.getMainHandler();
    }
}
