package com.github.yuuki1293;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static com.github.yuuki1293.KeymapPresets.*;

public class IOLogic {
    private static final File DIR_KEYMAPPRESETS = new File(CLIENT.runDirectory, MOD_ID);

    public static boolean saveKeymap(String presetName) {
        final File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");

        try {
            if (DIR_KEYMAPPRESETS.mkdirs())
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
            CONFIG.get().selectedPreset = presetName;
            CONFIG.save();
            return false;
        } catch (IOException e) {
            LOGGER.error("Couldn't write preset file", e);
            return true;
        }
    }

    public static boolean loadKeymap(String presetName) {
        final File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");

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

            CONFIG.get().selectedPreset = presetName;
            CONFIG.save();
            KeyBinding.updateKeysByCode();
            return false;
        } catch (Exception e) {
            LOGGER.warn("Keymap file does not exist", e);
            return true;
        }
    }

    public static String[] getPresets() {
        File[] rawFiles = DIR_KEYMAPPRESETS.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (rawFiles == null)
            return new String[0];

        return Arrays.stream(rawFiles)
            .sorted(Comparator.comparingLong(File::lastModified))
            .map(file -> FilenameUtils.removeExtension(file.getName()))
            .toArray(String[]::new);
    }

    public static boolean clearPresets() {
        try {
            FileUtils.cleanDirectory(DIR_KEYMAPPRESETS);
            CONFIG.get().selectedPreset = "";
            CONFIG.save();
            return false;
        } catch (IOException e) {
            LOGGER.error("Couldn't clean preset directory", e);
            return true;
        }
    }

    public static boolean movePresets(String presetName, String newName, boolean simulation) {
        final File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");
        final File newFile = new File(DIR_KEYMAPPRESETS, newName + ".txt");

        if(simulation) {
            return newFile.exists();
        }

        try {
            Files.move(presetFile.toPath(), newFile.toPath());
            return false;
        } catch (FileAlreadyExistsException e) {
            return true;
        } catch (Exception e) {
            LOGGER.error("Couldn't move preset file", e);
            return true;
        }
    }
}
