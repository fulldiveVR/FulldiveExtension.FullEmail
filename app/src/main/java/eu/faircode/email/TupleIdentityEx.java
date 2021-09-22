package eu.faircode.email;

/*
   
*/

import java.util.Objects;

public class TupleIdentityEx extends EntityIdentity {
    public String accountName;
    public Long drafts;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleIdentityEx) {
            TupleIdentityEx other = (TupleIdentityEx) obj;
            return (super.equals(obj) &&
                    Objects.equals(accountName, other.accountName) &&
                    Objects.equals(drafts, other.drafts));
        } else
            return false;
    }
}
