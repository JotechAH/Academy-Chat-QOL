package com.jotech.academy_chat_qol.commands;

import com.jotech.academy_chat_qol.chat.IgnoreManager;
import com.jotech.academy_chat_qol.lang.Lang;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class IgnoreCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("acq")
            .then(literal("ignore")
                .then(argument("player", StringArgumentType.string())
                    .executes(IgnoreCommand::execute))));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        
        if (IgnoreManager.addIgnoredPlayer(playerName)) {
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.ignore.success")));
        } else {
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.ignore.already")));
        }
        
        return 1;
    }
}