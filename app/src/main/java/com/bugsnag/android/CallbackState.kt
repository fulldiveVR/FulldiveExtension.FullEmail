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

import java.util.concurrent.ConcurrentLinkedQueue

internal data class CallbackState(
    val onErrorTasks: MutableCollection<OnErrorCallback> = ConcurrentLinkedQueue<OnErrorCallback>(),
    val onBreadcrumbTasks: MutableCollection<OnBreadcrumbCallback> = ConcurrentLinkedQueue<OnBreadcrumbCallback>(),
    val onSessionTasks: MutableCollection<OnSessionCallback> = ConcurrentLinkedQueue()
) : CallbackAware {

    override fun addOnError(onError: OnErrorCallback) {
        onErrorTasks.add(onError)
    }

    override fun removeOnError(onError: OnErrorCallback) {
        onErrorTasks.remove(onError)
    }

    override fun addOnBreadcrumb(onBreadcrumb: OnBreadcrumbCallback) {
        onBreadcrumbTasks.add(onBreadcrumb)
    }

    override fun removeOnBreadcrumb(onBreadcrumb: OnBreadcrumbCallback) {
        onBreadcrumbTasks.remove(onBreadcrumb)
    }

    override fun addOnSession(onSession: OnSessionCallback) {
        onSessionTasks.add(onSession)
    }

    override fun removeOnSession(onSession: OnSessionCallback) {
        onSessionTasks.remove(onSession)
    }

    fun runOnErrorTasks(event: Event, logger: Logger): Boolean {
        // optimization to avoid construction of iterator when no callbacks set
        if (onErrorTasks.isEmpty()) {
            return true
        }
        onErrorTasks.forEach {
            try {
                if (!it.onError(event)) {
                    return false
                }
            } catch (ex: Throwable) {
                logger.w("OnBreadcrumbCallback threw an Exception", ex)
            }
        }
        return true
    }

    fun runOnBreadcrumbTasks(breadcrumb: Breadcrumb, logger: Logger): Boolean {
        // optimization to avoid construction of iterator when no callbacks set
        if (onBreadcrumbTasks.isEmpty()) {
            return true
        }
        onBreadcrumbTasks.forEach {
            try {
                if (!it.onBreadcrumb(breadcrumb)) {
                    return false
                }
            } catch (ex: Throwable) {
                logger.w("OnBreadcrumbCallback threw an Exception", ex)
            }
        }
        return true
    }

    fun runOnSessionTasks(session: Session, logger: Logger): Boolean {
        // optimization to avoid construction of iterator when no callbacks set
        if (onSessionTasks.isEmpty()) {
            return true
        }
        onSessionTasks.forEach {
            try {
                if (!it.onSession(session)) {
                    return false
                }
            } catch (ex: Throwable) {
                logger.w("OnSessionCallback threw an Exception", ex)
            }
        }
        return true
    }

    fun copy() = this.copy(
        onErrorTasks = onErrorTasks,
        onBreadcrumbTasks = onBreadcrumbTasks,
        onSessionTasks = onSessionTasks
    )
}
