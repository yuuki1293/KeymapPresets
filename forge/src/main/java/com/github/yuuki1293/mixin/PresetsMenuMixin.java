package com.github.yuuki1293.mixin;

import com.github.yuuki1293.Common;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.yuuki1293.Common.CLIENT;

@Mixin(ForgeIngameGui.class)
public class PresetsMenuMixin {
    @Inject(at = @At("HEAD"), method = "render")
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        final var client = CLIENT;
        final var screen = Common.screenPresetsMenu;
        final var width = client.getWindow().getScaledWidth();
        final var height = client.getWindow().getScaledHeight();
        final var mouseX = (int)(client.mouse.getX() * (double)width / (double)client.getWindow().getWidth());
        final var mouseY = (int)(client.mouse.getY() * (double)height / (double)client.getWindow().getHeight());

        if (Common.pressed) {
            Common.screenPresetsMenu.init(client, width, height);
            Common.pressed = false;
            screen.visible = true;
        }

        if (Common.keyBindingMenu.isPressed())
            screen.render(matrices, mouseX, mouseY, tickDelta);
        else
            screen.visible = false;
    }
}
