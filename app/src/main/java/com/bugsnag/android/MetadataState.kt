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

import com.bugsnag.android.StateEvent.AddMetadata

internal data class MetadataState(val metadata: Metadata = Metadata()) :
    BaseObservable(),
    MetadataAware {

    override fun addMetadata(section: String, value: Map<String, Any?>) {
        metadata.addMetadata(section, value)
        notifyMetadataAdded(section, value)
    }

    override fun addMetadata(section: String, key: String, value: Any?) {
        metadata.addMetadata(section, key, value)
        notifyMetadataAdded(section, key, value)
    }

    override fun clearMetadata(section: String) {
        metadata.clearMetadata(section)
        notifyClear(section, null)
    }

    override fun clearMetadata(section: String, key: String) {
        metadata.clearMetadata(section, key)
        notifyClear(section, key)
    }

    private fun notifyClear(section: String, key: String?) {
        when (key) {
            null -> updateState { StateEvent.ClearMetadataSection(section) }
            else -> updateState { StateEvent.ClearMetadataValue(section, key) }
        }
    }

    override fun getMetadata(section: String) = metadata.getMetadata(section)
    override fun getMetadata(section: String, key: String) = metadata.getMetadata(section, key)

    /**
     * Fires the initial observable messages for all the metadata which has been added before an
     * Observer was added. This is used initially to populate the NDK with data.
     */
    fun emitObservableEvent() {
        val sections = metadata.store.keys

        for (section in sections) {
            val data = metadata.getMetadata(section)

            data?.entries?.forEach {
                notifyMetadataAdded(section, it.key, it.value)
            }
        }
    }

    private fun notifyMetadataAdded(section: String, key: String, value: Any?) {
        when (value) {
            null -> notifyClear(section, key)
            else -> updateState { AddMetadata(section, key, metadata.getMetadata(section, key)) }
        }
    }

    private fun notifyMetadataAdded(section: String, value: Map<String, Any?>) {
        value.entries.forEach {
            updateState { AddMetadata(section, it.key, metadata.getMetadata(it.key)) }
        }
    }
}
