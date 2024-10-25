package com.github.yuuki1293.screen;

import com.github.yuuki1293.IOLogic;
import com.github.yuuki1293.KeymapPresets;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditPresetWidget extends AbstractParentElement implements Drawable, Selectable {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final ArrayList<ButtonWidget> buttons = new ArrayList<>(0);
    private final ButtonWidget selectedButton;

    public EditPresetWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        final var config = KeymapPresets.CONFIG.get();
        this.selectedButton = new ButtonWidget(this.x, this.y, 150, 20,
            new LiteralText(config.selectedPreset), button -> showButtons());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        selectedButton.active = buttons.isEmpty();
        selectedButton.render(matrices, mouseX, mouseY, delta);
        for (ButtonWidget button : buttons) {
            button.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<? extends Element> children() {
        return Stream.concat(buttons.stream(), Stream.of(selectedButton))
            .collect(Collectors.toList());
    }

    public void showButtons() {
        buttons.clear();
        final var presets = IOLogic.getPresets();
        final int x = this.x;
        int y = this.y + 20;
        for (var preset : presets) {
            buttons.add(new ButtonWidget(x, y, 150, 20, new LiteralText(preset), button -> {
                closeButtons();
                IOLogic.loadKeymap(preset);
                final var config = KeymapPresets.CONFIG.get();
                selectedButton.setMessage(new LiteralText(config.selectedPreset));
            }));
            y += 20;
        }
        IOLogic.saveKeymap(getSelected());
    }

    public void closeButtons() {
        buttons.clear();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public String getSelected(){
        return selectedButton.getMessage().getString();
    }
}
