package com.github.yuuki1293.screen;

import com.github.yuuki1293.IOLogic;
import com.github.yuuki1293.KeymapPresets;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class KeymapPresetsMenuScreen extends Screen {
    public KeymapPresetsMenuScreen() {
        super(LiteralText.EMPTY);
    }

    @Override
    public void init() {
        super.init();

        final var x0 = this.width / 2;
        final var y0 = this.height / 2;
        final double radius = this.height / 3.0;

        final var presets = IOLogic.getPresets();
        final var len = presets.length;

        for (int i = 0; i < len; i++) {
            final int x = x0 + (int) (Math.cos(2 * Math.PI / (double)len * (double)i - Math.PI / 2.0) * radius);
            final int y = y0 + (int) (Math.sin(2 * Math.PI / (double)len * (double)i - Math.PI / 2.0) * radius);
            final var presetName = presets[i];

            this.addDrawableChild(new ButtonWidget(x - 25, y - 10, 50, 20, new LiteralText(presetName), button -> {
                final var player = KeymapPresets.CLIENT.player;
                if (player == null) {
                    this.close();
                    return;
                }

                if (IOLogic.loadKeymap(presetName))
                    player.sendMessage(new LiteralText("Failed to load Keymap " + presetName), true);
                else
                    player.sendMessage(new LiteralText("Keymap " + presetName + " loaded!"), true);
                this.close();
            }));
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
