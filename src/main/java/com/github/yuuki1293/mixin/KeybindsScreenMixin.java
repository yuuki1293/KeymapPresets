package com.github.yuuki1293.mixin;

import com.github.yuuki1293.IOLogic;
import com.github.yuuki1293.screen.EditPresetWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeybindsScreen.class)
abstract public class KeybindsScreenMixin extends GameOptionsScreen {
    @Unique
    EditPresetWidget editPresetWidget;

    public KeybindsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        editPresetWidget = this.addSelectableChild(new EditPresetWidget(this.width / 2 - 155, 20, 310, 20, this));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        editPresetWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        final var isHovered = editPresetWidget.children().stream().anyMatch(b -> b.isMouseOver(mouseX, mouseY));
        if (!isHovered)
            editPresetWidget.closeButtons();
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V"), index = 5)
    private ButtonWidget.PressAction injectDone(int i, int j, int k, int l, Text text, ButtonWidget.PressAction pressAction) {
        if (text.equals(ScreenTexts.DONE)) {
            return button -> {
                pressAction.onPress(button);
                IOLogic.saveKeymap(editPresetWidget.getSelected());
            };
        }

        return pressAction;
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/GameOptionsScreen;keyPressed(III)Z"), cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(editPresetWidget.isEditing()) {
            editPresetWidget.keyPressed(keyCode, scanCode, modifiers);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public void close(){
        super.close();
        IOLogic.saveKeymap(editPresetWidget.getSelected());
    }
}
