package eu.faircode.email;

/*

*/

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

public class EditTextMultiAutoComplete extends AppCompatMultiAutoCompleteTextView {
    public EditTextMultiAutoComplete(@NonNull Context context) {
        super(context);
        Helper.setKeyboardIncognitoMode(this, context);
    }

    public EditTextMultiAutoComplete(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Helper.setKeyboardIncognitoMode(this, context);
    }

    public EditTextMultiAutoComplete(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Helper.setKeyboardIncognitoMode(this, context);
    }
}
