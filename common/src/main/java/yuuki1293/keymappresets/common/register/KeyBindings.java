package yuuki1293.keymappresets.common.register;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding keyBindingMenu = new KeyBinding(
        "key.keymappresets.open_menu", // The translation key of the keybinding's name
        InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
        "category.keymappresets.generic" // The translation key of the keybinding's category.
    );

    public static void register() {
        KeyMappingRegistry.register(keyBindingMenu);
    }
}
