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

/**
 * Stateless information set by the notifier about your app can be found on this class. These values
 * can be accessed and amended if necessary.
 */
open class App internal constructor(
    /**
     * The architecture of the running application binary
     */
    var binaryArch: String?,

    /**
     * The package name of the application
     */
    var id: String?,

    /**
     * The release stage set in [Configuration.releaseStage]
     */
    var releaseStage: String?,

    /**
     * The version of the application set in [Configuration.version]
     */
    var version: String?,

    /**
     The revision ID from the manifest (React Native apps only)
     */
    var codeBundleId: String?,

    /**
     * The unique identifier for the build of the application set in [Configuration.buildUuid]
     */
    var buildUuid: String?,

    /**
     * The application type set in [Configuration#version]
     */
    var type: String?,

    /**
     * The version code of the application set in [Configuration.versionCode]
     */
    var versionCode: Number?
) : JsonStream.Streamable {

    internal constructor(
        config: ImmutableConfig,
        binaryArch: String?,
        id: String?,
        releaseStage: String?,
        version: String?,
        codeBundleId: String?
    ) : this(
        binaryArch,
        id,
        releaseStage,
        version,
        codeBundleId,
        config.buildUuid,
        config.appType,
        config.versionCode
    )

    internal open fun serialiseFields(writer: JsonStream) {
        writer.name("binaryArch").value(binaryArch)
        writer.name("buildUUID").value(buildUuid)
        writer.name("codeBundleId").value(codeBundleId)
        writer.name("id").value(id)
        writer.name("releaseStage").value(releaseStage)
        writer.name("type").value(type)
        writer.name("version").value(version)
        writer.name("versionCode").value(versionCode)
    }

    @Throws(IOException::class)
    override fun toStream(writer: JsonStream) {
        writer.beginObject()
        serialiseFields(writer)
        writer.endObject()
    }
}
