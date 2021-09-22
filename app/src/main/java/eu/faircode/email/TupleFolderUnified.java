package eu.faircode.email;

import java.util.Objects;

public class TupleFolderUnified {
    public String type;
    public int messages;
    public int unseen;
    public String sync_state;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleFolderUnified) {
            TupleFolderUnified other = (TupleFolderUnified) obj;
            return (Objects.equals(this.type, other.type) &&
                    this.messages == other.messages &&
                    this.unseen == other.unseen &&
                    Objects.equals(this.sync_state, other.sync_state));
        } else
            return false;
    }
}
