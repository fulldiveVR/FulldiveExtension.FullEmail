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

sealed class StateEvent { // JvmField allows direct field access optimizations

    class Install(
        @JvmField val apiKey: String,
        @JvmField val autoDetectNdkCrashes: Boolean,
        @JvmField val appVersion: String?,
        @JvmField val buildUuid: String?,
        @JvmField val releaseStage: String?,
        @JvmField val lastRunInfoPath: String,
        @JvmField val consecutiveLaunchCrashes: Int
    ) : StateEvent()

    object DeliverPending : StateEvent()

    class AddMetadata(
        @JvmField val section: String,
        @JvmField val key: String?,
        @JvmField val value: Any?
    ) : StateEvent()

    class ClearMetadataSection(@JvmField val section: String) : StateEvent()

    class ClearMetadataValue(
        @JvmField val section: String,
        @JvmField val key: String?
    ) : StateEvent()

    class AddBreadcrumb(
        @JvmField val message: String,
        @JvmField val type: BreadcrumbType,
        @JvmField val timestamp: String,
        @JvmField val metadata: MutableMap<String, Any?>
    ) : StateEvent()

    object NotifyHandled : StateEvent()

    object NotifyUnhandled : StateEvent()

    object PauseSession : StateEvent()

    class StartSession(
        @JvmField val id: String,
        @JvmField val startedAt: String,
        @JvmField val handledCount: Int,
        val unhandledCount: Int
    ) : StateEvent()

    class UpdateContext(@JvmField val context: String?) : StateEvent()

    class UpdateInForeground(
        @JvmField val inForeground: Boolean,
        val contextActivity: String?
    ) : StateEvent()

    class UpdateLastRunInfo(@JvmField val consecutiveLaunchCrashes: Int) : StateEvent()

    class UpdateIsLaunching(@JvmField val isLaunching: Boolean) : StateEvent()

    class UpdateOrientation(@JvmField val orientation: String?) : StateEvent()

    class UpdateUser(@JvmField val user: User) : StateEvent()

    class UpdateMemoryTrimEvent(
        @JvmField val isLowMemory: Boolean,
        @JvmField val memoryTrimLevel: Int? = null,
        @JvmField val memoryTrimLevelDescription: String = "None"
    ) : StateEvent()
}
