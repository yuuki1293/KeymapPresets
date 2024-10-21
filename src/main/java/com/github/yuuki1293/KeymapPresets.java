package com.github.yuuki1293;

import com.github.yuuki1293.command.KeymapPresetsCommand;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeymapPresets implements ModInitializer {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final String URL_ISSUE = "https://github.com/yuuki1293/KeymapPresets/issues";
    public static final int COLOR_LINK = 0x0000EE;

    private static KeyBinding keyBindingSave;

    @Override
    public void onInitialize() {
        KeymapPresetsCommand.register();
    }
}
