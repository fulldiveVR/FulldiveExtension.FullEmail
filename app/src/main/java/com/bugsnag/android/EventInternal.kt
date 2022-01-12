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
import java.io.IOException

internal class EventInternal @JvmOverloads internal constructor(
    val originalError: Throwable? = null,
    config: ImmutableConfig,
    private var severityReason: SeverityReason,
    data: Metadata = Metadata()
) : JsonStream.Streamable, MetadataAware, UserAware {

    val metadata: Metadata = data.copy()
    private val discardClasses: Set<String> = config.discardClasses.toSet()
    private val projectPackages = config.projectPackages

    @JvmField
    internal var session: Session? = null

    var severity: Severity
        get() = severityReason.currentSeverity
        set(value) {
            severityReason.currentSeverity = value
        }

    var apiKey: String = config.apiKey
    lateinit var app: AppWithState
    lateinit var device: DeviceWithState
    var breadcrumbs: MutableList<Breadcrumb> = mutableListOf()
    var unhandled: Boolean
        get() = severityReason.unhandled
        set(value) {
            severityReason.unhandled = value
        }
    val unhandledOverridden: Boolean
        get() = severityReason.unhandledOverridden

    val originalUnhandled: Boolean
        get() = severityReason.originalUnhandled

    var errors: MutableList<Error> = when (originalError) {
        null -> mutableListOf()
        else -> Error.createError(originalError, config.projectPackages, config.logger)
    }

    var threads: MutableList<Thread> = ThreadState(originalError, unhandled, config).threads
    var groupingHash: String? = null
    var context: String? = null

    /**
     * @return user information associated with this Event
     */
    internal var _user = User(null, null, null)

    protected fun shouldDiscardClass(): Boolean {
        return when {
            errors.isEmpty() -> true
            else -> errors.any { discardClasses.contains(it.errorClass) }
        }
    }

    protected fun isAnr(event: Event): Boolean {
        val errors = event.errors
        var errorClass: String? = null
        if (errors.isNotEmpty()) {
            val error = errors[0]
            errorClass = error.errorClass
        }
        return "ANR" == errorClass
    }

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        // Write error basics
        writer.beginObject()
        writer.name("context").value(context)
        writer.name("metaData").value(metadata)

        writer.name("severity").value(severity)
        writer.name("severityReason").value(severityReason)
        writer.name("unhandled").value(severityReason.unhandled)

        // Write exception info
        writer.name("exceptions")
        writer.beginArray()
        errors.forEach { writer.value(it) }
        writer.endArray()

        // Write project packages
        writer.name("projectPackages")
        writer.beginArray()
        projectPackages.forEach { writer.value(it) }
        writer.endArray()

        // Write user info
        writer.name("user").value(_user)

        // Write diagnostics
        writer.name("app").value(app)
        writer.name("device").value(device)
        writer.name("breadcrumbs").value(breadcrumbs)
        writer.name("groupingHash").value(groupingHash)

        writer.name("threads")
        writer.beginArray()
        threads.forEach { writer.value(it) }
        writer.endArray()

        if (session != null) {
            val copy = Session.copySession(session)
            writer.name("session").beginObject()
            writer.name("id").value(copy.id)
            writer.name("startedAt").value(copy.startedAt)
            writer.name("events").beginObject()
            writer.name("handled").value(copy.handledCount.toLong())
            writer.name("unhandled").value(copy.unhandledCount.toLong())
            writer.endObject()
            writer.endObject()
        }

        writer.endObject()
    }

    internal fun getErrorTypesFromStackframes(): Set<ErrorType> {
        val errorTypes = errors.mapNotNull(Error::getType).toSet()
        val frameOverrideTypes = errors
            .map { it.stacktrace }
            .flatMap { it.mapNotNull(Stackframe::type) }
        return errorTypes.plus(frameOverrideTypes)
    }

    protected fun updateSeverityInternal(severity: Severity) {
        severityReason = SeverityReason(
            severityReason.severityReasonType,
            severity,
            severityReason.unhandled,
            severityReason.attributeValue
        )
    }

    protected fun updateSeverityReason(@SeverityReason.SeverityReasonType reason: String) {
        severityReason = SeverityReason(
            reason,
            severityReason.currentSeverity,
            severityReason.unhandled,
            severityReason.attributeValue
        )
    }

    fun getSeverityReasonType(): String = severityReason.severityReasonType

    override fun setUser(id: String?, email: String?, name: String?) {
        _user = User(id, email, name)
    }

    override fun getUser() = _user

    override fun addMetadata(section: String, value: Map<String, Any?>) = metadata.addMetadata(section, value)

    override fun addMetadata(section: String, key: String, value: Any?) =
        metadata.addMetadata(section, key, value)

    override fun clearMetadata(section: String) = metadata.clearMetadata(section)

    override fun clearMetadata(section: String, key: String) = metadata.clearMetadata(section, key)

    override fun getMetadata(section: String) = metadata.getMetadata(section)

    override fun getMetadata(section: String, key: String) = metadata.getMetadata(section, key)
}
