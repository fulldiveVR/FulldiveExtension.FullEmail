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
import java.lang.reflect.Array
import java.util.Date

internal class ObjectJsonStreamer {

    companion object {
        private const val REDACTED_PLACEHOLDER = "[REDACTED]"
        private const val OBJECT_PLACEHOLDER = "[OBJECT]"
    }

    var redactedKeys = setOf("password")

    // Write complex/nested values to a JsonStreamer
    @Throws(IOException::class)
    fun objectToStream(obj: Any?, writer: JsonStream, shouldRedactKeys: Boolean = false) {
        when {
            obj == null -> writer.nullValue()
            obj is String -> writer.value(obj)
            obj is Number -> writer.value(obj)
            obj is Boolean -> writer.value(obj)
            obj is JsonStream.Streamable -> obj.toStream(writer)
            obj is Date -> writer.value(DateUtils.toIso8601(obj))
            obj is Map<*, *> -> mapToStream(writer, obj, shouldRedactKeys)
            obj is Collection<*> -> collectionToStream(writer, obj)
            obj.javaClass.isArray -> arrayToStream(writer, obj)
            else -> writer.value(OBJECT_PLACEHOLDER)
        }
    }

    private fun mapToStream(writer: JsonStream, obj: Map<*, *>, shouldRedactKeys: Boolean) {
        writer.beginObject()
        obj.entries.forEach {
            val keyObj = it.key
            if (keyObj is String) {
                writer.name(keyObj)
                if (shouldRedactKeys && isRedactedKey(keyObj)) {
                    writer.value(REDACTED_PLACEHOLDER)
                } else {
                    objectToStream(it.value, writer, shouldRedactKeys)
                }
            }
        }
        writer.endObject()
    }

    private fun collectionToStream(writer: JsonStream, obj: Collection<*>) {
        writer.beginArray()
        obj.forEach { objectToStream(it, writer) }
        writer.endArray()
    }

    private fun arrayToStream(writer: JsonStream, obj: Any) {
        // Primitive array objects
        writer.beginArray()
        val length = Array.getLength(obj)
        var i = 0
        while (i < length) {
            objectToStream(Array.get(obj, i), writer)
            i += 1
        }
        writer.endArray()
    }

    // Should this key be redacted
    private fun isRedactedKey(key: String) = redactedKeys.any { key.contains(it) }
}
