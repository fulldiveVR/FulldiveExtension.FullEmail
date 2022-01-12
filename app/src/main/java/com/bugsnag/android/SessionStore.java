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

package com.bugsnag.android;

import com.bugsnag.android.internal.ImmutableConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Comparator;
import java.util.UUID;

/**
 * Store and flush Sessions which couldn't be sent immediately due to
 * lack of network connectivity.
 */
class SessionStore extends FileStore {

    static final Comparator<File> SESSION_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            }
            if (lhs == null) {
                return 1;
            }
            if (rhs == null) {
                return -1;
            }
            String lhsName = lhs.getName();
            String rhsName = rhs.getName();
            return lhsName.compareTo(rhsName);
        }
    };

    SessionStore(@NonNull ImmutableConfig config,
                 @NonNull Logger logger,
                 @Nullable Delegate delegate) {
        super(new File(config.getPersistenceDirectory().getValue(), "bugsnag-sessions"),
                config.getMaxPersistedSessions(),
                SESSION_COMPARATOR,
                logger,
                delegate);
    }

    @NonNull
    @Override
    String getFilename(Object object) {
        return UUID.randomUUID().toString() + System.currentTimeMillis() + "_v2.json";
    }

}
