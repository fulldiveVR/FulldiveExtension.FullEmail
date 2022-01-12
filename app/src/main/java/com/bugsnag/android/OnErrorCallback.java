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
 * A callback to be run before error reports are sent to Bugsnag.
 * <p>
 * <p>You can use this to add or modify information attached to an error
 * before it is sent to your dashboard. You can also return
 * <code>false</code> from any callback to halt execution.
 * <p>"on error" callbacks added via the JVM API do not run when a fatal C/C++ crash occurs.
 */
public interface OnErrorCallback {

    /**
     * Runs the "on error" callback. If the callback returns
     * <code>false</code> any further OnErrorCallback callbacks will not be called
     * and the event will not be sent to Bugsnag.
     *
     * @param event the event to be sent to Bugsnag
     * @see Event
     */
    boolean onError(@NonNull Event event);
}
