package com.github.yuuki1293.screen;

import com.github.yuuki1293.IOLogic;
import com.github.yuuki1293.KeymapPresets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditPresetWidget extends AbstractParentElement implements Drawable, Selectable {
    private final static MinecraftClient CLIENT = KeymapPresets.CLIENT;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final ArrayList<ButtonWidget> buttons = new ArrayList<>(0);
    private final ButtonWidget selectedButton;
    private final ButtonWidget renameButton;
    private final TextFieldWidget renameField;

    public EditPresetWidget(int x, int y, int width, int height, Screen parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        final var config = KeymapPresets.CONFIG.get();
        this.selectedButton = new ButtonWidget(this.x, this.y, 150, 20,
            new LiteralText(config.selectedPreset), button -> showButtons());
        this.renameField = new RenameFieldWidget(CLIENT.textRenderer, this.x, this.y, 150, 20, new LiteralText(getSelected()));
        this.renameField.visible = false;
        this.renameButton = new ButtonWidget(this.x + this.width / 2 + 5, this.y, 20, 20, new LiteralText("R"), button -> {
            selectedButton.visible = false;
            parent.focusOn(renameField);
            this.focusOn(renameField);
            renameField.setVisible(true);
            renameField.setText(getSelected());
            renameField.setSelectionStart(0);
            renameField.setTextFieldFocused(true);
        }, (button, matrices, mouseX, mouseY) -> {
            if(button.active){
                parent.renderOrderedTooltip(matrices, List.of(OrderedText.styledForwardsVisitedString("Rename", Style.EMPTY)), mouseX, mouseY);
            }
        }) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                super.mouseClicked(mouseX, mouseY, button);
                return false;
            }
        };
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        selectedButton.active = buttons.isEmpty();
        selectedButton.render(matrices, mouseX, mouseY, delta);
        for (ButtonWidget button : buttons) {
            button.render(matrices, mouseX, mouseY, delta);
        }
        renameButton.active = !renameField.isActive();
        renameButton.render(matrices, mouseX, mouseY, delta);
        renameField.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        final Element[] children = {
            selectedButton,
            renameField,
            renameButton
        };
        return Stream.concat(buttons.stream(),
                Arrays.stream(children))
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

    public String getSelected() {
        return selectedButton.getMessage().getString();
    }

    public boolean isEditing() {
        return renameField.isVisible();
    }

    private class RenameFieldWidget extends TextFieldWidget {
        private static final BiFunction<String, Integer, OrderedText> TEXT_PROVIDER_NORMAL = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(
            string, Style.EMPTY
        );
        private static final BiFunction<String, Integer, OrderedText> TEXT_PROVIDER_ERROR = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(
            string, Style.EMPTY.withColor(Formatting.RED)
        );

        public RenameFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
            super(textRenderer, x, y, width, height, text);
            this.setChangedListener(newText -> {
                if (IOLogic.movePresets(getSelected(), newText, true))
                    renameField.setRenderTextProvider(TEXT_PROVIDER_ERROR);
                else
                    renameField.setRenderTextProvider(TEXT_PROVIDER_NORMAL);
            });
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (super.keyPressed(keyCode, scanCode, modifiers))
                return true;

            if (!this.isActive())
                return false;

            switch (keyCode) {
                case GLFW.GLFW_KEY_ESCAPE:
                    this.setVisible(false);
                    selectedButton.visible = true;
                    return true;
                case GLFW.GLFW_KEY_ENTER:
                    if (!IOLogic.movePresets(getSelected(), this.getText(), false))
                        selectedButton.setMessage(new LiteralText(this.getText()));
                    this.setVisible(false);
                    selectedButton.visible = true;
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            final var ret = super.mouseClicked(mouseX, mouseY, button);

            if (!this.isVisible())
                return ret;

            if (!this.isHovered()) {
                if (!IOLogic.movePresets(getSelected(), this.getText(), false))
                    selectedButton.setMessage(new LiteralText(this.getText()));
                this.setVisible(false);
                selectedButton.visible = true;
            }
            return ret;
        }
    }
}
