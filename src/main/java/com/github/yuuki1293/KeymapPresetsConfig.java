package com.github.yuuki1293;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = KeymapPresets.MOD_ID)
public class KeymapPresetsConfig implements ConfigData {
    public String selectedPreset = "";
}
