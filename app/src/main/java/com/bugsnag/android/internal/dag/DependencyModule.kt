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

package com.bugsnag.android.internal.dag

import com.bugsnag.android.BackgroundTaskService
import com.bugsnag.android.TaskType

internal abstract class DependencyModule {

    private val properties = mutableListOf<Lazy<*>>()

    /**
     * Creates a new [Lazy] property that is marked as an object that should be resolved off the
     * main thread when [resolveDependencies] is called.
     */
    fun <T> future(initializer: () -> T): Lazy<T> {
        val lazy = lazy {
            initializer()
        }
        properties.add(lazy)
        return lazy
    }

    /**
     * Blocks until all dependencies in the module have been constructed. This provides the option
     * for modules to construct objects in a background thread, then have a user block on another
     * thread until all the objects have been constructed.
     */
    fun resolveDependencies(bgTaskService: BackgroundTaskService, taskType: TaskType) {
        kotlin.runCatching {
            bgTaskService.submitTask(
                taskType,
                Runnable {
                    properties.forEach { it.value }
                }
            ).get()
        }
    }
}
