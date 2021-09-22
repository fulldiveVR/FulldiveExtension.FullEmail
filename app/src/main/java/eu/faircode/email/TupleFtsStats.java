package eu.faircode.email;

/*

*/

import androidx.annotation.Nullable;

public class TupleFtsStats {
    public long fts;
    public long total;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleFtsStats) {
            TupleFtsStats other = (TupleFtsStats) obj;
            return (this.fts == other.fts && this.total == other.total);
        } else
            return false;
    }
}
