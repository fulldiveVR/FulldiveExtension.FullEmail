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

import java.io.IOException

/**
 * The severity of an Event, one of "error", "warning" or "info".
 *
 * By default, unhandled exceptions will be Severity.ERROR and handled
 * exceptions sent with bugsnag.notify will be Severity.WARNING.
 */
enum class Severity(private val str: String) : JsonStream.Streamable {
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        writer.value(str)
    }
}
