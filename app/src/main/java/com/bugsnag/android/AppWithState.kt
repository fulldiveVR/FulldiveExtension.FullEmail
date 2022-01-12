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

/**
 * Stateful information set by the notifier about your app can be found on this class. These values
 * can be accessed and amended if necessary.
 */
class AppWithState(
    binaryArch: String?,
    id: String?,
    releaseStage: String?,
    version: String?,
    codeBundleId: String?,
    buildUuid: String?,
    type: String?,
    versionCode: Number?,

    /**
     * The number of milliseconds the application was running before the event occurred
     */
    var duration: Number?,

    /**
     * The number of milliseconds the application was running in the foreground before the
     * event occurred
     */
    var durationInForeground: Number?,

    /**
     * Whether the application was in the foreground when the event occurred
     */
    var inForeground: Boolean?,

    /**
     * Whether the application was launching when the event occurred
     */
    var isLaunching: Boolean?
) : App(binaryArch, id, releaseStage, version, codeBundleId, buildUuid, type, versionCode) {

    internal constructor(
        config: ImmutableConfig,
        binaryArch: String?,
        id: String?,
        releaseStage: String?,
        version: String?,
        codeBundleId: String?,
        duration: Number?,
        durationInForeground: Number?,
        inForeground: Boolean?,
        isLaunching: Boolean?
    ) : this(
        binaryArch,
        id,
        releaseStage,
        version,
        codeBundleId,
        config.buildUuid,
        config.appType,
        config.versionCode,
        duration,
        durationInForeground,
        inForeground,
        isLaunching
    )

    override fun serialiseFields(writer: JsonStream) {
        super.serialiseFields(writer)
        writer.name("duration").value(duration)
        writer.name("durationInForeground").value(durationInForeground)
        writer.name("inForeground").value(inForeground)
        writer.name("isLaunching").value(isLaunching)
    }
}
