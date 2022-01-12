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

import android.content.ComponentCallbacks2

internal class MemoryTrimState : BaseObservable() {
    var isLowMemory: Boolean = false
    var memoryTrimLevel: Int? = null

    val trimLevelDescription: String get() = descriptionFor(memoryTrimLevel)

    fun updateMemoryTrimLevel(newTrimLevel: Int?): Boolean {
        if (memoryTrimLevel == newTrimLevel) {
            return false
        }

        memoryTrimLevel = newTrimLevel
        return true
    }

    fun emitObservableEvent() {
        updateState {
            StateEvent.UpdateMemoryTrimEvent(
                isLowMemory,
                memoryTrimLevel,
                trimLevelDescription
            )
        }
    }

    private fun descriptionFor(memoryTrimLevel: Int?) = when (memoryTrimLevel) {
        null -> "None"
        ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "Complete"
        ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "Moderate"
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "Background"
        ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI hidden"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "Running critical"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "Running low"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "Running moderate"
        else -> "Unknown ($memoryTrimLevel)"
    }
}
