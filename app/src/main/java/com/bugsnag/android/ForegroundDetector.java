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

import static com.bugsnag.android.ContextExtensionsKt.getActivityManagerFrom;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;

import androidx.annotation.Nullable;

import java.util.List;

class ForegroundDetector {

    @Nullable
    private final ActivityManager activityManager;

    ForegroundDetector(Context context) {
        this.activityManager = getActivityManagerFrom(context);
    }

    /**
     * Determines whether or not the application is in the foreground, by using the process'
     * importance as a proxy.
     * <p/>
     * In the unlikely event that information about the process cannot be retrieved, this method
     * will return null, and the 'inForeground' and 'durationInForeground' values will not be
     * serialized in API calls.
     *
     * @return whether the application is in the foreground or not
     */
    @Nullable
    Boolean isInForeground() {
        try {
            ActivityManager.RunningAppProcessInfo info = getProcessInfo();

            if (info != null) {
                return info.importance
                        <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            } else {
                return null;
            }
        } catch (RuntimeException exc) {
            return null;
        }
    }

    private ActivityManager.RunningAppProcessInfo getProcessInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.RunningAppProcessInfo info =
                new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(info);
            return info;
        } else {
            return getProcessInfoPreApi16();
        }
    }

    @Nullable
    private ActivityManager.RunningAppProcessInfo getProcessInfoPreApi16() {
        if (activityManager == null) {
            return null;
        }

        List<ActivityManager.RunningAppProcessInfo> appProcesses
                = activityManager.getRunningAppProcesses();

        if (appProcesses != null) {
            int pid = Process.myPid();

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (pid == appProcess.pid) {
                    return appProcess;
                }
            }
        }
        return null;
    }
}
