package com.github.yuuki1293.command;

import com.github.yuuki1293.IOLogic;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.*;

import java.util.Arrays;

import static com.github.yuuki1293.IOLogic.*;
import static com.github.yuuki1293.KeymapPresets.*;

public class KeymapPresetsCommand {
    public static final SuggestionProvider<FabricClientCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
        Arrays.stream(getPresets()).map(KeymapPresetsCommand::fixBadString)
        , builder
    );

    public static void register() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("keymap")
                .then(ClientCommandManager.literal("help")
                    .executes(KeymapPresetsCommand::commandHelp))

                .then(ClientCommandManager.literal("save")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(KeymapPresetsCommand::commandSave)))

                .then(ClientCommandManager.literal("load")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(KeymapPresetsCommand::commandLoad)))

                .then(ClientCommandManager.literal("list")
                    .executes(KeymapPresetsCommand::commandList))

                .then(ClientCommandManager.literal("clear")
                    .executes(KeymapPresetsCommand::commandClear))

                .then(ClientCommandManager.literal("rename")
                    .then(ClientCommandManager.argument("old", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .then(ClientCommandManager.argument("new", StringArgumentType.string())
                            .executes(KeymapPresetsCommand::commandRename))))
        );
    }

    private static int commandHelp(CommandContext<FabricClientCommandSource> context) {
        final var source = context.getSource();
        source.sendFeedback(linkText("https://github.com/yuuki1293/KeymapPresets/blob/release1.18/README.md"));
        return 1;
    }

    private static int commandSave(CommandContext<FabricClientCommandSource> context) {
        final var source = context.getSource();
        final String presetName = StringArgumentType.getString(context, "name");
        if (saveKeymap(presetName)) {
            source.sendError(new TranslatableText("text.keymappresets.save_failure", presetName));
            source.sendFeedback(linkText(URL_ISSUE));
        } else
            source.sendFeedback(new TranslatableText("text.keymappresets.save_success", presetName));
        return 1;
    }

    private static int commandLoad(CommandContext<FabricClientCommandSource> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (loadKeymap(presetName))
            context.getSource().sendError(new TranslatableText("text.keymappresets.load_failure", presetName));
        else
            context.getSource().sendFeedback(new TranslatableText("text.keymappresets.load_success", presetName));
        return 1;
    }

    private static int commandList(CommandContext<FabricClientCommandSource> context) {
        Arrays.stream(getPresets())
            .map(KeymapPresetsCommand::fixBadString)
            .forEach(preset -> context.getSource().sendFeedback(new LiteralText(preset)));
        return 1;
    }

    private static int commandClear(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (clearPresets()) {
            source.sendError(new TranslatableText("text.keymappresets.clear_failure"));
            source.sendFeedback(linkText(URL_ISSUE));
        } else {
            source.sendFeedback(new TranslatableText("text.keymappresets.clear_success"));
        }
        return 1;
    }

    private static int commandRename(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        final String presetName = StringArgumentType.getString(context, "old");
        final String newName = StringArgumentType.getString(context, "new");
        if (IOLogic.movePresets(presetName, newName, false)){
            source.sendError(new TranslatableText("text.keymappresets.rename_failure", newName));
        } else {
            source.sendFeedback(new TranslatableText("text.keymappresets.rename_success", presetName, newName));
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

    private static String fixBadString(String s){
        if (s.contains(" ") // contain space
            || s.matches(".*[^\u0000-\u007F].*")) // contain multibyte character
            return "\"" + s + "\"";
        else
            return s;
    }
}
