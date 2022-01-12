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

import android.os.Build

internal class DeviceBuildInfo(
    val manufacturer: String?,
    val model: String?,
    val osVersion: String?,
    val apiLevel: Int?,
    val osBuild: String?,
    val fingerprint: String?,
    val tags: String?,
    val brand: String?,
    val cpuAbis: Array<String>?
) {
    companion object {
        fun defaultInfo(): DeviceBuildInfo {
            @Suppress("DEPRECATION") val cpuABis = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> Build.SUPPORTED_ABIS
                else -> arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            }

            return DeviceBuildInfo(
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.DISPLAY,
                Build.FINGERPRINT,
                Build.TAGS,
                Build.BRAND,
                cpuABis
            )
        }
    }
}
