package yuuki1293.keymappresets.common.mixin;

import yuuki1293.keymappresets.common.IOLogic;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static yuuki1293.keymappresets.common.Common.CONFIG;

@Mixin(MinecraftClient.class)
public class MinecaftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        IOLogic.load(CONFIG.get().selectedPreset);
    }
}
