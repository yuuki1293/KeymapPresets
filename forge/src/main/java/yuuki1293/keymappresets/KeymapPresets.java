package yuuki1293.keymappresets;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import yuuki1293.keymappresets.common.Common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yuuki1293.keymappresets.common.command.CommonCommand;

import static yuuki1293.keymappresets.common.Common.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeymapPresets {
    public KeymapPresets() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onInitializeClient);
    }

    @SubscribeEvent
    public void onInitializeClient(final FMLClientSetupEvent event) {
        Common.init();
    }

    @SubscribeEvent
    public static void onCommandRegistration(final @NotNull RegisterClientCommandsEvent event) {
        CommonCommand.register(event.getDispatcher());
    }
}
