package eu.faircode.email;

/*

*/

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import static android.app.Activity.RESULT_OK;

public class FragmentDialogColor extends FragmentDialogBase {
    private int color;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("fair:color", color);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        color = (savedInstanceState == null
                ? args.getInt("color")
                : savedInstanceState.getInt("fair:color"));
        String title = args.getString("title");
        boolean reset = args.getBoolean("reset", false);

        Context context = getContext();
        int editTextColor = Helper.resolveColor(context, android.R.attr.editTextColor);

        ColorPickerDialogBuilder builder = ColorPickerDialogBuilder
                .with(context)
                .setTitle(title)
                .showColorEdit(true)
                .setColorEditTextColor(editTextColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(6)
                .lightnessSliderOnly()
                .setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int selectedColor) {
                        color = selectedColor;
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        getArguments().putInt("color", selectedColor);
                        sendResult(RESULT_OK);
                    }
                });

        if (color != Color.TRANSPARENT)
            builder.initialColor(color);

        if (reset)
            builder.setNegativeButton(R.string.title_reset, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getArguments().putInt("color", Color.TRANSPARENT);
                    sendResult(RESULT_OK);
                }
            });

        return builder.build();
    }
}
