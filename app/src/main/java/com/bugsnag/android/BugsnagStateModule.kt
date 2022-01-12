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
import com.bugsnag.android.internal.dag.DependencyModule

/**
 * A dependency module which constructs the objects that track state in Bugsnag. For example, this
 * class is responsible for creating classes which track the current breadcrumb/metadata state.
 */
internal class BugsnagStateModule(
    configModule: ConfigModule,
    configuration: Configuration
) : DependencyModule() {

    private val cfg = configModule.config

    val clientObservable = ClientObservable()

    val callbackState = configuration.impl.callbackState.copy()

    val contextState = ContextState().apply {
        if (configuration.context != null) {
            setManualContext(configuration.context)
        }
    }

    val breadcrumbState = BreadcrumbState(cfg.maxBreadcrumbs, callbackState, cfg.logger)

    val metadataState = copyMetadataState(configuration)

    private fun copyMetadataState(configuration: Configuration): MetadataState {
        // performs deep copy of metadata to preserve immutability of Configuration interface
        val orig = configuration.impl.metadataState.metadata
        return configuration.impl.metadataState.copy(metadata = orig.copy())
    }
}
