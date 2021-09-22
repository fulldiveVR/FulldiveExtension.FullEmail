package eu.faircode.email;

/*

*/

import android.os.Parcel;
import android.text.style.LeadingMarginSpan;

public class IndentSpan extends LeadingMarginSpan.Standard {
    public IndentSpan(int first, int rest) {
        super(first, rest);
    }

    public IndentSpan(int every) {
        super(every);
    }

    public IndentSpan(Parcel src) {
        super(src);
    }
}
