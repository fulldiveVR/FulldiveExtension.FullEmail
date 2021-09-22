package eu.faircode.email;

/*

*/

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityError extends ActivityBase {
    static final int PI_ERROR = 1;
    static final int PI_ALERT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setSubtitle(getString(R.string.title_setup_error));

        View view = LayoutInflater.from(this).inflate(R.layout.activity_error, null);
        setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        ImageButton ibSetting = view.findViewById(R.id.ibSetting);
        ImageButton ibInfo = view.findViewById(R.id.ibInfo);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        long account = intent.getLongExtra("account", -1L);
        int faq = intent.getIntExtra("faq", -1);

        tvTitle.setText(title);
        tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tvMessage.setText(message);

        ibSetting.setVisibility(account < 0 ? View.GONE : View.VISIBLE);
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), ActivitySetup.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("target", "accounts"));
            }
        });

        ibInfo.setVisibility(faq > 0 ? View.VISIBLE : View.GONE);
        ibInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.viewFAQ(view.getContext(), faq);
            }
        });
    }
}
