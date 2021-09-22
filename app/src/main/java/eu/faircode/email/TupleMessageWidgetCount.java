package eu.faircode.email;

/*

*/

import androidx.annotation.Nullable;

public class TupleMessageWidgetCount {
    public long folder;
    public int total;
    public int seen;
    public int flagged;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleMessageWidgetCount) {
            TupleMessageWidgetCount other = (TupleMessageWidgetCount) obj;
            return (this.folder == other.folder &&
                    this.total == other.total &&
                    this.seen == other.seen &&
                    this.flagged == other.flagged);
        } else
            return false;
    }
}
