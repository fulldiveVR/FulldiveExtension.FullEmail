/*
 * This file is part of FairEmail.
 *     FairEmail is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     FairEmail is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.
 *     Copyright 2018-2021 by Marcel Bokhorst (M66B)
 */

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
