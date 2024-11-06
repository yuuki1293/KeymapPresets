package com.github.yuuki1293;

import com.github.yuuki1293.command.KeymapPresetsCommand;
import com.github.yuuki1293.screen.KeymapPresetsMenuScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import static com.github.yuuki1293.Common.*;

public class KeymapPresets implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeymapPresetsCommand.register();
        screenPresetsMenu = new KeymapPresetsMenuScreen();

        KeyBindingHelper.registerKeyBinding(keyBindingMenu);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBindingMenu.wasPressed()) { // initialize
                client.mouse.unlockCursor();
                pressed = true;
            }
            if (wasPressed && !keyBindingMenu.isPressed()) { // finalize
                client.mouse.lockCursor();
            }

            wasPressed = keyBindingMenu.isPressed();
        });

        AutoConfig.register(KeymapPresetsConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KeymapPresetsConfig.class);
    }
}
