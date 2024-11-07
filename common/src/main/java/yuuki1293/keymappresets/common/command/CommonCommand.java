package yuuki1293.keymappresets.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import yuuki1293.keymappresets.common.IOLogic;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static yuuki1293.keymappresets.common.Common.*;
import static yuuki1293.keymappresets.common.Common.CLIENT;

@SuppressWarnings("unchecked")
public class CommonCommand {
    private static <T extends CommandSource> SuggestionProvider<T> getSuggestionProvider(Class<T> clazz) {
        return (context, builder) -> {
            try {
                return (CompletableFuture<Suggestions>) clazz.getMethod("suggestMatching", Stream.class, SuggestionsBuilder.class).invoke(
                    null,
                    Arrays.stream(IOLogic.getNames()).map(CommonCommand::fixBadString),
                    builder
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T extends CommandSource> void register(CommandDispatcher<T> dispatcher, Class<T> clazz) {
        final SuggestionProvider<T> suggestionProvider = getSuggestionProvider(clazz);

        dispatcher.register(
            LiteralArgumentBuilder.<T>literal("keymap")
                .then(LiteralArgumentBuilder.<T>literal("help")
                    .executes(CommonCommand::commandHelp))

                .then(LiteralArgumentBuilder.<T>literal("save")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(suggestionProvider)
                        .executes(CommonCommand::commandSave)))

                .then(LiteralArgumentBuilder.<T>literal("load")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(suggestionProvider)
                        .executes(CommonCommand::commandLoad)))

                .then(LiteralArgumentBuilder.<T>literal("list")
                    .executes(CommonCommand::commandList))

                .then(LiteralArgumentBuilder.<T>literal("clear")
                    .executes(CommonCommand::commandClear))

                .then(LiteralArgumentBuilder.<T>literal("rename")
                    .then(RequiredArgumentBuilder.<T, String>argument("old", StringArgumentType.string())
                        .suggests(suggestionProvider)
                        .then(RequiredArgumentBuilder.<T, String>argument("new", StringArgumentType.string())
                            .executes(CommonCommand::commandRename))))

                .then(LiteralArgumentBuilder.<T>literal("delete")
                    .then(RequiredArgumentBuilder.<T, String>argument("name", StringArgumentType.string())
                        .suggests(suggestionProvider)
                        .executes(CommonCommand::commandDelete)))
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
