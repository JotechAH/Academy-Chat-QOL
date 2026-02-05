package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ChatPreview extends ClickableWidget {
    
    private String chatFormat;
    private final float scale;
    
    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 12;
    
    private static final String[] EXAMPLE_MESSAGES = {
        "Hi!",
        "This is a medium length message",
        "This is a much longer message to test how the chat format handles longer text content"
    };
    
    public ChatPreview(int x, int y, int width, int height, String initialFormat, float scale) {
        super(x, y, width, height, Text.empty());
        this.chatFormat = initialFormat != null ? initialFormat : "";
        this.scale = scale;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledHeight = (int)(this.height / scale);
        
        int totalLines = 0;
        for (String message : EXAMPLE_MESSAGES) {
            totalLines += countLines(client, scaledWidth - PADDING * 2, message);
        }
        
        int neededHeight = PADDING + 10 + 8 + (totalLines * LINE_HEIGHT) + (EXAMPLE_MESSAGES.length - 1) * 4 + PADDING;
        int actualHeight = Math.max(scaledHeight, neededHeight);
        
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + actualHeight, 0xFF1A1A1A);
        
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + 1, 0xFF3A3A3A);
        context.fill(scaledX, scaledY + actualHeight - 1, scaledX + scaledWidth, scaledY + actualHeight, 0xFF3A3A3A);
        context.fill(scaledX, scaledY, scaledX + 1, scaledY + actualHeight, 0xFF3A3A3A);
        context.fill(scaledX + scaledWidth - 1, scaledY, scaledX + scaledWidth, scaledY + actualHeight, 0xFF3A3A3A);
        
        context.drawText(client.textRenderer, "Preview:", scaledX + PADDING, scaledY + PADDING, 0xFFAAAAAA, false);
        
        int currentY = scaledY + PADDING + 10 + 8;
        
        for (String message : EXAMPLE_MESSAGES) {
            currentY = drawFormattedMessage(context, client, scaledX + PADDING, currentY, scaledWidth - PADDING * 2, message);
            currentY += 4;
        }
        
        context.getMatrices().pop();
    }
    
    private int countLines(MinecraftClient client, int maxWidth, String message) {
        String formatted = chatFormat
            .replace("{rank}", "一")
            .replace("{nickname}", "Player123")
            .replace("{message}", message);
        
        String cleaned = formatted.replaceAll("<#[0-9A-Fa-f]{6}>|</#[0-9A-Fa-f]{6}>|&[lmnokr]", "");
        String[] words = cleaned.split(" ");
        
        int lines = 1;
        int currentWidth = 0;
        
        for (String word : words) {
            int wordWidth = client.textRenderer.getWidth(word + " ");
            if (currentWidth > 0 && currentWidth + wordWidth > maxWidth) {
                lines++;
                currentWidth = wordWidth;
            } else {
                currentWidth += wordWidth;
            }
        }
        
        return lines;
    }
    
    private int drawFormattedMessage(DrawContext context, MinecraftClient client, int x, int y, int maxWidth, String message) {
        String formatted = chatFormat
            .replace("{rank}", "一")
            .replace("{nickname}", "Player123")
            .replace("{message}", message)
            .replace("&", "§");
        
        return drawParsedTextWithWrap(context, client, x, y, maxWidth, formatted);
    }
    
    private int drawParsedTextWithWrap(DrawContext context, MinecraftClient client, int x, int y, int maxWidth, String text) {
        java.util.regex.Pattern hexPattern = java.util.regex.Pattern.compile("<#([0-9A-Fa-f]{6})>(.*?)</#\\1>");
        java.util.regex.Matcher matcher = hexPattern.matcher(text);
        
        java.util.List<TextSegment> segments = new java.util.ArrayList<>();
        int lastEnd = 0;
        
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                segments.add(new TextSegment(beforeText, -1));
            }
            
            String hexColor = matcher.group(1);
            String coloredText = matcher.group(2);
            
            try {
                int color = Integer.parseInt(hexColor, 16);
                segments.add(new TextSegment(coloredText, color));
            } catch (NumberFormatException e) {
                segments.add(new TextSegment(coloredText, -1));
            }
            
            lastEnd = matcher.end();
        }
        
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            segments.add(new TextSegment(remainingText, -1));
        }
        
        int currentX = x;
        int currentY = y;
        
        for (TextSegment segment : segments) {
            String[] words = segment.text.split(" ");
            
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (i < words.length - 1) word += " ";
                
                Text styledText = parseMinecraftFormatting(word);
                if (segment.color != -1) {
                    net.minecraft.text.MutableText colored = Text.literal("").append(styledText);
                    colored.setStyle(colored.getStyle().withColor(net.minecraft.text.TextColor.fromRgb(segment.color)));
                    styledText = colored;
                }
                
                int wordWidth = client.textRenderer.getWidth(styledText);
                
                if (currentX > x && currentX + wordWidth > x + maxWidth) {
                    currentY += LINE_HEIGHT;
                    currentX = x;
                }
                
                context.drawText(client.textRenderer, styledText, currentX, currentY, 0xFFFFFFFF, false);
                currentX += wordWidth;
            }
        }
        
        return currentY + LINE_HEIGHT;
    }
    
    private static class TextSegment {
        String text;
        int color;
        
        TextSegment(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
    
    private Text parseMinecraftFormatting(String text) {
        net.minecraft.text.MutableText result = Text.literal("");
        StringBuilder current = new StringBuilder();
        
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        
        for (int i = 0; i < text.length(); i++) {
            if (i < text.length() - 1 && text.charAt(i) == '&') {
                if (current.length() > 0) {
                    net.minecraft.text.MutableText part = Text.literal(current.toString());
                    net.minecraft.text.Style style = net.minecraft.text.Style.EMPTY;
                    if (bold) style = style.withBold(true);
                    if (italic) style = style.withItalic(true);
                    if (underline) style = style.withUnderline(true);
                    if (strikethrough) style = style.withStrikethrough(true);
                    if (obfuscated) style = style.withObfuscated(true);
                    part.setStyle(style);
                    result.append(part);
                    current = new StringBuilder();
                }
                
                char code = text.charAt(i + 1);
                switch (code) {
                    case 'l': bold = true; break;
                    case 'o': italic = true; break;
                    case 'n': underline = true; break;
                    case 'm': strikethrough = true; break;
                    case 'k': obfuscated = true; break;
                    case 'r': 
                        bold = false;
                        italic = false;
                        underline = false;
                        strikethrough = false;
                        obfuscated = false;
                        break;
                }
                i++;
            } else {
                current.append(text.charAt(i));
            }
        }
        
        if (current.length() > 0) {
            net.minecraft.text.MutableText part = Text.literal(current.toString());
            net.minecraft.text.Style style = net.minecraft.text.Style.EMPTY;
            if (bold) style = style.withBold(true);
            if (italic) style = style.withItalic(true);
            if (underline) style = style.withUnderline(true);
            if (strikethrough) style = style.withStrikethrough(true);
            if (obfuscated) style = style.withObfuscated(true);
            part.setStyle(style);
            result.append(part);
        }
        
        return result;
    }
    
    public void setChatFormat(String format) {
        this.chatFormat = format != null ? format : "";
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Chat preview"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
