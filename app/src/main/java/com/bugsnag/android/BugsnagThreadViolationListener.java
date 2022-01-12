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

import android.os.Build;
import android.os.StrictMode.OnThreadViolationListener;
import android.os.strictmode.Violation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Sends an error report to Bugsnag for each StrictMode thread policy violation that occurs in
 * your app.
 * <p></p>
 * You should use this class by instantiating Bugsnag in the normal way and then set the
 * StrictMode policy with
 * {@link android.os.StrictMode.ThreadPolicy.Builder#penaltyListener
 * (Executor, OnThreadViolationListener)}.
 * This functionality is only supported on API 28+.
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class BugsnagThreadViolationListener implements OnThreadViolationListener {

    private final Client client;
    private final OnThreadViolationListener listener;

    public BugsnagThreadViolationListener() {
        this(Bugsnag.getClient(), null);
    }

    public BugsnagThreadViolationListener(@NonNull Client client) {
        this(client, null);
    }

    public BugsnagThreadViolationListener(@NonNull Client client,
                                          @Nullable OnThreadViolationListener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void onThreadViolation(@NonNull Violation violation) {
        if (client != null) {
            client.notify(violation, new StrictModeOnErrorCallback(
                    "StrictMode policy violation detected: ThreadPolicy"
            ));
        }
        if (listener != null) {
            listener.onThreadViolation(violation);
        }
    }
}
