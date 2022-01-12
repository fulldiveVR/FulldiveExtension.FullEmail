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
import android.content.res.Configuration

internal class ClientComponentCallbacks(
    private val deviceDataCollector: DeviceDataCollector,
    private val cb: (oldOrientation: String?, newOrientation: String?) -> Unit,
    val memoryCallback: (Boolean, Int?) -> Unit
) : ComponentCallbacks2 {

    override fun onConfigurationChanged(newConfig: Configuration) {
        val oldOrientation = deviceDataCollector.getOrientationAsString()

        if (deviceDataCollector.updateOrientation(newConfig.orientation)) {
            val newOrientation = deviceDataCollector.getOrientationAsString()
            cb(oldOrientation, newOrientation)
        }
    }

    override fun onTrimMemory(level: Int) {
        memoryCallback(level >= ComponentCallbacks2.TRIM_MEMORY_COMPLETE, level)
    }

    override fun onLowMemory() {
        memoryCallback(true, null)
    }
}
