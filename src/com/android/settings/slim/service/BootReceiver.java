/*
 * Copyright (C) 2013 Android Open Kang Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.slim.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentResolver resolver = context.getContentResolver();

        if (Settings.System.getInt(resolver,
                Settings.System.QUIET_HOURS_REQUIRE_CHARGING, 0) != 0) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            final int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            Settings.System.putInt(resolver,
                    Settings.System.QUIET_HOURS_REQUIRE_CHARGING, isCharging ? 2 : 1);
        }

        QuietHoursController.getInstance(context).scheduleService();
    }

    private void usbRomSettings(Context ctx) {
		final String BOOT_WITH_ADB_OVER_NETWORK_PROP = "persist.sys.boot_adb_network";
		final String BOOT_WITH_ADB_OVER_NETWORK_DEFAULT = "0";
        String useBootWithAdbNetwork = SystemProperties.get(BOOT_WITH_ADB_OVER_NETWORK_PROP,
 	                                                        BOOT_WITH_ADB_OVER_NETWORK_DEFAULT);
		Log.i(TAG, "usbRomSettings useBootWithAdbNetwork="+useBootWithAdbNetwork);
		if("1".equals(useBootWithAdbNetwork)) {
		    Settings.Secure.putInt(ctx.getContentResolver(),
		            Settings.Secure.ADB_PORT, 5555);
		}
   }
}
