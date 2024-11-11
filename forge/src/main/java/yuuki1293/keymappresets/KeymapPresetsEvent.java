package yuuki1293.keymappresets;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import yuuki1293.keymappresets.common.command.CommonCommand;

import static yuuki1293.keymappresets.common.Common.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeymapPresetsEvent {
    @SubscribeEvent
    public static void onCommandRegistration(final @NotNull RegisterClientCommandsEvent event) {
        CommonCommand.register(event.getDispatcher());
    }
}
