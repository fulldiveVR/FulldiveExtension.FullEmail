package eu.faircode.email;

/*

*/

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class FragmentEula extends FragmentBase {
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSubtitle(R.string.title_welcome);

        View view = inflater.inflate(R.layout.fragment_eula, container, false);

        TextView tvGplV3 = view.findViewById(R.id.tvGplV3);
        tvGplV3.setPaintFlags(tvGplV3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGplV3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.view(view.getContext(), Uri.parse(Helper.LICENSE_URI), true);
            }
        });

        Button btnAgree = view.findViewById(R.id.btnOk);
        Button btnDisagree = view.findViewById(R.id.btnCancel);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                prefs.edit().putBoolean("eula", true).apply();
            }
        });

        btnDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }
}
