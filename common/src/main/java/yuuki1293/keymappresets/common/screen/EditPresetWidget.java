package yuuki1293.keymappresets.common.screen;

import yuuki1293.keymappresets.common.EnumUtil;
import yuuki1293.keymappresets.common.IOLogic;
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
import yuuki1293.keymappresets.common.SortOrder;
import yuuki1293.keymappresets.common.SortType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yuuki1293.keymappresets.common.Common.CLIENT;
import static yuuki1293.keymappresets.common.Common.CONFIG;

public class EditPresetWidget extends AbstractParentElement implements Drawable, Selectable {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final ArrayList<ButtonWidget> buttons = new ArrayList<>(0);
    private final ButtonWidget selectedButton;
    private final ButtonWidget addButton;
    private final ButtonWidget deleteButton;
    private final ButtonWidget renameButton;
    private final ButtonWidget sortTypeButton;
    private final ButtonWidget sortOrderButton;
    private final TextFieldWidget renameField;

    public EditPresetWidget(int x, int y, int width, int height, Screen parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.selectedButton = new ButtonWidget(this.x, this.y, 150, 20,
            new LiteralText(CONFIG.get().selectedPreset), button -> showButtons());
        this.renameField = new RenameFieldWidget(CLIENT.textRenderer, this.x, this.y, 150, 20, new LiteralText(getSelected()));
        this.renameField.visible = false;
        this.renameButton = new InFocusedButtonWidget(this.x + this.width / 2 + 49, this.y, 20, 20, new LiteralText("R"), button -> {
            selectedButton.visible = false;
            parent.focusOn(renameField);
            this.focusOn(renameField);
            renameField.setVisible(true);
            renameField.setText(getSelected());
            renameField.setSelectionStart(0);
            renameField.setTextFieldFocused(true);
        }, (button, matrices, mouseX, mouseY) -> {
            if (button.active) {
                parent.renderOrderedTooltip(matrices, List.of(OrderedText.styledForwardsVisitedString("Rename", Style.EMPTY)), mouseX, mouseY);
            }
        });
        this.addButton = new InFocusedButtonWidget(this.x + this.width / 2 + 5, this.y, 20, 20, new LiteralText("+"), button -> {
            final var name = IOLogic.genPrimaryName("New Preset");
            IOLogic.save(name);
            selectedButton.setMessage(new LiteralText(name));
            renameButton.onPress();
        });
        this.deleteButton = new InFocusedButtonWidget(this.x + this.width / 2 + 27, this.y, 20, 20, new LiteralText("-"), button -> {
            IOLogic.delete(getSelected());
            selectedButton.setMessage(new LiteralText(CONFIG.get().selectedPreset));
        });
        this.sortTypeButton = new InFocusedButtonWidget(this.x + this.width / 2 + 71, this.y, 60, 20, new LiteralText(CONFIG.get().sortType.getString()), button -> {
            var sortType = CONFIG.get().sortType;
            SortType next = EnumUtil.next(sortType);
            CONFIG.get().sortType = next;
            CONFIG.save();
            button.setMessage(new LiteralText(next.getString()));
            if (!buttons.isEmpty())
                showButtons();
        });
        this.sortOrderButton = new InFocusedButtonWidget(this.x + this.width / 2 + 133, this.y, 20, 20, new LiteralText(CONFIG.get().sortOrder.getString()), button -> {
            var sortOrder = CONFIG.get().sortOrder;
            SortOrder next = EnumUtil.next(sortOrder);
            CONFIG.get().sortOrder = next;
            CONFIG.save();
            button.setMessage(new LiteralText(next.getString()));
            if (!buttons.isEmpty())
                showButtons();
        });
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
        addButton.active = !renameField.isActive();
        addButton.render(matrices, mouseX, mouseY, delta);
        deleteButton.active = !renameField.isActive();
        deleteButton.render(matrices, mouseX, mouseY, delta);
        sortTypeButton.render(matrices, mouseX, mouseY, delta);
        sortOrderButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        final Element[] children = {
            selectedButton,
            renameField,
            renameButton,
            addButton,
            deleteButton,
            sortTypeButton,
            sortOrderButton
        };
        return Stream.concat(buttons.stream(),
                Arrays.stream(children))
            .collect(Collectors.toList());
    }

    public void showButtons() {
        buttons.clear();
        final var presets = IOLogic.getNames();
        final int x = this.x;
        int y = this.y + 20;
        for (var preset : presets) {
            buttons.add(new ButtonWidget(x, y, 150, 20, new LiteralText(preset), button -> {
                closeButtons();
                IOLogic.save(getSelected());
                IOLogic.load(preset);
                selectedButton.setMessage(new LiteralText(CONFIG.get().selectedPreset));
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
                if (IOLogic.move(getSelected(), newText, true))
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
                    if (!IOLogic.move(getSelected(), this.getText(), false))
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
                if (!IOLogic.move(getSelected(), this.getText(), false))
                    selectedButton.setMessage(new LiteralText(this.getText()));
                this.setVisible(false);
                selectedButton.visible = true;
            }
            return ret;
        }
    }

    private static class InFocusedButtonWidget extends ButtonWidget {
        public InFocusedButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress);
        }

        public InFocusedButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier) {
            super(x, y, width, height, message, onPress, tooltipSupplier);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            return false;
        }
    }
}
