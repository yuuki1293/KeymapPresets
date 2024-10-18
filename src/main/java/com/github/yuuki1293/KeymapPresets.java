package com.github.yuuki1293;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

public class KeymapPresets implements ModInitializer {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient client = MinecraftClient.getInstance();

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
                saveKeymap(presetName);
            }
        });

        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("keymap")
                .then(ClientCommandManager.literal("save")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            final String presetName = StringArgumentType.getString(context, "name");
                            saveKeymap(presetName);
                            return 1;
                        })))
                .then(ClientCommandManager.literal("load")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            final String presetName = StringArgumentType.getString(context, "name");
                            loadKeymap(presetName);
                            return 1;
                        })))
        );
    }

    private void saveKeymap(String presetName) {
        final File keymapDirectory = new File(client.runDirectory, MOD_ID);
        final File presetFile = new File(keymapDirectory, presetName + ".txt");

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

    private void loadKeymap(String presetName) {
        final File keymapDirectory = new File(client.runDirectory, MOD_ID);
        final File presetFile = new File(keymapDirectory, presetName + ".txt");

        try (BufferedReader br = new BufferedReader(new FileReader(presetFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] split = line.split(":");
                String translationKey = split[0];
                String keyName = split[1];

                Optional<KeyBinding> keyBinding = Arrays.stream(client.options.allKeys)
                    .filter(keyBinding_ -> keyBinding_.getTranslationKey().equals(translationKey))
                    .findFirst();

                keyBinding.ifPresent(binding -> binding.setBoundKey(InputUtil.fromTranslationKey(keyName)));

            }

            KeyBinding.updateKeysByCode();
        } catch (Exception e) {
            LOGGER.info("Keymap file does not exist", e);
        }
    }

    private File[] getPresetFiles() {
        final File keymapDirectory = new File(client.runDirectory, MOD_ID);

        return keymapDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });
    }
}
