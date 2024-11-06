package com.github.yuuki1293;

import com.github.yuuki1293.command.KeymapPresetsCommand;
import com.github.yuuki1293.screen.KeymapPresetsMenuScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.github.yuuki1293.Common.MOD_ID;
import static com.github.yuuki1293.Common.CONFIG;
import static com.github.yuuki1293.Common.CLIENT;

@Mod(MOD_ID)
public class KeymapPresets {
    public KeymapPresets() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeymapPresetsCommand.class);
        ClientRegistry.registerKeyBinding(Common.keyBindingMenu);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onInitializeClient);

        AutoConfig.register(KeymapPresetsConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KeymapPresetsConfig.class);
    }

    public void onInitializeClient(final FMLClientSetupEvent event) {
        Common.screenPresetsMenu = new KeymapPresetsMenuScreen();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) {
            if (Common.keyBindingMenu.wasPressed()) { // initialize
                CLIENT.mouse.unlockCursor();
                Common.pressed = true;
            }
            if (Common.wasPressed && !Common.keyBindingMenu.isPressed()) { // finalize
                CLIENT.mouse.lockCursor();
            }

            Common.wasPressed = Common.keyBindingMenu.isPressed();
        }
    }
}
