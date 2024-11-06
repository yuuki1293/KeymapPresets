package yuuki1293.keymappresets.common.mixin;

import yuuki1293.keymappresets.common.Common;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static yuuki1293.keymappresets.common.Common.CLIENT;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (Common.screenPresetsMenu.visible) {
            final var client = CLIENT;
            double mouseX = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();
            Common.screenPresetsMenu.mouseClicked(mouseX, mouseY, action);
            ci.cancel();
        }
    }
}
