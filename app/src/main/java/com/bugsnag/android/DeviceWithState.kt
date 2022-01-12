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

import java.util.Date

/**
 * Stateful information set by the notifier about the device on which the event occurred can be
 * found on this class. These values can be accessed and amended if necessary.
 */
class DeviceWithState internal constructor(
    buildInfo: DeviceBuildInfo,
    jailbroken: Boolean?,
    id: String?,
    locale: String?,
    totalMemory: Long?,
    runtimeVersions: MutableMap<String, Any>,

    /**
     * The number of free bytes of storage available on the device
     */
    var freeDisk: Long?,

    /**
     * The number of free bytes of memory available on the device
     */
    var freeMemory: Long?,

    /**
     * The orientation of the device when the event occurred: either portrait or landscape
     */
    var orientation: String?,

    /**
     * The timestamp on the device when the event occurred
     */
    var time: Date?
) : Device(buildInfo, buildInfo.cpuAbis, jailbroken, id, locale, totalMemory, runtimeVersions) {

    override fun serializeFields(writer: JsonStream) {
        super.serializeFields(writer)
        writer.name("freeDisk").value(freeDisk)
        writer.name("freeMemory").value(freeMemory)
        writer.name("orientation").value(orientation)

        if (time != null) {
            writer.name("time").value(time)
        }
    }
}
