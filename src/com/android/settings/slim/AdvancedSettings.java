/*
 * Copyright (C) 2013 SlimRoms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.slim;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.List;

public class AdvancedSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String PREF_MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
    private static final String PREF_DEVICESETTINGS_APP = "devicesettings_app";

    private static final String BOOT_WITH_ADB_OVER_NETWORK_PREF = "adb_over_network_on_boot";   // from res/values/slim_strings.xml
    private SwitchPreference mBootWithAdbNetworkPref;
    private static final String BOOT_WITH_ADB_OVER_NETWORK_PROP = "persist.sys.boot_adb_network";
    private static final String BOOT_WITH_ADB_OVER_NETWORK_DEFAULT = "0";

    private PreferenceScreen mDeviceSettingsApp;
    private ListPreference mMsob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_advanced_settings);

        mMsob = (ListPreference) findPreference(PREF_MEDIA_SCANNER_ON_BOOT);
        mMsob.setValue(String.valueOf(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0)));
        mMsob.setSummary(mMsob.getEntry());
        mMsob.setOnPreferenceChangeListener(this);

        mDeviceSettingsApp = (PreferenceScreen) findPreference(PREF_DEVICESETTINGS_APP);

        mBootWithAdbNetworkPref = (SwitchPreference) prefSet.findPreference(BOOT_WITH_ADB_OVER_NETWORK_PREF);
        mBootWithAdbNetworkPref.setOnPreferenceChangeListener(this);

        if (!deviceSettingsAppExists()) {
            getPreferenceScreen().removePreference(mDeviceSettingsApp);
        }

        if (getPreferenceManager() != null) {
            String useBootWithAdbNetwork = SystemProperties.get(BOOT_WITH_ADB_OVER_NETWORK_PROP,
	                                                            BOOT_WITH_ADB_OVER_NETWORK_DEFAULT);
            Log.i(TAG, "onCreate useBootWithAdbNetwork="+useBootWithAdbNetwork);
            mBootWithAdbNetworkPref.setChecked("1".equals(useBootWithAdbNetwork));
        }
    }

    private boolean deviceSettingsAppExists() {
        Intent intent = mDeviceSettingsApp.getIntent();
        if (intent != null) {
            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            int listSize = list.size();
            return (listSize > 0) ? true : false;

        }
        return false;

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        if (preference == mMsob) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MEDIA_SCANNER_ON_BOOT,
                    Integer.valueOf(value));

            mMsob.setValue(String.valueOf(value));
            mMsob.setSummary(mMsob.getEntry());
            return true;
        }
        return false;

        if (preference.getKey().equals(BOOT_WITH_ADB_OVER_NETWORK_PREF)) {
            Log.i(TAG, "onPreferenceChange mBootWithAdbNetworkPref checked="+newValue);
			SystemProperties.set(BOOT_WITH_ADB_OVER_NETWORK_PROP, (Boolean)newValue ? "1" : "0");
            return true;
        }

    }

}
