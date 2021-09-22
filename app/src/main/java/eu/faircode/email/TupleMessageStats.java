package eu.faircode.email;

/*
   
*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class TupleMessageStats {
    public Long account;
    public Integer unseen;
    public Integer notifying;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleMessageStats) {
            TupleMessageStats other = (TupleMessageStats) obj;
            return (Objects.equals(this.account, other.account) &&
                    Objects.equals(this.unseen, other.unseen) &&
                    Objects.equals(this.notifying, other.notifying));
        } else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "unseen=" + unseen + " notify=" + notifying;
    }
}
