package eu.faircode.email;

/*

*/

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class EditTextPlain extends FixedEditText {
    public EditTextPlain(Context context) {
        super(context);
        Helper.setKeyboardIncognitoMode(this, context);
    }

    public EditTextPlain(Context context, AttributeSet attrs) {
        super(context, attrs);
        Helper.setKeyboardIncognitoMode(this, context);
    }

    public EditTextPlain(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Helper.setKeyboardIncognitoMode(this, context);
    }

    @Override
    protected void onAttachedToWindow() {
        // Spellchecker workaround
        boolean enabled = isEnabled();
        super.setEnabled(true);
        super.onAttachedToWindow();
        super.setEnabled(enabled);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection ic = super.onCreateInputConnection(editorInfo);
        if (ic == null)
            return null;

        return new InputConnectionWrapper(ic, true) {
            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                return super.commitText(text.toString(), newCursorPosition);
            }

            @Override
            public CharSequence getSelectedText(int flags) {
                try {
                    return super.getSelectedText(flags);
                } catch (Throwable ex) {
                    Log.w(ex);
                    return null;
                    /*
                        java.lang.IndexOutOfBoundsException: getChars (-1 ... 52) starts before 0
                          at android.text.SpannableStringBuilder.checkRange(SpannableStringBuilder.java:1314)
                          at android.text.SpannableStringBuilder.getChars(SpannableStringBuilder.java:1191)
                          at android.text.TextUtils.getChars(TextUtils.java:100)
                          at android.text.SpannableStringBuilder.<init>(SpannableStringBuilder.java:67)
                          at android.text.SpannableStringBuilder.subSequence(SpannableStringBuilder.java:1183)
                          at android.view.inputmethod.BaseInputConnection.getSelectedText(BaseInputConnection.java:528)
                          at android.view.inputmethod.InputConnectionWrapper.getSelectedText(InputConnectionWrapper.java:94)
                          at com.android.internal.view.IInputConnectionWrapper.executeMessage(IInputConnectionWrapper.java:286)
                          at com.android.internal.view.IInputConnectionWrapper$MyHandler.handleMessage(IInputConnectionWrapper.java:85)
                          at android.os.Handler.dispatchMessage(Handler.java:106)
                     */
                }
            }
        };
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        try {
            if (id == android.R.id.paste) {
                Context context = getContext();
                ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cbm != null && cbm.hasPrimaryClip()) {
                    ClipData data = cbm.getPrimaryClip();
                    ClipData.Item item = (data == null ? null : data.getItemAt(0));
                    CharSequence text = (item == null ? null : item.coerceToText(context));
                    if (text != null) {
                        data = ClipData.newPlainText("coerced_plain_text", text.toString());
                        cbm.setPrimaryClip(data);
                    }
                }
            }

            return super.onTextContextMenuItem(id);
        } catch (Throwable ex) {
            Log.e(ex);
            return false;
        }
    }
}
