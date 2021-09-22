package eu.faircode.email;

/*
   
*/

import java.util.Objects;

public class TupleRuleEx extends EntityRule {
    public long account;
    public String folderName;
    public String accountName;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleRuleEx) {
            TupleRuleEx other = (TupleRuleEx) obj;
            return (super.equals(obj) &&
                    this.account == other.account &&
                    Objects.equals(this.folderName, other.folderName) &&
                    Objects.equals(this.accountName, other.accountName));
        } else
            return false;
    }
}
