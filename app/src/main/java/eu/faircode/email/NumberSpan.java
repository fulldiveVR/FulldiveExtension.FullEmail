package eu.faircode.email;

/*

*/

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BulletSpan;

public class NumberSpan extends BulletSpan {
    private int indentWidth;
    private int level;
    private int index;

    private TextPaint tp;
    private String number;
    private int numberWidth;
    private int margin;

    public NumberSpan(int indentWidth, int gapWidth, int color, float textSize, int level, int index) {
        tp = new TextPaint();
        tp.setStyle(Paint.Style.FILL);
        tp.setColor(color);
        tp.setTypeface(Typeface.MONOSPACE);
        tp.setTextSize(textSize);

        this.indentWidth = indentWidth;
        this.level = level;
        this.index = index;

        number = index + ".";
        numberWidth = Math.round(tp.measureText(number));
        margin = numberWidth + gapWidth;
    }

    float getTextSize() {
        return tp.getTextSize();
    }

    int getLevel() {
        return this.level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    int getIndex() {
        return index;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        // https://issuetracker.google.com/issues/36956124
        // This is called before drawLeadingMargin to justify the text
        return indentWidth * (level + 1) + margin;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        if (text instanceof Spanned &&
                ((Spanned) text).getSpanStart(this) == start) {
            float textSize = tp.getTextSize();
            if (textSize > p.getTextSize())
                tp.setTextSize(p.getTextSize());
            int offset = (dir < 0 ? numberWidth : 0);
            c.drawText(number, x + indentWidth * (level + 1) * dir - offset, baseline, tp);
            tp.setTextSize(textSize);
        }
    }
}
