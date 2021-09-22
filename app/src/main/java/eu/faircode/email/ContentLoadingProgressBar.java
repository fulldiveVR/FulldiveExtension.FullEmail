package eu.faircode.email;

/*

*/

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentLoadingProgressBar extends ProgressBar {
    private int visibility = VISIBLE;
    private boolean init = false;
    private int delay = VISIBILITY_DELAY;
    private boolean delaying = false;

    private static final int VISIBILITY_DELAY = 1500; // milliseconds

    public ContentLoadingProgressBar(@NonNull Context context) {
        this(context, null);
    }

    public ContentLoadingProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        getAttr(context, attrs);
    }

    public ContentLoadingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttr(context, attrs);
    }

    public ContentLoadingProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttr(context, attrs);
    }

    private void getAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ContentLoadingProgressBar, 0, 0);
        this.delay = a.getInt(R.styleable.ContentLoadingProgressBar_show_delay, VISIBILITY_DELAY);
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
            ApplicationEx.getMainHandler().postDelayed(delayedShow, delay);
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
                ContentLoadingProgressBar.super.setVisibility(VISIBLE);
        }
    };
}
