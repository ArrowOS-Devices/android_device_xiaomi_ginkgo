/*
 * Copyright (C) 2021 WaveOS
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

package org.lineageos.settings.utils;

import android.content.Context;
import android.provider.Settings;

public class VibrationUtils {

    private static final String VIBRATION_STRENGTH_PATH = "/sys/class/leds/vibrator/vtg_level";
    private static final String PREF_VIBRATION_STRENGTH = "vibration_strength";

    // predefined values in kernel - drivers/leds/leds-qpnp-vibrator-ldo.c
    private static final int MIN_STRENGTH = 1504; /* QPNP_VIB_LDO_VMIN_UV */
    private static final int MAX_STRENGTH = 3544; /* QPNP_VIB_LDO_VMAX_UV */
    private static final int STRENGTH_RANGE = MAX_STRENGTH - MIN_STRENGTH;

    public static boolean isAvailable() {
        return FileUtils.isFileWritable(VIBRATION_STRENGTH_PATH);
    }

    public static void setVibStrength(int percent) {
        int strength = MIN_STRENGTH + (int) (percent / 100.0f * STRENGTH_RANGE);
        FileUtils.writeLine(VIBRATION_STRENGTH_PATH, Integer.toString(strength));
    }

    public static void setCurrentVibStrength(Context context) {
        int vibStrength = Settings.Secure.getInt(context.getContentResolver(), PREF_VIBRATION_STRENGTH, 85);
        setVibStrength(vibStrength);
    }

    public static int getVibStrength() { /* returns percentage */
        String strength = FileUtils.readOneLine(VIBRATION_STRENGTH_PATH);
        return (int) ((Integer.valueOf(strength) - MIN_STRENGTH) * 100 / (float) STRENGTH_RANGE);
    }
}
