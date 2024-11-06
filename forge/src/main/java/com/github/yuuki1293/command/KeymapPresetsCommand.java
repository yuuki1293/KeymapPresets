package com.github.yuuki1293.command;

import com.github.yuuki1293.IOLogic;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

import static com.github.yuuki1293.Common.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeymapPresetsCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
        Arrays.stream(IOLogic.getNames()).map(KeymapPresetsCommand::fixBadString)
        , builder
    );

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            CommandManager.literal("keymap")
                .then(CommandManager.literal("help")
                    .executes(KeymapPresetsCommand::commandHelp))

                .then(CommandManager.literal("save")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(KeymapPresetsCommand::commandSave)))

                .then(CommandManager.literal("load")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(KeymapPresetsCommand::commandLoad)))

                .then(CommandManager.literal("list")
                    .executes(KeymapPresetsCommand::commandList))

                .then(CommandManager.literal("clear")
                    .executes(KeymapPresetsCommand::commandClear))

                .then(CommandManager.literal("rename")
                    .then(CommandManager.argument("old", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .then(CommandManager.argument("new", StringArgumentType.string())
                            .executes(KeymapPresetsCommand::commandRename))))

                .then(CommandManager.literal("delete")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(KeymapPresetsCommand::commandDelete)))
        );
    }

    private static int commandHelp(CommandContext<ServerCommandSource> context) {
        sendFeedback(linkText("https://github.com/yuuki1293/KeymapPresets/blob/release1.18/README.md"));
        return 1;
    }

    private static int commandSave(CommandContext<ServerCommandSource> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (IOLogic.save(presetName)) {
            sendError(new TranslatableText("text.keymappresets.save_failure", presetName));
            sendFeedback(linkText(URL_ISSUE));
        } else
            sendFeedback(new TranslatableText("text.keymappresets.save_success", presetName));
        return 1;
    }

    private static int commandLoad(CommandContext<ServerCommandSource> context) {
        final String presetName = StringArgumentType.getString(context, "name");
        if (IOLogic.load(presetName))
            sendError(new TranslatableText("text.keymappresets.load_failure", presetName));
        else
            sendFeedback(new TranslatableText("text.keymappresets.load_success", presetName));
        return 1;
    }

    private static int commandList(CommandContext<ServerCommandSource> context) {
        Arrays.stream(IOLogic.getNames())
            .map(KeymapPresetsCommand::fixBadString)
            .forEach(preset -> sendFeedback(new LiteralText(preset)));
        return 1;
    }

    private static int commandClear(CommandContext<ServerCommandSource> context) {
        if (IOLogic.clear()) {
            sendError(new TranslatableText("text.keymappresets.clear_failure"));
            sendFeedback(linkText(URL_ISSUE));
        } else {
            sendFeedback(new TranslatableText("text.keymappresets.clear_success"));
        }
        return 1;
    }

    private static int commandRename(CommandContext<ServerCommandSource> context) {
        final String presetName = StringArgumentType.getString(context, "old");
        final String newName = StringArgumentType.getString(context, "new");
        if (IOLogic.move(presetName, newName, false)) {
            sendError(new TranslatableText("text.keymappresets.rename_failure", newName));
        } else {
            sendFeedback(new TranslatableText("text.keymappresets.rename_success", presetName, newName));
        }
        return 1;
    }

    private static int commandDelete(CommandContext<ServerCommandSource> context) {
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
