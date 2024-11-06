package com.github.yuuki1293.mixin;

import com.github.yuuki1293.IOLogic;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.yuuki1293.Common.CONFIG;

@Mixin(MinecraftClient.class)
public class MinecaftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        IOLogic.load(CONFIG.get().selectedPreset);
    }
}
