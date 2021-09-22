package eu.faircode.email;

/*

*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.DatabaseView;

import java.util.Objects;

@DatabaseView(
        viewName = "identity_view",
        value = TupleIdentityView.query
)
public class TupleIdentityView {
    static final String query = "SELECT id, name, email, display, color, synchronize FROM identity";

    @NonNull
    public Long id;
    @NonNull
    public String name;
    @NonNull
    public String email;
    public String display;
    public Integer color;
    @NonNull
    public Boolean synchronize;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleIdentityView) {
            TupleIdentityView other = (TupleIdentityView) obj;
            return (this.id.equals(other.id) &&
                    this.name.equals(other.name) &&
                    this.email.equals(other.email) &&
                    Objects.equals(this.display, other.display) &&
                    Objects.equals(this.color, other.color) &&
                    this.synchronize.equals(other.synchronize));
        } else
            return false;
    }
}
