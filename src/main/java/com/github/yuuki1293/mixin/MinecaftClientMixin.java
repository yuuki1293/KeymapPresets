package com.github.yuuki1293.mixin;

import com.github.yuuki1293.IOLogic;
import com.github.yuuki1293.KeymapPresets;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecaftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        IOLogic.load(KeymapPresets.CONFIG.get().selectedPreset);
    }
}
