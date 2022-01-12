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
 * Controls whether we should capture and serialize the state of all threads at the time
 * of an error.
 */
enum class ThreadSendPolicy {

    /**
     * Threads should be captured for all events.
     */
    ALWAYS,

    /**
     * Threads should be captured for unhandled events only.
     */
    UNHANDLED_ONLY,

    /**
     * Threads should never be captured.
     */
    NEVER;

    internal companion object {
        fun fromString(str: String) = values().find { it.name == str } ?: ALWAYS
    }
}
