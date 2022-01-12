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

package com.bugsnag.android

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityBreadcrumbCollector(
    private val cb: (message: String, method: Map<String, Any>) -> Unit
) : Application.ActivityLifecycleCallbacks {

    var prevState: String? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) =
        leaveBreadcrumb(getActivityName(activity), "onCreate()", savedInstanceState != null)

    override fun onActivityStarted(activity: Activity) =
        leaveBreadcrumb(getActivityName(activity), "onStart()")

    override fun onActivityResumed(activity: Activity) =
        leaveBreadcrumb(getActivityName(activity), "onResume()")

    override fun onActivityPaused(activity: Activity) =
        leaveBreadcrumb(getActivityName(activity), "onPause()")

    override fun onActivityStopped(activity: Activity) =
        leaveBreadcrumb(getActivityName(activity), "onStop()")

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
        leaveBreadcrumb(getActivityName(activity), "onSaveInstanceState()")

    override fun onActivityDestroyed(activity: Activity) =
        leaveBreadcrumb(getActivityName(activity), "onDestroy()")

    private fun getActivityName(activity: Activity) = activity.javaClass.simpleName

    private fun leaveBreadcrumb(activityName: String, lifecycleCallback: String, hasBundle: Boolean? = null) {
        val metadata = mutableMapOf<String, Any>()
        if (hasBundle != null) {
            metadata["hasBundle"] = hasBundle
        }
        val previousVal = prevState

        if (previousVal != null) {
            metadata["previous"] = previousVal
        }
        cb("$activityName#$lifecycleCallback", metadata)
        prevState = lifecycleCallback
    }
}
