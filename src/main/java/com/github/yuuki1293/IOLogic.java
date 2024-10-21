package com.github.yuuki1293;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

import static com.github.yuuki1293.KeymapPresets.*;

public class IOLogic {
    public static boolean saveKeymap(String presetName) {
        final File keymapDirectory = new File(CLIENT.runDirectory, MOD_ID);
        final File presetFile = new File(keymapDirectory, presetName + ".txt");

        try {
            if (keymapDirectory.mkdirs())
                LOGGER.info("Created keymap directory");

            if (presetFile.createNewFile())
                LOGGER.info("New keymap file created");

        } catch (IOException e) {
            LOGGER.error("Couldn't create preset file", e);
            return true;
        }

        try (FileWriter fileWriter = new FileWriter(presetFile)) {
            for (KeyBinding key : CLIENT.options.allKeys) {
                String keymap = String.format("%s:%s", key.getTranslationKey(), key.getBoundKeyTranslationKey());
                fileWriter.append(keymap).append("\n");
            }
            return false;
        } catch (IOException e) {
            LOGGER.error("Couldn't write preset file", e);
            return true;
        }
    }

    public static boolean loadKeymap(String presetName) {
        final File keymapDirectory = new File(CLIENT.runDirectory, MOD_ID);
        final File presetFile = new File(keymapDirectory, presetName + ".txt");

        try (BufferedReader br = new BufferedReader(new FileReader(presetFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] split = line.split(":");
                String translationKey = split[0];
                String keyName = split[1];

                Optional<KeyBinding> keyBinding = Arrays.stream(CLIENT.options.allKeys)
                    .filter(keyBinding_ -> keyBinding_.getTranslationKey().equals(translationKey))
                    .findFirst();

                keyBinding.ifPresent(binding -> binding.setBoundKey(InputUtil.fromTranslationKey(keyName)));

            }

            KeyBinding.updateKeysByCode();
            return false;
        } catch (Exception e) {
            LOGGER.info("Keymap file does not exist", e);
            return true;
        }
    }

    public static String[] getPresets() {
        final File keymapDirectory = new File(CLIENT.runDirectory, MOD_ID);

        File[] rawFiles = keymapDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (rawFiles == null)
            return new String[0];

        return Arrays.stream(rawFiles)
            .map(file -> FilenameUtils.removeExtension(file.getName()))
            .toArray(String[]::new);
    }

    public static boolean clearPresets() {
        final File keymapDirectory = new File(CLIENT.runDirectory, MOD_ID);
        try {
            FileUtils.cleanDirectory(keymapDirectory);
            return false;
        } catch (IOException e) {
            LOGGER.error("Couldn't clean preset directory", e);
            return true;
        }
    }
}
