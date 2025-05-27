package yuuki1293.keymappresets.common.screen;

import net.minecraft.client.gui.DrawableHelper;
import yuuki1293.keymappresets.common.IOLogic;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import static yuuki1293.keymappresets.common.Common.CLIENT;

public class KeymapPresetsMenuScreen extends Screen {
    public boolean visible = false;

    public KeymapPresetsMenuScreen() {
        super(LiteralText.EMPTY);
    }

    @Override
    public void init() {
        super.init();

        final var x0 = this.width / 2;
        final var y0 = this.height / 2;
        final double radius = this.height / 3.0;

        final var presets = IOLogic.getNames();
        final var len = presets.length;

        for (int i = 0; i < len; i++) {
            final int x = x0 + (int) (Math.cos(2 * Math.PI / (double) len * (double) i - Math.PI / 2.0) * radius);
            final int y = y0 + (int) (Math.sin(2 * Math.PI / (double) len * (double) i - Math.PI / 2.0) * radius);
            var presetName = presets[i];
            var keyText = CLIENT.options.hotbarKeys[i].getBoundKeyLocalizedText();
            int width = CLIENT.textRenderer.getWidth(presetName);

            this.addDrawableChild(new ButtonWidget(x - 25, y - 10, 50, 20, new LiteralText(presetName), button -> {
                final var player = CLIENT.player;
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
                    drawCenteredText(matrices, CLIENT.textRenderer, keyText, x + width / 2, y + height + 2, 0x80ffffff);
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
