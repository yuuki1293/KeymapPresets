package com.github.yuuki1293.mixin;

import com.github.yuuki1293.KeymapPresets;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class PresetsMenuMixin {
    @Inject(at = @At("HEAD"), method = "render")
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        final var client = KeymapPresets.CLIENT;
        final var screen = KeymapPresets.screenPresetsMenu;
        final var width = client.getWindow().getScaledWidth();
        final var height = client.getWindow().getScaledHeight();
        final var mouseX = (int)(client.mouse.getX() * (double)width / (double)client.getWindow().getWidth());
        final var mouseY = (int)(client.mouse.getY() * (double)height / (double)client.getWindow().getHeight());

        if (KeymapPresets.pressed) {
            KeymapPresets.screenPresetsMenu.init(client, width, height);
            KeymapPresets.pressed = false;
            screen.visible = true;
        }

        if (KeymapPresets.keyBindingMenu.isPressed())
            screen.render(matrices, mouseX, mouseY, tickDelta);
        else
            screen.visible = false;
    }
}
