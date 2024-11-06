package yuuki1293.keymappresets.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import static yuuki1293.keymappresets.common.Common.MOD_ID;

@Config(name = MOD_ID)
public class KeymapPresetsConfig implements ConfigData {
    public String selectedPreset = "";

    public SortType sortType = SortType.CREATED;
    public SortOrder sortOrder = SortOrder.ASC;
}
