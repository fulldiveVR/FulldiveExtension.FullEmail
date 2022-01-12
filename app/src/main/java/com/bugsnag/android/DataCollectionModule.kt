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

import android.os.Environment
import com.bugsnag.android.internal.dag.ConfigModule
import com.bugsnag.android.internal.dag.ContextModule
import com.bugsnag.android.internal.dag.DependencyModule
import com.bugsnag.android.internal.dag.SystemServiceModule

/**
 * A dependency module which constructs the objects that collect data in Bugsnag. For example, this
 * class is responsible for creating classes which capture device-specific information.
 */
internal class DataCollectionModule(
    contextModule: ContextModule,
    configModule: ConfigModule,
    systemServiceModule: SystemServiceModule,
    trackerModule: TrackerModule,
    bgTaskService: BackgroundTaskService,
    connectivity: Connectivity,
    deviceId: String?,
    memoryTrimState: MemoryTrimState
) : DependencyModule() {

    private val ctx = contextModule.ctx
    private val cfg = configModule.config
    private val logger = cfg.logger
    private val deviceBuildInfo: DeviceBuildInfo = DeviceBuildInfo.defaultInfo()
    private val dataDir = Environment.getDataDirectory()

    val appDataCollector by future {
        AppDataCollector(
            ctx,
            ctx.packageManager,
            cfg,
            trackerModule.sessionTracker,
            systemServiceModule.activityManager,
            trackerModule.launchCrashTracker,
            memoryTrimState
        )
    }

    private val rootDetector by future {
        RootDetector(logger = logger, deviceBuildInfo = deviceBuildInfo)
    }

    val deviceDataCollector by future {
        DeviceDataCollector(
            connectivity,
            ctx,
            ctx.resources,
            deviceId,
            deviceBuildInfo,
            dataDir,
            rootDetector,
            bgTaskService,
            logger
        )
    }
}
