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

internal class PluginClient(
    userPlugins: Set<Plugin>,
    private val immutableConfig: ImmutableConfig,
    private val logger: Logger
) {

    companion object {
        private const val NDK_PLUGIN = "com.bugsnag.android.NdkPlugin"
        private const val ANR_PLUGIN = "com.bugsnag.android.AnrPlugin"
        private const val RN_PLUGIN = "com.bugsnag.android.BugsnagReactNativePlugin"
    }

    private val plugins: Set<Plugin>

    private val ndkPlugin = instantiatePlugin(NDK_PLUGIN)
    private val anrPlugin = instantiatePlugin(ANR_PLUGIN)
    private val rnPlugin = instantiatePlugin(RN_PLUGIN)

    init {
        val set = mutableSetOf<Plugin>()
        set.addAll(userPlugins)

        // instantiate ANR + NDK plugins by reflection as bugsnag-android-core has no
        // direct dependency on the artefacts
        ndkPlugin?.let(set::add)
        anrPlugin?.let(set::add)
        rnPlugin?.let(set::add)
        plugins = set.toSet()
    }

    private fun instantiatePlugin(clz: String): Plugin? {
        return try {
            val pluginClz = Class.forName(clz)
            pluginClz.newInstance() as Plugin
        } catch (exc: ClassNotFoundException) {
            logger.d("Plugin '$clz' is not on the classpath - functionality will not be enabled.")
            null
        } catch (exc: Throwable) {
            logger.e("Failed to load plugin '$clz'", exc)
            null
        }
    }

    fun loadPlugins(client: Client) {
        plugins.forEach { plugin ->
            try {
                loadPluginInternal(plugin, client)
            } catch (exc: Throwable) {
                logger.e("Failed to load plugin $plugin, continuing with initialisation.", exc)
            }
        }
    }

    fun setAutoNotify(client: Client, autoNotify: Boolean) {
        setAutoDetectAnrs(client, autoNotify)

        if (autoNotify) {
            ndkPlugin?.load(client)
        } else {
            ndkPlugin?.unload()
        }
    }

    fun setAutoDetectAnrs(client: Client, autoDetectAnrs: Boolean) {
        if (autoDetectAnrs) {
            anrPlugin?.load(client)
        } else {
            anrPlugin?.unload()
        }
    }

    fun findPlugin(clz: Class<*>): Plugin? = plugins.find { it.javaClass == clz }

    private fun loadPluginInternal(plugin: Plugin, client: Client) {
        val name = plugin.javaClass.name
        val errorTypes = immutableConfig.enabledErrorTypes

        // only initialize NDK/ANR plugins if automatic detection enabled
        if (name == NDK_PLUGIN) {
            if (errorTypes.ndkCrashes) {
                plugin.load(client)
            }
        } else if (name == ANR_PLUGIN) {
            if (errorTypes.anrs) {
                plugin.load(client)
            }
        } else {
            plugin.load(client)
        }
    }
}
