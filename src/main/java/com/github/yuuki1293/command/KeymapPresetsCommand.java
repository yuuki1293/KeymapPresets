package com.github.yuuki1293.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Arrays;

import static com.github.yuuki1293.IOLogic.*;
import static com.github.yuuki1293.KeymapPresets.*;

public class KeymapPresetsCommand {
    public static final SuggestionProvider<FabricClientCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
        getPresets(), builder
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
            source.sendError(new LiteralText("Failed to save Keymap. Please report the issue to the GitHub repository."));
            source.sendFeedback(linkText(URL_ISSUE));
        } else
            source.sendFeedback(new LiteralText("Keymap " + presetName + " saved!"));
        return 1;
    }

    private static int commandLoad(CommandContext<FabricClientCommandSource> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (loadKeymap(presetName))
            context.getSource().sendError(new LiteralText("Failed to load Keymap " + presetName));
        else
            context.getSource().sendFeedback(new LiteralText("Keymap " + presetName + " loaded!"));
        return 1;
    }

    private static int commandList(CommandContext<FabricClientCommandSource> context) {
        Arrays.stream(getPresets())
            .forEach(preset -> context.getSource().sendFeedback(new LiteralText(preset)));
        return 1;
    }

    private static int commandClear(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (clearPresets()) {
            source.sendError(new LiteralText("Failed to clear keymap. Please report the issue to the GitHub repository."));
            source.sendFeedback(linkText(URL_ISSUE));
        } else {
            source.sendFeedback(new LiteralText("Keymaps cleared."));
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
}
