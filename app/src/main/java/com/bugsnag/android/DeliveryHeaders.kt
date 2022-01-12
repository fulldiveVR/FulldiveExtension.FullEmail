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

import java.io.OutputStream
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.Date

private const val HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version"
private const val HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At"
private const val HEADER_BUGSNAG_STACKTRACE_TYPES = "Bugsnag-Stacktrace-Types"
private const val HEADER_CONTENT_TYPE = "Content-Type"
internal const val HEADER_BUGSNAG_INTEGRITY = "Bugsnag-Integrity"
internal const val HEADER_API_KEY = "Bugsnag-Api-Key"
internal const val HEADER_INTERNAL_ERROR = "Bugsnag-Internal-Error"

/**
 * Supplies the headers which must be used in any request sent to the Error Reporting API.
 *
 * @return the HTTP headers
 */
internal fun errorApiHeaders(payload: EventPayload): Map<String, String?> {
    val mutableHeaders = mutableMapOf(
        HEADER_API_PAYLOAD_VERSION to "4.0",
        HEADER_API_KEY to (payload.apiKey ?: ""),
        HEADER_BUGSNAG_SENT_AT to DateUtils.toIso8601(Date()),
        HEADER_CONTENT_TYPE to "application/json"
    )
    val errorTypes = payload.getErrorTypes()
    if (errorTypes.isNotEmpty()) {
        mutableHeaders[HEADER_BUGSNAG_STACKTRACE_TYPES] = serializeErrorTypeHeader(errorTypes)
    }
    return mutableHeaders.toMap()
}

/**
 * Serializes the error types to a comma delimited string
 */
internal fun serializeErrorTypeHeader(errorTypes: Set<ErrorType>): String {
    return when {
        errorTypes.isEmpty() -> ""
        else ->
            errorTypes
                .map(ErrorType::desc)
                .reduce { accumulator, str ->
                    "$accumulator,$str"
                }
    }
}

/**
 * Supplies the headers which must be used in any request sent to the Session Tracking API.
 *
 * @return the HTTP headers
 */
internal fun sessionApiHeaders(apiKey: String): Map<String, String?> = mapOf(
    HEADER_API_PAYLOAD_VERSION to "1.0",
    HEADER_API_KEY to apiKey,
    HEADER_CONTENT_TYPE to "application/json",
    HEADER_BUGSNAG_SENT_AT to DateUtils.toIso8601(Date())
)

internal fun computeSha1Digest(payload: ByteArray): String? {
    runCatching {
        val shaDigest = MessageDigest.getInstance("SHA-1")
        val builder = StringBuilder("sha1 ")

        // Pipe the object through a no-op output stream
        DigestOutputStream(NullOutputStream(), shaDigest).use { stream ->
            stream.buffered().use { writer ->
                writer.write(payload)
            }
            shaDigest.digest().forEach { byte ->
                builder.append(String.format("%02x", byte))
            }
        }
        return builder.toString()
    }.getOrElse { return null }
}

internal class NullOutputStream : OutputStream() {
    override fun write(b: Int) = Unit
}
