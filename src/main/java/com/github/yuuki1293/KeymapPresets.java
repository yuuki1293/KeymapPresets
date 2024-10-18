package com.github.yuuki1293;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KeymapPresets implements ModInitializer {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding keyBindingSave;

    @Override
    public void onInitialize() {
        keyBindingSave = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.keymappresets.save", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.keymappresets.generic" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBindingSave.wasPressed()) {
                String presetName = "sample preset";
                final File keymapDirectory = new File(client.runDirectory, MOD_ID);
                final File presetFile = new File(keymapDirectory, presetName);

                try {
                    if (keymapDirectory.mkdirs())
                        LOGGER.info("Created keymap directory");

                    if (presetFile.createNewFile())
                        LOGGER.info("New keymap file created");

                } catch (IOException e) {
                    LOGGER.error("Couldn't create preset file", e);
                }

                try (FileWriter fileWriter = new FileWriter(presetFile)) {
                    for (KeyBinding key : client.options.allKeys) {
                        String keymap = String.format("%s:%s", key.getTranslationKey(), key.getBoundKeyTranslationKey());
                        fileWriter.append(keymap).append("\n");
                    }
                } catch (IOException e) {
                    LOGGER.error("Couldn't write preset file", e);
                }
            }
        });
    }
}
