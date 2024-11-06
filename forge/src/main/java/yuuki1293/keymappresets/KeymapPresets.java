package yuuki1293.keymappresets;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import yuuki1293.keymappresets.command.KeymapPresetsCommand;
import yuuki1293.keymappresets.common.Common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static yuuki1293.keymappresets.common.Common.MOD_ID;

@Mod(MOD_ID)
public class KeymapPresets {
    public KeymapPresets() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeymapPresetsCommand.class);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onInitializeClient);
    }

    @SubscribeEvent
    public void onInitializeClient(final FMLClientSetupEvent event) {
        Common.init();
    }
}
