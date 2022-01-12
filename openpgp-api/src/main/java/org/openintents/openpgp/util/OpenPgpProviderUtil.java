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

package org.openintents.openpgp.util;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;


public class OpenPgpProviderUtil {
    private static final String PACKAGE_NAME_APG = "org.thialfihar.android.apg";
    private static final ArrayList<String> PROVIDER_BLACKLIST = new ArrayList<>();
    static {
        PROVIDER_BLACKLIST.add(PACKAGE_NAME_APG);
    }

    public static List<String> getOpenPgpProviderPackages(Context context) {
        ArrayList<String> result = new ArrayList<>();

        Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentServices(intent, 0);
        if (resInfo == null) {
            return result;
        }

        for (ResolveInfo resolveInfo : resInfo) {
            if (resolveInfo.serviceInfo == null) {
                continue;
            }

            result.add(resolveInfo.serviceInfo.packageName);
        }

        return result;
    }

    public static String getOpenPgpProviderName(PackageManager packageManager, String openPgpProvider) {
        Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
        intent.setPackage(openPgpProvider);
        List<ResolveInfo> resInfo = packageManager.queryIntentServices(intent, 0);
        if (resInfo == null) {
            return null;
        }

        for (ResolveInfo resolveInfo : resInfo) {
            if (resolveInfo.serviceInfo == null) {
                continue;
            }

            return String.valueOf(resolveInfo.serviceInfo.loadLabel(packageManager));
        }

        return null;
    }

    public static boolean isBlacklisted(String packageName) {
        return PROVIDER_BLACKLIST.contains(packageName);
    }
}
