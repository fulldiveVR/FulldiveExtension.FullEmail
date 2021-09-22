package eu.faircode.email;

/*

*/

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenteredImageSpan extends ImageSpan {
    public CenteredImageSpan(final Drawable drawable) {
        this(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
    }

    public CenteredImageSpan(final Drawable drawable, final int verticalAlignment) {
        super(drawable, verticalAlignment);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        getDrawable().draw(canvas);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end,
                       @Nullable Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();

        if (fm != null) {
            int descent = 0;
            int padding = 0;
            if (rect.bottom - (fm.descent - fm.ascent) >= 0) {
                descent = fm.descent;
                padding = rect.bottom - (fm.descent - fm.ascent);
            }

            fm.descent = padding / 2 + descent;
            fm.bottom = fm.descent;

            fm.ascent = -rect.bottom + fm.descent;
            fm.top = fm.ascent;
        }

        return rect.right;
    }
}
