package yuuki1293.keymappresets.common;

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

import static yuuki1293.keymappresets.common.Common.CLIENT;
import static yuuki1293.keymappresets.common.Common.CONFIG;
import static yuuki1293.keymappresets.common.Common.LOGGER;
import static yuuki1293.keymappresets.common.Common.MOD_ID;

public class IOLogic {
    private static final File DIR_KEYMAPPRESETS = new File(CLIENT.runDirectory, MOD_ID);

    public static boolean save(String presetName) {
        if (presetName == null || presetName.isEmpty()) {
            logNull();
            return true;
        }

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

    public static boolean load(String presetName) {
        if (presetName == null || presetName.isEmpty()) {
            logNull();
            return true;
        }

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

    public static String[] getNames() {
        File[] rawFiles = DIR_KEYMAPPRESETS.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (rawFiles == null)
            return new String[0];

        return Arrays.stream(rawFiles)
            .sorted(Comparator.comparingLong(File::lastModified))
            .map(file -> FilenameUtils.removeExtension(file.getName()))
            .toArray(String[]::new);
    }

    public static boolean clear() {
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

    public static boolean move(String presetName, String newName, boolean simulation) {
        if (presetName == null || presetName.isEmpty()) {
            logNull();
            return true;
        }

        final File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");
        final File newFile = new File(DIR_KEYMAPPRESETS, newName + ".txt");

        if (presetName.equals(newName)) {
            return false;
        }

        if (simulation) {
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

    public static boolean delete(String presetName) {
        if (presetName == null || presetName.isEmpty()) {
            logNull();
            return true;
        }

        final File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");

        try {
            FileUtils.delete(presetFile);
            final var next = Arrays.stream(getNames()).findFirst();
            if (next.isPresent()) {
                IOLogic.load(next.get());
            } else {
                CONFIG.get().selectedPreset = "";
                CONFIG.save();
            }
            return false;
        } catch (IOException e) {
            LOGGER.error("Couldn't delete preset", e);
            return true;
        }
    }

    public static String genPrimaryName(String primary) {
        var presetName = primary;
        int i = 1;
        File presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");

        while (presetFile.exists()) {
            presetName = primary + " " + i;
            presetFile = new File(DIR_KEYMAPPRESETS, presetName + ".txt");
            i++;
        }

        return presetName;
    }

    private static void logNull() {
        LOGGER.error("Preset name is null");
    }
}
