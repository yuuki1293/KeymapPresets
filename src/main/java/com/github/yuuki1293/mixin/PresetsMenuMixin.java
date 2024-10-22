package com.github.yuuki1293.mixin;

import com.github.yuuki1293.KeymapPresets;
import com.github.yuuki1293.screen.KeymapPresetsMenuScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class PresetsMenuMixin {
    @Unique
    private static Screen screenPresetsMenu;
    @Unique
    private static boolean enableMenu = false;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(MinecraftClient client, CallbackInfo ci) {
        screenPresetsMenu = new KeymapPresetsMenuScreen(PresetsMenuMixin::setEnabledMenu);
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        final var client = KeymapPresets.CLIENT;
        final var width = client.getWindow().getScaledWidth();
        final var height = client.getWindow().getScaledHeight();
        final var mouseX = (int)(client.mouse.getX() * (double)width / (double)client.getWindow().getWidth());
        final var mouseY = (int)(client.mouse.getY() * (double)height / (double)client.getWindow().getHeight());

        if (KeymapPresets.pressed) {
            screenPresetsMenu.init(client, width, height);
            KeymapPresets.pressed = false;
            enableMenu = true;
        }

        if (KeymapPresets.keyBindingMenu.isPressed() && enableMenu)
            screenPresetsMenu.render(matrices, mouseX, mouseY, tickDelta);
        if (!KeymapPresets.keyBindingMenu.isPressed())
            enableMenu = false;
    }

    @Unique
    private static void setEnabledMenu(boolean enabled) {
        enableMenu = enabled;
    }
}
