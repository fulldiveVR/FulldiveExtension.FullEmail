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

import android.content.Context
import com.bugsnag.android.internal.ImmutableConfig
import com.bugsnag.android.internal.dag.DependencyModule

/**
 * A dependency module which constructs the objects that store information to disk in Bugsnag.
 */
internal class StorageModule(
    appContext: Context,
    immutableConfig: ImmutableConfig,
    logger: Logger
) : DependencyModule() {

    val sharedPrefMigrator by future { SharedPrefMigrator(appContext) }

    private val deviceIdStore by future {
        DeviceIdStore(
            appContext,
            sharedPrefMigrator = sharedPrefMigrator,
            logger = logger
        )
    }

    val deviceId by future { deviceIdStore.loadDeviceId() }

    val userStore by future {
        UserStore(
            immutableConfig,
            deviceId,
            sharedPrefMigrator = sharedPrefMigrator,
            logger = logger
        )
    }

    val lastRunInfoStore by future { LastRunInfoStore(immutableConfig) }

    val sessionStore by future { SessionStore(immutableConfig, logger, null) }

    val lastRunInfo by future {
        val info = lastRunInfoStore.load()
        val currentRunInfo = LastRunInfo(0, crashed = false, crashedDuringLaunch = false)
        lastRunInfoStore.persist(currentRunInfo)
        info
    }
}
