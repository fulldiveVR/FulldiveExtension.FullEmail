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

import androidx.annotation.NonNull;

/**
 * A callback to be run before sessions are sent to Bugsnag.
 * <p>
 * <p>You can use this to add or modify information attached to a session
 * before it is sent to your dashboard. You can also return
 * <code>false</code> from any callback to halt execution.
 */
public interface OnSessionCallback {

    /**
     * Runs the "on session" callback. If the callback returns
     * <code>false</code> any further OnSessionCallback callbacks will not be called
     * and the session will not be sent to Bugsnag.
     *
     * @param session the session to be sent to Bugsnag
     * @see Session
     */
    boolean onSession(@NonNull Session session);
}
