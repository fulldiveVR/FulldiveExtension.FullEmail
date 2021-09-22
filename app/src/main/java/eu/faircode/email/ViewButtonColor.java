package eu.faircode.email;

/*

*/

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.ColorUtils;

public class ViewButtonColor extends AppCompatButton {
    private int color = Color.TRANSPARENT;

    public ViewButtonColor(Context context) {
        super(context);
    }

    public ViewButtonColor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewButtonColor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, this.color);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setColor(savedState.getColor());
    }

    void setColor(Integer color) {
        if (color == null)
            color = Color.TRANSPARENT;
        this.color = color;

        GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setStroke(
                Helper.dp2pixels(getContext(), 1),
                Helper.resolveColor(getContext(), R.attr.colorSeparator));
        setBackground(background);

        if (color == Color.TRANSPARENT)
            setTextColor(Helper.resolveColor(getContext(), android.R.attr.textColorPrimary));
        else {
            double lum = ColorUtils.calculateLuminance(color);
            setTextColor(lum < 0.5 ? Color.WHITE : Color.BLACK);
        }
    }

    int getColor() {
        return this.color;
    }

    static class SavedState extends View.BaseSavedState {
        private int color;

        private SavedState(Parcelable superState, int color) {
            super(superState);
            this.color = color;
        }

        private SavedState(Parcel in) {
            super(in);
            color = in.readInt();
        }

        public int getColor() {
            return this.color;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(color);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
