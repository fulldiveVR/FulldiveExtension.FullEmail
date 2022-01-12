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

import android.annotation.SuppressLint
import android.content.Context

/**
 * Reads legacy information left in SharedPreferences and migrates it to the new location.
 */
internal class SharedPrefMigrator(context: Context) {

    private val prefs = context
        .getSharedPreferences("com.bugsnag.android", Context.MODE_PRIVATE)

    fun loadDeviceId() = prefs.getString(INSTALL_ID_KEY, null)

    fun loadUser(deviceId: String?) = User(
        prefs.getString(USER_ID_KEY, deviceId),
        prefs.getString(USER_EMAIL_KEY, null),
        prefs.getString(USER_NAME_KEY, null)
    )

    fun hasPrefs() = prefs.contains(INSTALL_ID_KEY)

    @SuppressLint("ApplySharedPref")
    fun deleteLegacyPrefs() {
        if (hasPrefs()) {
            prefs.edit().clear().commit()
        }
    }

    companion object {
        private const val INSTALL_ID_KEY = "install.iud"
        private const val USER_ID_KEY = "user.id"
        private const val USER_NAME_KEY = "user.name"
        private const val USER_EMAIL_KEY = "user.email"
    }
}
