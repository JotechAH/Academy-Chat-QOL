package com.jotech.academy_chat_qol.chat;

import com.jotech.academy_chat_qol.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFormatter {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>(.*?)</#\\1>");
    
    private static final Pattern NICKNAME_PATTERN_NEW = Pattern.compile("^([^\\x00-\\x7F])\\s+([^:]+):\\s(.*)$");
    
    private static final Pattern NICKNAME_PATTERN_OLD = Pattern.compile("^<([^>]+)>\\s*(.*)$");
    
    public static Text formatChatMessage(Text originalMessage) {
        String messageString = originalMessage.getString();
        
        String rank = null;
        String nickname = null;
        String message = null;
        
        Matcher newMatcher = NICKNAME_PATTERN_NEW.matcher(messageString);
        if (newMatcher.matches()) {
            rank = newMatcher.group(1);
            nickname = newMatcher.group(2).trim();
            message = newMatcher.group(3);
            
            if (nickname.isEmpty() || nickname.length() > 16) {
                return originalMessage;
            }
        } else {
            Matcher oldMatcher = NICKNAME_PATTERN_OLD.matcher(messageString);
            if (oldMatcher.matches()) {
                rank = "";
                nickname = oldMatcher.group(1);
                message = oldMatcher.group(2);
            }
        }
        
        if (nickname == null || message == null) {
            return originalMessage;
        }
        
        String format = ConfigManager.getConfig().getChatDisplay();
        
        format = format.replace("{rank}", rank != null ? rank : "");
        format = format.replace("{nickname}", nickname);
        format = format.replace("{message}", message);
        
        Text formattedText = parseFormattedText(format);
        
        return applyMentionHighlight(formattedText, message);
    }
    
    private static Text parseFormattedText(String text) {
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
    
    private static Text applyMentionHighlight(Text formattedText, String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return formattedText;
        }
        
        String playerName = client.player.getName().getString();
        
        if (!message.toLowerCase().contains(playerName.toLowerCase())) {
            return formattedText;
        }
        
        MutableText result = Text.empty();
        String fullText = formattedText.getString();
        
        Pattern mentionPattern = Pattern.compile("(?i)" + Pattern.quote(playerName));
        Matcher matcher = mentionPattern.matcher(fullText);
        
        int lastEnd = 0;
        boolean foundMention = false;
        
        while (matcher.find()) {
            foundMention = true;
            
            if (matcher.start() > lastEnd) {
                String before = fullText.substring(lastEnd, matcher.start());
                result.append(Text.literal(before));
            }
            
            String mentionedName = fullText.substring(matcher.start(), matcher.end());
            result.append(createMentionText(mentionedName));
            
            lastEnd = matcher.end();
        }
        
        if (lastEnd < fullText.length()) {
            String remaining = fullText.substring(lastEnd);
            result.append(Text.literal(remaining));
        }
        
        if (foundMention) {
            playMentionSound();
        }
        
        return foundMention ? result : formattedText;
    }
    
    private static MutableText createMentionText(String text) {
        try {
            String colorHex = ConfigManager.getConfig().getMentionColor().replace("#", "");
            int textColor = Integer.parseInt(colorHex, 16);
            
            MutableText mentionText = Text.literal("@" + text);
            mentionText.setStyle(Style.EMPTY
                .withColor(TextColor.fromRgb(textColor))
                .withBold(true)
                .withUnderline(true)
            );
            
            return mentionText;
        } catch (NumberFormatException e) {
            return Text.literal("@" + text).formatted(Formatting.AQUA, Formatting.BOLD, Formatting.UNDERLINE);
        }
    }
    
    private static void playMentionSound() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        try {
            String soundId = ConfigManager.getConfig().getMentionSound();
            float volume = ConfigManager.getConfig().getMentionVolume();
            float pitch = ConfigManager.getConfig().getMentionPitch();
            
            String[] parts = soundId.split(":");
            if (parts.length != 2) {
                return;
            }
            
            net.minecraft.registry.Registries.SOUND_EVENT.getOrEmpty(
                net.minecraft.util.Identifier.of(parts[0], parts[1])
            ).ifPresent(sound -> {
                client.player.playSound(sound, volume, pitch);
            });
        } catch (Exception e) {
        }
    }
    
    public static String extractPlayerName(Text message) {
        String messageString = message.getString();
        
        Matcher newMatcher = NICKNAME_PATTERN_NEW.matcher(messageString);
        if (newMatcher.matches()) {
            String nickname = newMatcher.group(2).trim();
            if (!nickname.isEmpty() && nickname.length() <= 16) {
                return nickname;
            }
        }
        
        Matcher oldMatcher = NICKNAME_PATTERN_OLD.matcher(messageString);
        if (oldMatcher.matches()) {
            return oldMatcher.group(1);
        }
        
        return null;
    }
}