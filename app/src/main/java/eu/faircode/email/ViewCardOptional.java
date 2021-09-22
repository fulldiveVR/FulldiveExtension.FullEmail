package eu.faircode.email;

/*

*/

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

public class ViewCardOptional extends CardView {
    private boolean cards;
    private boolean compact;
    private int margin;
    private int ident;
    private Integer color = null;

    public ViewCardOptional(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ViewCardOptional(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewCardOptional(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        cards = prefs.getBoolean("cards", true);
        compact = prefs.getBoolean("compact", false);

        margin = Helper.dp2pixels(context, compact ? 3 : 6);

        setRadius(cards ? margin : 0);
        setCardElevation(0);
        setCardBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onAttachedToWindow() {
        if (cards) {
            ViewGroup.MarginLayoutParams lparam = (ViewGroup.MarginLayoutParams) getLayoutParams();
            lparam.setMargins(margin, margin, margin, margin);
            setLayoutParams(lparam);
            setContentPadding(margin, margin, margin, margin);
        }

        super.onAttachedToWindow();
    }

    @Override
    public void setCardBackgroundColor(int color) {
        if (this.color == null || this.color != color) {
            this.color = color;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean cards = prefs.getBoolean("cards", true);
            if (cards && color == Color.TRANSPARENT)
                color = Helper.resolveColor(getContext(), R.attr.colorCardBackground);

            super.setCardBackgroundColor(color);
        }
    }
}
