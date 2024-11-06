package com.github.yuuki1293;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import static com.github.yuuki1293.Common.MOD_ID;

@Config(name = MOD_ID)
public class KeymapPresetsConfig implements ConfigData {
    public String selectedPreset = "";
}
