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

import com.bugsnag.android.internal.dag.ConfigModule
import com.bugsnag.android.internal.dag.ContextModule
import com.bugsnag.android.internal.dag.DependencyModule
import com.bugsnag.android.internal.dag.SystemServiceModule

/**
 * A dependency module which constructs the objects that persist events to disk in Bugsnag.
 */
internal class EventStorageModule(
    contextModule: ContextModule,
    configModule: ConfigModule,
    dataCollectionModule: DataCollectionModule,
    bgTaskService: BackgroundTaskService,
    trackerModule: TrackerModule,
    systemServiceModule: SystemServiceModule,
    notifier: Notifier
) : DependencyModule() {

    private val cfg = configModule.config

    private val delegate by future {
        InternalReportDelegate(
            contextModule.ctx,
            cfg.logger,
            cfg,
            systemServiceModule.storageManager,
            dataCollectionModule.appDataCollector,
            dataCollectionModule.deviceDataCollector,
            trackerModule.sessionTracker,
            notifier,
            bgTaskService
        )
    }

    val eventStore by future { EventStore(cfg, cfg.logger, notifier, bgTaskService, delegate) }
}
