package com.github.yuuki1293.screen;

import com.github.yuuki1293.IOLogic;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class KeymapPresetsMenuScreen extends Screen {
    private static final int radius = 500; // WIP

    public KeymapPresetsMenuScreen() {
        super(LiteralText.EMPTY);
    }

    @Override
    public void init() {
        super.init();

        final var x0 = this.width / 2;
        final var y0 = this.height / 2;

        final var presets = IOLogic.getPresets();
        final var len = presets.length;

        for (int i = 0; i < len; i++) {
            final int x = x0 + (int)Math.cos(2 * Math.PI / len * i) * radius;
            final int y = y0 + (int)Math.sin(2 * Math.PI / len * i) * radius;

            this.addSelectableChild(new ButtonWidget(x, y, 50, 50, new LiteralText(presets[i]), null));
        }
    }
}
