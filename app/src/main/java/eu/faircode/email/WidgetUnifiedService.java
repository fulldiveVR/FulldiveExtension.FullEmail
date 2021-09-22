package eu.faircode.email;

/*

*/

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetUnifiedService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetUnifiedRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
