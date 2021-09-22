package eu.faircode.email;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import java.text.DateFormat;
import java.util.List;

public class FragmentAbout extends FragmentBase {
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSubtitle(R.string.menu_about);
        setHasOptionsMenu(false);

        final Context context = getContext();

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView tvVersion = view.findViewById(R.id.tvVersion);
        TextView tvUpdated = view.findViewById(R.id.tvUpdated);
        tvVersion.setText(getString(R.string.title_version, BuildConfig.VERSION_NAME));

        long last = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(BuildConfig.APPLICATION_ID, 0);
            last = pi.lastUpdateTime;
        } catch (Throwable ex) {
            Log.e(ex);
        }

        DateFormat DF = Helper.getDateTimeInstance(context, DateFormat.SHORT, DateFormat.SHORT);
        tvUpdated.setText(getString(R.string.app_updated, last == 0 ? "-" : DF.format(last)));

        TypedValue style = new TypedValue();
        context.getTheme().resolveAttribute(R.style.TextAppearance_AppCompat_Small, style, true);

        List<Contributor> contributors = Contributor.loadContributors(context);
        for (Contributor contributor : contributors) {
            TextView tv = new TextView(context);
            TextViewCompat.setTextAppearance(tv, style.data);
            tv.setText(contributor.toString());
        }

        FragmentDialogTheme.setBackground(context, view, false);

        return view;
    }
}
