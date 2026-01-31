package com.jotech.academy_chat_qol.commands;

import com.jotech.academy_chat_qol.config.ConfigManager;
import com.jotech.academy_chat_qol.chat.IgnoreManager;
import com.jotech.academy_chat_qol.lang.Lang;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ReloadCommand {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>(.*?)</#\\1>");
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("acq")
            .then(literal("reload")
                .executes(ReloadCommand::execute)));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        try {
            ConfigManager.load();
            IgnoreManager.load();
            
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.reload.success")));
        } catch (Exception e) {
            context.getSource().sendFeedback(Text.literal(Lang.get("acq.reload.error") + e.getMessage()));
            e.printStackTrace();
        }
        
        return 1;
    }
    
    private static Text parseFormattedPreview(String text) {
        text = text.replace("{rank}", "一");
        text = text.replace("{nickname}", Lang.get("acq.status.player123"));
        text = text.replace("{message}", Lang.get("acq.status.testmessage"));
        
        MutableText result = Text.empty();
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        
        int lastEnd = 0;
        
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                if (!beforeText.isEmpty()) {
                    result.append(Text.literal(beforeText));
                }
            }
            
            String hexColor = matcher.group(1);
            String coloredText = matcher.group(2);
            
            try {
                int color = Integer.parseInt(hexColor, 16);
                MutableText coloredPart = Text.literal(coloredText);
                coloredPart.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
                result.append(coloredPart);
            } catch (NumberFormatException e) {
                result.append(Text.literal(coloredText));
            }
            
            lastEnd = matcher.end();
        }
        
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            if (!remainingText.isEmpty()) {
                result.append(Text.literal(remainingText));
            }
        }
        
        return result;
    }
}