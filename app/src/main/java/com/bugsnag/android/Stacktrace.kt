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
 * Serialize an exception stacktrace and mark frames as "in-project"
 * where appropriate.
 */
internal class Stacktrace : JsonStream.Streamable {

    companion object {
        private const val STACKTRACE_TRIM_LENGTH = 200

        /**
         * Calculates whether a stackframe is 'in project' or not by checking its class against
         * [Configuration.getProjectPackages].
         *
         * For example if the projectPackages included 'com.example', then
         * the `com.example.Foo` class would be considered in project, but `org.example.Bar` would
         * not.
         */
        fun inProject(className: String, projectPackages: Collection<String>): Boolean? {
            return when {
                projectPackages.any { className.startsWith(it) } -> true
                else -> null
            }
        }
    }

    val trace: List<Stackframe>

    constructor(frames: List<Stackframe>) {
        trace = limitTraceLength(frames)
    }

    constructor(
        stacktrace: Array<StackTraceElement>,
        projectPackages: Collection<String>,
        logger: Logger
    ) {
        val frames = limitTraceLength(stacktrace)
        trace = frames.mapNotNull { serializeStackframe(it, projectPackages, logger) }
    }

    private fun limitTraceLength(frames: Array<StackTraceElement>): Array<StackTraceElement> {
        return when {
            frames.size >= STACKTRACE_TRIM_LENGTH -> frames.sliceArray(0 until STACKTRACE_TRIM_LENGTH)
            else -> frames
        }
    }

    private fun limitTraceLength(frames: List<Stackframe>): List<Stackframe> {
        return when {
            frames.size >= STACKTRACE_TRIM_LENGTH -> frames.subList(0, STACKTRACE_TRIM_LENGTH)
            else -> frames
        }
    }

    private fun serializeStackframe(
        el: StackTraceElement,
        projectPackages: Collection<String>,
        logger: Logger
    ): Stackframe? {
        try {
            val className = el.className
            val methodName = when {
                className.isNotEmpty() -> className + "." + el.methodName
                else -> el.methodName
            }

            return Stackframe(
                methodName,
                el.fileName ?: "Unknown",
                el.lineNumber,
                inProject(className, projectPackages)
            )
        } catch (lineEx: Exception) {
            logger.w("Failed to serialize stacktrace", lineEx)
            return null
        }
    }

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        writer.beginArray()
        trace.forEach { writer.value(it) }
        writer.endArray()
    }
}
