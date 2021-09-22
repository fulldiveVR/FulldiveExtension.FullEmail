package eu.faircode.email;

/*

*/

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

@TargetApi(Build.VERSION_CODES.N)
public class ServiceTileSynchronize extends TileService implements SharedPreferences.OnSharedPreferenceChangeListener {
    public void onStartListening() {
        Log.i("Start tile synchronize");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        update();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if ("enabled".equals(key))
            update();
    }

    private void update() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = prefs.getBoolean("enabled", true);
        Log.i("Update tile synchronize=" + enabled);

        Tile tile = getQsTile();
        if (tile != null)
            try {
                tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
                tile.setIcon(Icon.createWithResource(this,
                        enabled ? R.drawable.twotone_sync_24 : R.drawable.twotone_sync_disabled_24));
                tile.updateTile();
            } catch (Throwable ex) {
                Log.w(ex);
                /*
                    java.lang.IllegalArgumentException: Service not registered: com.android.systemui.qs.external.TileLifecycleManager@9b4b3b0
                      at android.os.Parcel.createException(Parcel.java:1954)
                      at android.os.Parcel.readException(Parcel.java:1918)
                      at android.os.Parcel.readException(Parcel.java:1868)
                      at android.service.quicksettings.IQSService$Stub$Proxy.updateQsTile(IQSService.java:219)
                      at android.service.quicksettings.Tile.updateTile(Tile.java:182)
                      at eu.faircode.email.ServiceTileSynchronize.update(SourceFile:56)
                      at eu.faircode.email.ServiceTileSynchronize.onStartListening(SourceFile:37)
                      at android.service.quicksettings.TileService$H.handleMessage(TileService.java:407)
                */
            }
    }

    public void onStopListening() {
        Log.i("Stop tile synchronize");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onClick() {
        Log.i("Click tile synchronize");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = !prefs.getBoolean("enabled", true);
        prefs.edit().putBoolean("enabled", enabled).apply();
    }
}
