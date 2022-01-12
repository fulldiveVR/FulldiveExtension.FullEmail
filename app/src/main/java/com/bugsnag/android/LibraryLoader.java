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

import java.util.concurrent.atomic.AtomicBoolean;

class LibraryLoader {

    private final AtomicBoolean attemptedLoad = new AtomicBoolean();
    private boolean loaded = false;

    /**
     * Attempts to load a native library, returning false if the load was unsuccessful.
     * <p>
     * If a load was attempted and failed, an error report will be sent using the supplied client
     * and OnErrorCallback.
     *
     * @param name     the library name
     * @param client   the bugsnag client
     * @param callback an OnErrorCallback
     * @return true if the library was loaded, false if not
     */
    boolean loadLibrary(final String name, final Client client, final OnErrorCallback callback) {
        try {
            client.bgTaskService.submitTask(TaskType.IO, new Runnable() {
                @Override
                public void run() {
                    loadLibInternal(name, client, callback);
                }
            }).get();
            return loaded;
        } catch (Throwable exc) {
            return false;
        }
    }

    void loadLibInternal(String name, Client client, OnErrorCallback callback) {
        if (!attemptedLoad.getAndSet(true)) {
            try {
                System.loadLibrary(name);
                loaded = true;
            } catch (UnsatisfiedLinkError error) {
                client.notify(error, callback);
            }
        }
    }

    boolean isLoaded() {
        return loaded;
    }
}
