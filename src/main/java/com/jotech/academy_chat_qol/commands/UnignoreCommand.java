package com.jotech.academy_chat_qol.commands;

import com.jotech.academy_chat_qol.chat.IgnoreManager;
import com.jotech.academy_chat_qol.lang.Lang;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class UnignoreCommand {
    
    private static final SuggestionProvider<FabricClientCommandSource> IGNORED_PLAYERS_SUGGESTION = (context, builder) -> {
        for (String player : IgnoreManager.getIgnoredPlayers()) {
            builder.suggest(player);
        }
        return builder.buildFuture();
    };
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("acq")
            .then(literal("unignore")
                .then(argument("player", StringArgumentType.string())
                    .suggests(IGNORED_PLAYERS_SUGGESTION)
                    .executes(UnignoreCommand::execute))));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        
        if (IgnoreManager.removeIgnoredPlayer(playerName)) {
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.unignore.success")));
        } else {
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.unignore.not_ignored")));
        }
        
        return 1;
    }
}