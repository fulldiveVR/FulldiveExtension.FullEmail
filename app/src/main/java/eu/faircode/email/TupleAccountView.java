package eu.faircode.email;

/*

*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.util.Objects;

@DatabaseView(
        viewName = "account_view",
        value = TupleAccountView.query
)
public class TupleAccountView {
    static final String query = "SELECT id, pop, name, color, synchronize, notify, leave_deleted, auto_seen, created FROM account";

    @NonNull
    public Long id;
    @NonNull
    @ColumnInfo(name = "pop")
    public Integer protocol;
    public String name;
    public Integer color;
    @NonNull
    public Boolean synchronize;
    @NonNull
    public Boolean notify = false;
    @NonNull
    public Boolean leave_deleted = false;
    @NonNull
    public Boolean auto_seen = true;
    public Long created;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleAccountView) {
            TupleAccountView other = (TupleAccountView) obj;
            return (this.id.equals(other.id) &&
                    this.protocol.equals(other.protocol) &&
                    Objects.equals(this.name, other.name) &&
                    Objects.equals(this.color, other.color) &&
                    this.synchronize.equals(other.synchronize) &&
                    this.notify.equals(other.notify) &&
                    this.leave_deleted.equals(other.leave_deleted) &&
                    this.auto_seen.equals(other.auto_seen) &&
                    Objects.equals(this.created, other.created));
        } else
            return false;
    }
}
