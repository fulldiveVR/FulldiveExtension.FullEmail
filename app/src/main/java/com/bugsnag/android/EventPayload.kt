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

import com.bugsnag.android.internal.ImmutableConfig
import java.io.File
import java.io.IOException

/**
 * An error report payload.
 *
 * This payload contains an error report and identifies the source application
 * using your API key.
 */
class EventPayload @JvmOverloads internal constructor(
    var apiKey: String?,
    val event: Event? = null,
    internal val eventFile: File? = null,
    notifier: Notifier,
    private val config: ImmutableConfig
) : JsonStream.Streamable {

    internal val notifier = Notifier(notifier.name, notifier.version, notifier.url).apply {
        dependencies = notifier.dependencies.toMutableList()
    }

    internal fun getErrorTypes(): Set<ErrorType> {
        return when {
            event != null -> event.impl.getErrorTypesFromStackframes()
            eventFile != null -> EventFilenameInfo.fromFile(eventFile, config).errorTypes
            else -> emptySet()
        }
    }

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        writer.beginObject()
        writer.name("apiKey").value(apiKey)
        writer.name("payloadVersion").value("4.0")
        writer.name("notifier").value(notifier)
        writer.name("events").beginArray()

        when {
            event != null -> writer.value(event)
            eventFile != null -> writer.value(eventFile)
            else -> Unit
        }

        writer.endArray()
        writer.endObject()
    }
}
