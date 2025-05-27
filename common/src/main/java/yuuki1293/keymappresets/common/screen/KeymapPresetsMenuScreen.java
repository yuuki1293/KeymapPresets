package yuuki1293.keymappresets.common.screen;

import yuuki1293.keymappresets.common.IOLogic;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import static yuuki1293.keymappresets.common.Common.CLIENT;
import static yuuki1293.keymappresets.common.Common.CONFIG;

public class KeymapPresetsMenuScreen extends Screen {
    public boolean visible = false;

    public KeymapPresetsMenuScreen() {
        super(LiteralText.EMPTY);
    }

    @Override
    public void init() {
        super.init();

        var x0 = this.width / 2;
        var y0 = this.height / 2;
        double radius = this.height / 3.0;

        var presets = IOLogic.getNames();
        var len = presets.length;

        for (int i = 0; i < len; i++) {
            var config = CONFIG.get();
            int x = x0 + (int) (Math.cos(2 * Math.PI / (double) len * (double) i - Math.PI / 2.0) * radius);
            int y = y0 + (int) (Math.sin(2 * Math.PI / (double) len * (double) i - Math.PI / 2.0) * radius);
            var presetName = presets[i];
            var keyText = CLIENT.options.hotbarKeys[i].getBoundKeyLocalizedText();
            int width = Math.max(Math.min(CLIENT.textRenderer.getWidth(presetName) + 8, config.maxButtonWidth), config.minButtonWidth);

            this.addDrawableChild(new ButtonWidget(x - 25, y - 10, width, 20, new LiteralText(presetName), button -> {
                var player = CLIENT.player;
                if (player == null) {
                    this.close();
                    return;
                }

                if (closeWith(presetName))
                    player.sendMessage(new TranslatableText("text.keymappresets.load_failure", presetName), true);
                else
                    player.sendMessage(new TranslatableText("text.keymappresets.load_success", presetName), true);
            }) {
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    super.renderButton(matrices, mouseX, mouseY, delta);
                    drawCenteredText(matrices, CLIENT.textRenderer, keyText, x + width / 2, y + height + 2, config.shortcutTextColor);
                }
            });
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void close() {
        super.close();
        visible = false;
        if (client != null)
            client.mouse.lockCursor();
    }

    public boolean closeWith(int ordinal) {
        var presets = IOLogic.getNames();

        if (ordinal < 0 || ordinal >= presets.length) {
            close();
            return true;
        } else {
            return closeWith(presets[ordinal]);
        }
    }

    public boolean closeWith(String presetName) {
        this.close();
        return IOLogic.load(presetName);
    }
}
