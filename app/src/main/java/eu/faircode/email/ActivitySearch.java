package eu.faircode.email;

/*

*/

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

public class ActivitySearch extends ActivityBase {
    private static final String SEARCH_SCHEME = "eu.faircode.email.search";

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CharSequence query;

        Uri uri = getIntent().getData();
        boolean external = (uri != null && SEARCH_SCHEME.equals(uri.getScheme()));
        if (external)
            query = Uri.decode(uri.toString().substring(SEARCH_SCHEME.length() + 1));
        else
            query = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        Log.i("External search query=" + query);
        Intent view = new Intent(this, ActivityView.class)
                .putExtra(Intent.EXTRA_PROCESS_TEXT, query);
        if (external)
            view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(view);

        finish();
    }
}
