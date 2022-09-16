/*
 * Copyright (C) 2020 Paranoid Android
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

package org.lineageos.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;

import org.lineageos.settings.R;
import org.lineageos.settings.dirac.DiracUtils;
import org.lineageos.settings.display.KcalSettingsActivity;
import org.lineageos.settings.display.LcdFeaturesPreferenceActivity;
import org.lineageos.settings.doze.DozeSettingsActivity;
import org.lineageos.settings.speaker.ClearSpeakerActivity;
import org.lineageos.settings.utils.VibrationUtils;

public class DeviceSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_DIRAC = "dirac_pref";
    private static final String PREF_HEADSET = "dirac_headset_pref";
    private static final String PREF_PRESET = "dirac_preset_pref";
    private static final String PREF_CLEAR_SPEAKER = "clear_speaker_settings";
    private static final String PREF_KCAL_SETTINGS = "kcal_settings";
    private static final String PREF_LCD_FEATURES = "lcd_features_settings";
    private static final String PREF_DOZE_SETTINGS = "doze_settings";
    private static final String PREF_VIBRATION_STRENGTH = "vibration_strength";

    private SwitchPreference mDiracPref;

    private ListPreference mHeadsetPref;
    private ListPreference mPresetPref;

    private Preference mKcalSettingsPref;
    private Preference mLcdFeaturesPref;
    private Preference mClearSpeakerPref;
    private Preference mDozeSettingsPref;

    private SeekBarPreference mVibStrengthPref;

    private DiracUtils mDiracUtils;

    private Vibrator mVibrator;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.device_settings);

        mDiracUtils = new DiracUtils(getContext());

        boolean enhancerEnabled = mDiracUtils.isDiracEnabled();

        mDiracPref = (SwitchPreference) findPreference(PREF_DIRAC);
        mDiracPref.setOnPreferenceChangeListener(this);
        mDiracPref.setChecked(enhancerEnabled);

        mHeadsetPref = (ListPreference) findPreference(PREF_HEADSET);
        mHeadsetPref.setOnPreferenceChangeListener(this);
        mHeadsetPref.setEnabled(enhancerEnabled);

        mPresetPref = (ListPreference) findPreference(PREF_PRESET);
        mPresetPref.setOnPreferenceChangeListener(this);
        mPresetPref.setEnabled(enhancerEnabled);

        mClearSpeakerPref = (Preference) findPreference(PREF_CLEAR_SPEAKER);
        mClearSpeakerPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), ClearSpeakerActivity.class);
            startActivity(intent);
            return true;
        });

        mKcalSettingsPref = (Preference) findPreference(PREF_KCAL_SETTINGS);
        mKcalSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KcalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mLcdFeaturesPref = (Preference) findPreference(PREF_LCD_FEATURES);
        mLcdFeaturesPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), LcdFeaturesPreferenceActivity.class);
            startActivity(intent);
            return true;
        });

        mDozeSettingsPref = (Preference) findPreference(PREF_DOZE_SETTINGS);
        mDozeSettingsPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), DozeSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mVibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        mVibStrengthPref = (SeekBarPreference) findPreference(PREF_VIBRATION_STRENGTH);

        if (VibrationUtils.isAvailable()) {
            mVibStrengthPref.setOnPreferenceChangeListener(this);
            mVibStrengthPref.setValue(VibrationUtils.getVibStrength());
            mVibStrengthPref.setSummary(Integer.toString(VibrationUtils.getVibStrength()) + "%");
            mVibStrengthPref.setMin(10);
        } else {
            mVibStrengthPref.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case PREF_DIRAC:
                mDiracUtils.setEnabled((boolean) newValue);
                setDiracEnabled((boolean) newValue);
                return true;
            case PREF_HEADSET:
                mDiracUtils.setHeadsetType(Integer.parseInt(newValue.toString()));
                return true;
            case PREF_PRESET:
                mDiracUtils.setLevel(String.valueOf(newValue));
                return true;
            case PREF_VIBRATION_STRENGTH:
                mVibStrengthPref.setSummary(String.valueOf(newValue) + "%");
                VibrationUtils.setVibStrength((int) newValue);
                if (mVibrator.hasVibrator()) {
                    mVibrator.vibrate(VibrationEffect.createOneShot(75, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                return true;
            default:
                return false;
        }
    }

    private void setDiracEnabled(boolean enabled) {
        mDiracPref.setChecked(enabled);
        mHeadsetPref.setEnabled(enabled);
        mPresetPref.setEnabled(enabled);
    }
}
