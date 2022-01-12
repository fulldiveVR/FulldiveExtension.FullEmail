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

internal class ErrorInternal @JvmOverloads internal constructor(
    var errorClass: String,
    var errorMessage: String?,
    stacktrace: Stacktrace,
    var type: ErrorType = ErrorType.ANDROID
) : JsonStream.Streamable {

    val stacktrace: List<Stackframe> = stacktrace.trace

    internal companion object {
        fun createError(exc: Throwable, projectPackages: Collection<String>, logger: Logger): MutableList<Error> {
            return exc.safeUnrollCauses()
                .mapTo(mutableListOf()) { currentEx ->
                    // Somehow it's possible for stackTrace to be null in rare cases
                    val stacktrace = currentEx.stackTrace ?: arrayOf<StackTraceElement>()
                    val trace = Stacktrace(stacktrace, projectPackages, logger)
                    val errorInternal =
                        ErrorInternal(currentEx.javaClass.name, currentEx.localizedMessage, trace)

                    return@mapTo Error(errorInternal, logger)
                }
        }
    }

    override fun toStream(writer: JsonStream) {
        writer.beginObject()
        writer.name("errorClass").value(errorClass)
        writer.name("message").value(errorMessage)
        writer.name("type").value(type.desc)
        writer.name("stacktrace").value(stacktrace)
        writer.endObject()
    }
}
