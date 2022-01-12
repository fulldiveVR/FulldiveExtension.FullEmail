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

import com.bugsnag.android.internal.StateObserver
import java.util.concurrent.CopyOnWriteArrayList

internal open class BaseObservable {

    internal val observers = CopyOnWriteArrayList<StateObserver>()

    /**
     * Adds an observer that can react to [StateEvent] messages.
     */
    fun addObserver(observer: StateObserver) {
        observers.addIfAbsent(observer)
    }

    /**
     * Removes a previously added observer that reacts to [StateEvent] messages.
     */
    fun removeObserver(observer: StateObserver) {
        observers.remove(observer)
    }

    /**
     * This method should be invoked when the notifier's state has changed. If an observer
     * has been set, it will be notified of the [StateEvent] message so that it can react
     * appropriately. If no observer has been set then this method will no-op.
     */
    internal inline fun updateState(provider: () -> StateEvent) {
        // optimization to avoid unnecessary iterator and StateEvent construction
        if (observers.isEmpty()) {
            return
        }

        // construct the StateEvent object and notify observers
        val event = provider()
        observers.forEach { it.onStateChange(event) }
    }

    /**
     * An eager version of [updateState], which is intended primarily for use in Java code.
     * If the event will occur very frequently, you should consider calling the lazy method
     * instead.
     */
    fun updateState(event: StateEvent) = updateState { event }
}
