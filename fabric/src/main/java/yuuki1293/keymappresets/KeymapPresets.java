package yuuki1293.keymappresets;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import yuuki1293.keymappresets.common.Common;
import net.fabricmc.api.ClientModInitializer;
import yuuki1293.keymappresets.common.command.CommonCommand;

public class KeymapPresets implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Common.init();
        CommonCommand.register(ClientCommandManager.DISPATCHER, FabricClientCommandSource.class);
    }
}
