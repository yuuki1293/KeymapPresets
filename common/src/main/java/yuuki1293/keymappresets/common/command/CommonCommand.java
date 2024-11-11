package yuuki1293.keymappresets.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import yuuki1293.keymappresets.common.IOLogic;
import yuuki1293.keymappresets.common.SortOrder;
import yuuki1293.keymappresets.common.SortType;

import java.util.Arrays;

import static yuuki1293.keymappresets.common.Common.*;
import static yuuki1293.keymappresets.common.Common.CLIENT;

public class CommonCommand {
    private static <T extends CommandSource, E extends Enum<E>> SuggestionProvider<T> getEnumSuggestionProvider(Class<E> enumClass) {
        return ((context, builder) -> CommandSource.suggestMatching(
            Arrays.stream(enumClass.getEnumConstants()).map(Object::toString),
            builder
        ));
    }

    public static <T extends CommandSource> void register(CommandDispatcher<T> dispatcher) {
        final SuggestionProvider<T> presetSuggestionProvider = (context, builder) -> CommandSource.suggestMatching(
            Arrays.stream(IOLogic.getNames()).map(CommonCommand::fixBadString),
            builder
        );
        final SuggestionProvider<T> sortTypeSuggestionProvider = getEnumSuggestionProvider(SortType.class);
        final SuggestionProvider<T> sortOrderSuggestionProvider = getEnumSuggestionProvider(SortOrder.class);

        dispatcher.register(
            LiteralArgumentBuilder.<T>literal("keymap")
                .then(LiteralArgumentBuilder.<T>literal("help")
                    .executes(CommonCommand::commandHelp))

                .then(LiteralArgumentBuilder.<T>literal("save")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(presetSuggestionProvider)
                        .executes(CommonCommand::commandSave)))

                .then(LiteralArgumentBuilder.<T>literal("load")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(presetSuggestionProvider)
                        .executes(CommonCommand::commandLoad)))

                .then(LiteralArgumentBuilder.<T>literal("list")
                    .executes(CommonCommand::commandList))

                .then(LiteralArgumentBuilder.<T>literal("clear")
                    .executes(CommonCommand::commandClear))

                .then(LiteralArgumentBuilder.<T>literal("rename")
                    .then(RequiredArgumentBuilder.<T, String>argument("old", StringArgumentType.string())
                        .suggests(presetSuggestionProvider)
                        .then(RequiredArgumentBuilder.<T, String>argument("new", StringArgumentType.string())
                            .executes(CommonCommand::commandRename))))

                .then(LiteralArgumentBuilder.<T>literal("delete")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(presetSuggestionProvider)
                        .executes(CommonCommand::commandDelete)))

                .then(LiteralArgumentBuilder.<T>literal("sort")
                    .then(RequiredArgumentBuilder.<T, String>argument("type", StringArgumentType.string())
                        .suggests(sortTypeSuggestionProvider)
                        .then(RequiredArgumentBuilder.<T, String>argument("order", StringArgumentType.string())
                            .suggests(sortOrderSuggestionProvider)
                            .executes(CommonCommand::commandSort))))
        );
    }

    public static int commandHelp(CommandContext<?> context) {
        sendFeedback(linkText("https://github.com/yuuki1293/KeymapPresets/blob/release1.18/README.md"));
        return 1;
    }

    public static int commandSave(CommandContext<?> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (IOLogic.save(presetName)) {
            sendError(new TranslatableText("text.keymappresets.save_failure", presetName));
            sendFeedback(linkText(URL_ISSUE));
        } else
            sendFeedback(new TranslatableText("text.keymappresets.save_success", presetName));
        return 1;
    }

    public static int commandLoad(CommandContext<?> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (IOLogic.load(presetName))
            sendError(new TranslatableText("text.keymappresets.load_failure", presetName));
        else
            sendFeedback(new TranslatableText("text.keymappresets.load_success", presetName));
        return 1;
    }

    public static int commandList(CommandContext<?> context) {
        Arrays.stream(IOLogic.getNames())
            .map(CommonCommand::fixBadString)
            .forEach(preset -> sendFeedback(new LiteralText(preset)));
        return 1;
    }

    public static int commandClear(CommandContext<?> context) {
        if (IOLogic.clear()) {
            sendError(new TranslatableText("text.keymappresets.clear_failure"));
            sendFeedback(linkText(URL_ISSUE));
        } else {
            sendFeedback(new TranslatableText("text.keymappresets.clear_success"));
        }
        return 1;
    }

    public static int commandRename(CommandContext<?> context) {
        final String presetName = StringArgumentType.getString(context, "old");
        final String newName = StringArgumentType.getString(context, "new");
        if (IOLogic.move(presetName, newName, false)) {
            sendError(new TranslatableText("text.keymappresets.rename_failure", newName));
        } else {
            sendFeedback(new TranslatableText("text.keymappresets.rename_success", presetName, newName));
        }
        return 1;
    }

    public static int commandDelete(CommandContext<?> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (IOLogic.delete(presetName)) {
            sendError(new TranslatableText("text.keymappresets.delete_failure"));
            sendFeedback(linkText(URL_ISSUE));
        } else {
            sendFeedback(new TranslatableText("text.keymappresets.delete_success"));
        }
        return 1;
    }

    public static int commandSort(CommandContext<?> context) {
        try {
            final SortType sortType = Enum.valueOf(SortType.class, StringArgumentType.getString(context, "type"));
            final SortOrder sortOrder = Enum.valueOf(SortOrder.class, StringArgumentType.getString(context, "order"));
            CONFIG.get().sortType = sortType;
            CONFIG.get().sortOrder = sortOrder;
            CONFIG.save();
            sendFeedback(new TranslatableText("text.keymappresets.sort_success"));
        } catch (IllegalArgumentException e) {
            sendError(new TranslatableText("text.keymappresets.sort_failure"));
        }
        return 1;
    }

    private static Text linkText(String url) {
        return new LiteralText(url)
            .setStyle(Style.EMPTY
                .withColor(COLOR_LINK)
                .withUnderline(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    private static String fixBadString(String s) {
        if (s.contains(" ") // contain space
            || s.matches(".*[^\u0000-\u007F].*")) // contain multibyte character
            return "\"" + s + "\"";
        else
            return s;
    }

    private static void sendFeedback(Text message) {
        CLIENT.inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID);
    }

    private static void sendError(Text message) {
        CLIENT.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText("").append(message).formatted(Formatting.RED), Util.NIL_UUID);
    }
}
