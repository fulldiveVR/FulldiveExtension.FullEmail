package eu.faircode.email;

/*

*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.DatabaseView;

import java.util.Objects;

@DatabaseView(
        viewName = "folder_view",
        value = TupleFolderView.query
)
public class TupleFolderView {
    static final String query = "SELECT id, account, name, type, display, color, unified, notify, read_only FROM folder";

    @NonNull
    public Long id;
    public Long account;
    @NonNull
    public String name;
    @NonNull
    public String type;
    public String display;
    public Integer color;
    @NonNull
    public Boolean unified = false;
    @NonNull
    public Boolean notify = false;
    @NonNull
    public Boolean read_only = false;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleFolderView) {
            TupleFolderView other = (TupleFolderView) obj;
            return (this.id.equals(other.id) &&
                    Objects.equals(this.account, other.account) &&
                    this.name.equals(other.name) &&
                    this.type.equals(other.type) &&
                    Objects.equals(this.display, other.display) &&
                    Objects.equals(this.color, other.color) &&
                    this.unified == other.unified &&
                    this.notify == other.notify &&
                    this.read_only == other.read_only);
        } else
            return false;
    }
}
