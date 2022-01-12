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
 * Recognized types of breadcrumbs
 */
enum class BreadcrumbType(private val type: String) {
    /**
     * An error was sent to Bugsnag (internal use only)
     */
    ERROR("error"),
    /**
     * A log message
     */
    LOG("log"),
    /**
     * A manual invocation of `leaveBreadcrumb` (default)
     */
    MANUAL("manual"),
    /**
     * A navigation event, such as a window opening or closing
     */
    NAVIGATION("navigation"),
    /**
     * A background process such as a database query
     */
    PROCESS("process"),
    /**
     * A network request
     */
    REQUEST("request"),
    /**
     * A change in application state, such as launch or memory warning
     */
    STATE("state"),
    /**
     * A user action, such as tapping a button
     */
    USER("user");

    override fun toString() = type
}
