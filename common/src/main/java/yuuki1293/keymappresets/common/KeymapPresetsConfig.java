package yuuki1293.keymappresets.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import static yuuki1293.keymappresets.common.Common.MOD_ID;

@Config(name = MOD_ID)
public class KeymapPresetsConfig implements ConfigData {
    public String selectedPreset = "";

    public SortType sortType = SortType.created;
    public SortOrder sortOrder = SortOrder.asc;

    public int maxButtonWidth = 150;
    public int minButtonWidth = 50;
    public int shortcutTextColor = 0x80FFFFFF;
}
