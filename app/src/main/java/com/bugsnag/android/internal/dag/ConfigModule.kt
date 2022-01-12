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

package com.bugsnag.android.internal.dag

import com.bugsnag.android.Configuration
import com.bugsnag.android.Connectivity
import com.bugsnag.android.internal.sanitiseConfiguration

/**
 * A dependency module which constructs the configuration object that is used to alter
 * Bugsnag's default behaviour.
 */
internal class ConfigModule(
    contextModule: ContextModule,
    configuration: Configuration,
    connectivity: Connectivity
) : DependencyModule() {

    val config = sanitiseConfiguration(contextModule.ctx, configuration, connectivity)
}
