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
 * Information about this library, including name and version.
 */
class Notifier @JvmOverloads constructor(
    var name: String = "Android Bugsnag Notifier",
    var version: String = "5.12.0",
    var url: String = "https://bugsnag.com"
) : JsonStream.Streamable {

    var dependencies = listOf<Notifier>()

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        writer.beginObject()
        writer.name("name").value(name)
        writer.name("version").value(version)
        writer.name("url").value(url)

        if (dependencies.isNotEmpty()) {
            writer.name("dependencies")
            writer.beginArray()
            dependencies.forEach { writer.value(it) }
            writer.endArray()
        }
        writer.endObject()
    }
}
