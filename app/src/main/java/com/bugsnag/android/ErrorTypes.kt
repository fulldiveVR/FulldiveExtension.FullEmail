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

class ErrorTypes(

    /**
     * Sets whether [ANRs](https://developer.android.com/topic/performance/vitals/anr)
     * should be reported to Bugsnag.
     *
     * If you wish to disable ANR detection, you should set this property to false.
     */
    var anrs: Boolean = true,

    /**
     * Determines whether NDK crashes such as signals and exceptions should be reported by bugsnag.
     *
     * This flag is true by default.
     */
    var ndkCrashes: Boolean = true,

    /**
     * Sets whether Bugsnag should automatically capture and report unhandled errors.
     * By default, this value is true.
     */
    var unhandledExceptions: Boolean = true,

    /**
     * Sets whether Bugsnag should automatically capture and report unhandled promise rejections.
     * This only applies to React Native apps.
     * By default, this value is true.
     */
    var unhandledRejections: Boolean = true
) {
    internal constructor(detectErrors: Boolean) : this(detectErrors, detectErrors, detectErrors, detectErrors)

    internal fun copy() = ErrorTypes(anrs, ndkCrashes, unhandledExceptions, unhandledRejections)
}
