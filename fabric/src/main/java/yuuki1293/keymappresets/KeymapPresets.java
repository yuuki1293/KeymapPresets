package yuuki1293.keymappresets;

import yuuki1293.keymappresets.command.KeymapPresetsCommand;
import yuuki1293.keymappresets.common.Common;
import net.fabricmc.api.ClientModInitializer;

public class KeymapPresets implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Common.init();
        KeymapPresetsCommand.register();
    }
}
