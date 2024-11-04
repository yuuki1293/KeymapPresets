package com.github.yuuki1293;

import com.github.yuuki1293.command.KeymapPresetsCommand;
import com.github.yuuki1293.screen.KeymapPresetsMenuScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(KeymapPresets.MOD_ID)
public class KeymapPresets {
    public static final String MOD_ID = "keymappresets";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final String URL_ISSUE = "https://github.com/yuuki1293/KeymapPresets/issues";
    public static final int COLOR_LINK = 0x0000EE;

    public static ConfigHolder<KeymapPresetsConfig> CONFIG;
    public static KeymapPresetsMenuScreen screenPresetsMenu;
    public static KeyBinding keyBindingMenu;
    public static boolean pressed = false;
    private static boolean wasPressed = false;

    public KeymapPresets() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeymapPresetsCommand.class);
        ClientRegistry.registerKeyBinding(keyBindingMenu);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onInitializeClient);

        keyBindingMenu = new KeyBinding(
            "key.keymappresets.open_menu", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
            "category.keymappresets.generic" // The translation key of the keybinding's category.
        );

        AutoConfig.register(KeymapPresetsConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(KeymapPresetsConfig.class);
    }

    public void onInitializeClient(final FMLClientSetupEvent event) {
        screenPresetsMenu = new KeymapPresetsMenuScreen();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyBindingMenu.wasPressed()) { // initialize
            CLIENT.mouse.unlockCursor();
            pressed = true;
        }
        if (wasPressed && !keyBindingMenu.isPressed()) { // finalize
            CLIENT.mouse.lockCursor();
        }

        wasPressed = keyBindingMenu.isPressed();
    }
}
