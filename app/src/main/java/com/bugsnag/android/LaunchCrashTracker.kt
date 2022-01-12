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

import com.bugsnag.android.internal.ImmutableConfig
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Tracks whether the app is currently in its launch period. This creates a timer of
 * configuration.launchDurationMillis, after which which the launch period is considered
 * complete. If this value is zero, then the user must manually call markLaunchCompleted().
 */
internal class LaunchCrashTracker @JvmOverloads constructor(
    config: ImmutableConfig,
    private val executor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
) : BaseObservable() {

    private val launching = AtomicBoolean(true)
    private val logger = config.logger

    init {
        val delay = config.launchDurationMillis

        if (delay > 0) {
            executor.executeExistingDelayedTasksAfterShutdownPolicy = false
            try {
                executor.schedule({ markLaunchCompleted() }, delay, TimeUnit.MILLISECONDS)
            } catch (exc: RejectedExecutionException) {
                logger.w("Failed to schedule timer for LaunchCrashTracker", exc)
            }
        }
    }

    fun markLaunchCompleted() {
        executor.shutdown()
        launching.set(false)
        updateState { StateEvent.UpdateIsLaunching(false) }
        logger.d("App launch period marked as complete")
    }

    fun isLaunching() = launching.get()
}
