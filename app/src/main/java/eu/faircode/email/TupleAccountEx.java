package eu.faircode.email;

/*
   
*/

import java.util.Objects;

public class TupleAccountEx extends EntityAccount {
    public int unseen;
    public int identities; // synchronizing
    public Long drafts;
    public Long sent;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleAccountEx) {
            TupleAccountEx other = (TupleAccountEx) obj;
            return (super.equals(obj) &&
                    this.unseen == other.unseen &&
                    this.identities == other.identities &&
                    Objects.equals(this.drafts, other.drafts) &&
                    Objects.equals(this.sent, other.sent));
        } else
            return false;
    }
}
