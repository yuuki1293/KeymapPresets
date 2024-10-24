package com.github.yuuki1293.screen;

import com.github.yuuki1293.IOLogic;
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
        selectedButton = new ButtonWidget(this.x, this.y, 150, 20, new LiteralText("Select"), button -> showButtons());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        selectedButton.render(matrices, mouseX, mouseY, delta);
        selectedButton.active = buttons.isEmpty();
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
            }));
            y += 20;
        }
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
}
