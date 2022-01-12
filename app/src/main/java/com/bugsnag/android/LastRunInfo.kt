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

/**
 * Provides information about the last launch of the application, if there was one.
 */
class LastRunInfo(

    /**
     * The number times the app has consecutively crashed during its launch period.
     */
    val consecutiveLaunchCrashes: Int,

    /**
     * Whether the last app run ended with a crash, or was abnormally terminated by the system.
     */
    val crashed: Boolean,

    /**
     * True if the previous app run ended with a crash during its launch period.
     */
    val crashedDuringLaunch: Boolean
) {
    override fun toString(): String {
        return "LastRunInfo(consecutiveLaunchCrashes=$consecutiveLaunchCrashes, crashed=$crashed, crashedDuringLaunch=$crashedDuringLaunch)"
    }
}
