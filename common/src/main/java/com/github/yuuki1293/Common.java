package com.github.yuuki1293;

import com.github.yuuki1293.screen.KeymapPresetsMenuScreen;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Common {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final String URL_ISSUE = "https://github.com/yuuki1293/KeymapPresets/issues";
    public static final int COLOR_LINK = 0x0000EE;

    public static ConfigHolder<KeymapPresetsConfig> CONFIG;
    public static KeymapPresetsMenuScreen screenPresetsMenu;
    public static KeyBinding keyBindingMenu = new KeyBinding(
        "key.keymappresets.open_menu", // The translation key of the keybinding's name
        InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
        "category.keymappresets.generic" // The translation key of the keybinding's category.
    );
    public static boolean pressed = false;
    public static boolean wasPressed = false;
}
