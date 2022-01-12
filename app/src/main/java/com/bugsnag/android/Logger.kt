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
 * Logs internal messages from within the bugsnag notifier.
 */
interface Logger {

    /**
     * Logs a message at the error level.
     */
    fun e(msg: String): Unit = Unit

    /**
     * Logs a message at the error level.
     */
    fun e(msg: String, throwable: Throwable): Unit = Unit

    /**
     * Logs a message at the warning level.
     */
    fun w(msg: String): Unit = Unit

    /**
     * Logs a message at the warning level.
     */
    fun w(msg: String, throwable: Throwable): Unit = Unit

    /**
     * Logs a message at the info level.
     */
    fun i(msg: String): Unit = Unit

    /**
     * Logs a message at the info level.
     */
    fun i(msg: String, throwable: Throwable): Unit = Unit

    /**
     * Logs a message at the debug level.
     */
    fun d(msg: String): Unit = Unit

    /**
     * Logs a message at the debug level.
     */
    fun d(msg: String, throwable: Throwable): Unit = Unit
}
