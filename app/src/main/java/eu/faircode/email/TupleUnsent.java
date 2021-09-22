package eu.faircode.email;

/*

*/

import androidx.annotation.Nullable;

import java.util.Objects;

public class TupleUnsent {
    public Integer count;
    public Integer busy;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleUnsent) {
            TupleUnsent other = (TupleUnsent) obj;
            return (Objects.equals(this.count, other.count) &&
                    Objects.equals(this.busy, other.busy));
        } else
            return false;
    }
}
