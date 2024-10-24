package com.github.yuuki1293.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ControlsListWidget.class)
public class ControlsListWidgetMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ElementListWidget;<init>(Lnet/minecraft/client/MinecraftClient;IIIII)V"), index = 3)
    private static int injected(int x) {
        return x + 22;
    }
}
