package yuuki1293.keymappresets.common;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import yuuki1293.keymappresets.common.screen.KeymapPresetsMenuScreen;
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

    static {
        AutoConfig.register(KeymapPresetsConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KeymapPresetsConfig.class);
    }

    public static void init() {
        KeyMappingRegistry.register(keyBindingMenu);

        ClientTickEvent.CLIENT_POST.register(client -> {
            if (keyBindingMenu.wasPressed()) { // initialize
                client.mouse.unlockCursor();
                pressed = true;
            }
            if (wasPressed && !keyBindingMenu.isPressed()) { // finalize
                client.mouse.lockCursor();
            }

            wasPressed = keyBindingMenu.isPressed();
        });

        screenPresetsMenu = new KeymapPresetsMenuScreen();
    }
}
