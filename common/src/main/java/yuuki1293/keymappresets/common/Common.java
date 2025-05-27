package yuuki1293.keymappresets.common;

import dev.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import yuuki1293.keymappresets.common.register.KeyBindings;
import yuuki1293.keymappresets.common.screen.KeymapPresetsMenuScreen;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static yuuki1293.keymappresets.common.register.KeyBindings.keyBindingMenu;

public class Common {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final String URL_ISSUE = "https://github.com/yuuki1293/KeymapPresets/issues";
    public static final int COLOR_LINK = 0x0000EE;

    public static ConfigHolder<KeymapPresetsConfig> CONFIG;
    public static KeymapPresetsMenuScreen screenPresetsMenu;

    public static boolean pressed = false;
    public static boolean wasPressed = false;

    static {
        AutoConfig.register(KeymapPresetsConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KeymapPresetsConfig.class);
    }

    public static void init() {
        KeyBindings.register();

        ClientTickEvent.CLIENT_POST.register(Common::keyBindingMenuEvent);

        screenPresetsMenu = new KeymapPresetsMenuScreen();
    }

    private static void keyBindingMenuEvent(MinecraftClient client) {
        if (keyBindingMenu.wasPressed()) { // initialize
            client.mouse.unlockCursor();
            pressed = true;
        }

        if (wasPressed && !keyBindingMenu.isPressed()) { // finalize
            client.mouse.lockCursor();
        }

        wasPressed = keyBindingMenu.isPressed();

        if (screenPresetsMenu.visible) {
            for (int i = 0; i < client.options.hotbarKeys.length; i++) {
                if(client.options.hotbarKeys[i].isPressed()){
                    screenPresetsMenu.closeWith(i);
                }
            }
        }
    }
}
