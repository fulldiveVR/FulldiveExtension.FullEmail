package eu.faircode.email;

/*
   
*/

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ViewTextDelayed extends AppCompatTextView {
    private int visibility = VISIBLE;
    private boolean init = false;
    private boolean delaying = false;

    private static final int VISIBILITY_DELAY = 1500; // milliseconds

    public ViewTextDelayed(@NonNull Context context) {
        super(context);
    }

    public ViewTextDelayed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewTextDelayed(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        this.visibility = visibility;

        if (visibility == VISIBLE) {
            if (delaying || (init && super.getVisibility() == VISIBLE))
                return;
            init = true;
            delaying = true;
            super.setVisibility(INVISIBLE);
            ApplicationEx.getMainHandler().postDelayed(delayedShow, VISIBILITY_DELAY);
        } else {
            ApplicationEx.getMainHandler().removeCallbacks(delayedShow);
            delaying = false;
            super.setVisibility(visibility);
        }
    }

    @Override
    public int getVisibility() {
        return this.visibility;
    }

    private final Runnable delayedShow = new Runnable() {
        @Override
        public void run() {
            delaying = false;
            if (visibility == VISIBLE)
                ViewTextDelayed.super.setVisibility(VISIBLE);
        }
    };
}
