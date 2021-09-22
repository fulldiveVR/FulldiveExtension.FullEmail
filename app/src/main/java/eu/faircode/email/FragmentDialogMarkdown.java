package eu.faircode.email;

/*
   
*/

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;

public class FragmentDialogMarkdown extends FragmentDialogBase {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View dview = LayoutInflater.from(getContext()).inflate(R.layout.dialog_markdown, null);
        final TextView tvMarkdown = dview.findViewById(R.id.tvMarkdown);
        final ContentLoadingProgressBar pbWait = dview.findViewById(R.id.pbWait);

        tvMarkdown.setText(null);

        Dialog dialog = new Dialog(getContext());
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dview);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        new SimpleTask<Spanned>() {
            @Override
            protected void onPreExecute(Bundle args) {
                tvMarkdown.setVisibility(View.GONE);
                pbWait.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Bundle args) {
                tvMarkdown.setVisibility(View.VISIBLE);
                pbWait.setVisibility(View.GONE);
            }

            @Override
            protected Spanned onExecute(Context context, Bundle args) throws Throwable {
                String name = args.getString("name");

                String markdown;
                String asset = Helper.getLocalizedAsset(context, name);
                try (InputStream is = context.getAssets().open(asset)) {
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    markdown = new String(buffer);
                }

                String locale = Helper.getFAQLocale();
                if (locale != null)
                    markdown = markdown.replace(
                            "https://github.com/M66B/Full Email/blob/master/FAQ.md",
                            "https://github.com/M66B/Full Email/blob/master/docs/FAQ-" + locale + ".md");

                Markwon markwon = Markwon.builder(context)
                        .usePlugin(HtmlPlugin.create())
                        .build();
                return markwon.toMarkdown(markdown);
            }

            @Override
            protected void onExecuted(Bundle args, Spanned markdown) {
                tvMarkdown.setText(markdown);
                tvMarkdown.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected void onException(Bundle args, Throwable ex) {
                Log.unexpectedError(getParentFragmentManager(), ex);
            }
        }.execute(this, getArguments(), "markdown:read");

        return dialog;
    }
}
