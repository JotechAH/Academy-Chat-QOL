package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class ModernColorPicker extends ClickableWidget {
    
    private float hue = 0.6f;
    private float saturation = 0.7f;
    private float brightness = 0.9f;
    private final Consumer<Integer> onColorChange;
    private final float scale;
    private final int defaultColor;
    
    private static final int PICKER_SIZE = 100;
    private static final int HUE_BAR_HEIGHT = 12;
    private static final int SPACING = 8;
    private static final int RESET_BUTTON_WIDTH = 50;
    private static final int RESET_BUTTON_HEIGHT = 16;
    
    private boolean draggingPicker = false;
    private boolean draggingHueBar = false;
    
    public ModernColorPicker(int x, int y, int width, int height, int initialColor, float scale, Consumer<Integer> onColorChange) {
        super(x, y, width, height, Text.empty());
        this.scale = scale;
        this.onColorChange = onColorChange;
        this.defaultColor = initialColor;
        
        int r = (initialColor >> 16) & 0xFF;
        int g = (initialColor >> 8) & 0xFF;
        int b = initialColor & 0xFF;
        float[] hsb = rgbToHsb(r, g, b);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        context.drawText(client.textRenderer, "Mention Color:", 
                        scaledX, scaledY, 0xFFFFFFFF, false);
        
        int contentY = scaledY + 12;
        
        drawSaturationBrightnessPicker(context, scaledX, contentY);
        
        int hueBarY = contentY + PICKER_SIZE + SPACING;
        drawHueBar(context, scaledX, hueBarY, PICKER_SIZE);
        
        int resetButtonY = hueBarY + HUE_BAR_HEIGHT + 8;
        drawResetButton(context, client, scaledX, resetButtonY, scaledMouseX, scaledMouseY);
        
        int previewX = scaledX + PICKER_SIZE + 15;
        int previewWidth = scaledWidth - PICKER_SIZE - 20;
        drawPreviewWithChatFormat(context, client, previewX, contentY, previewWidth);
        
        context.getMatrices().pop();
    }
    
    private void drawResetButton(DrawContext context, MinecraftClient client, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + RESET_BUTTON_WIDTH
                       && mouseY >= y && mouseY < y + RESET_BUTTON_HEIGHT;
        
        int bgColor = hovered ? 0xFF4A4A4A : 0xFF3A3A3A;
        context.fill(x, y, x + RESET_BUTTON_WIDTH, y + RESET_BUTTON_HEIGHT, bgColor);
        
        int borderColor = hovered ? 0xFF5DADE2 : 0xFF555555;
        context.fill(x, y, x + RESET_BUTTON_WIDTH, y + 1, borderColor);
        context.fill(x, y + RESET_BUTTON_HEIGHT - 1, x + RESET_BUTTON_WIDTH, y + RESET_BUTTON_HEIGHT, borderColor);
        context.fill(x, y, x + 1, y + RESET_BUTTON_HEIGHT, borderColor);
        context.fill(x + RESET_BUTTON_WIDTH - 1, y, x + RESET_BUTTON_WIDTH, y + RESET_BUTTON_HEIGHT, borderColor);
        
        String text = "Reset";
        int textX = x + (RESET_BUTTON_WIDTH - client.textRenderer.getWidth(text)) / 2;
        int textY = y + (RESET_BUTTON_HEIGHT - client.textRenderer.fontHeight) / 2;
        context.drawText(client.textRenderer, text, textX, textY, 0xFFFFFFFF, false);
    }
    
    private void drawSaturationBrightnessPicker(DrawContext context, int x, int y) {
        for (int px = 0; px < PICKER_SIZE; px++) {
            for (int py = 0; py < PICKER_SIZE; py++) {
                float s = (float) px / PICKER_SIZE;
                float b = 1.0f - ((float) py / PICKER_SIZE);
                
                int color = hsbToRgb(hue, s, b);
                context.fill(x + px, y + py, x + px + 1, y + py + 1, 0xFF000000 | color);
            }
        }
        
        int cursorX = x + (int)(saturation * PICKER_SIZE);
        int cursorY = y + (int)((1.0f - brightness) * PICKER_SIZE);
        
        drawCircle(context, cursorX, cursorY, 5, 0xFFFFFFFF);
        drawCircle(context, cursorX, cursorY, 3, 0xFF000000);
    }
    
    private void drawHueBar(DrawContext context, int x, int y, int width) {
        for (int px = 0; px < width; px++) {
            float h = (float) px / width;
            int color = hsbToRgb(h, 1.0f, 1.0f);
            context.fill(x + px, y, x + px + 1, y + HUE_BAR_HEIGHT, 0xFF000000 | color);
        }
        
        int cursorX = x + (int)(hue * width);
        
        context.fill(cursorX - 1, y - 2, cursorX + 2, y + HUE_BAR_HEIGHT + 2, 0xFFFFFFFF);
        context.fill(cursorX, y - 1, cursorX + 1, y + HUE_BAR_HEIGHT + 1, 0xFF000000);
    }
    
    private void drawPreviewWithChatFormat(DrawContext context, MinecraftClient client, int x, int y, int maxWidth) {
        String chatFormat = com.jotech.academy_chat_qol.config.ConfigManager.getConfig().getChatDisplay();
        
        chatFormat = chatFormat.replace("{rank}", "一");
        chatFormat = chatFormat.replace("{nickname}", "Player123");
        
        String playerName = client.player.getName().getString();
        String message = "This is a mention test message for " + playerName;
        chatFormat = chatFormat.replace("{message}", message);
        
        int currentColor = hsbToRgb(hue, saturation, brightness);
        
        drawFormattedChatPreview(context, client, x, y, maxWidth, chatFormat, currentColor);
    }
    
    private void drawFormattedChatPreview(DrawContext context, MinecraftClient client, int x, int y, int maxWidth, String text, int mentionColor) {
        java.util.regex.Pattern hexPattern = java.util.regex.Pattern.compile("<#([0-9A-Fa-f]{6})>(.*?)</#\\1>");
        java.util.regex.Matcher matcher = hexPattern.matcher(text);
        
        int currentX = x;
        int currentY = y;
        int lastEnd = 0;
        
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                if (!beforeText.isEmpty()) {
                    String playerName = client.player.getName().getString();
                    if (beforeText.contains(playerName)) {
                        currentX = drawTextWithMention(context, client, beforeText, playerName, mentionColor, currentX, currentY, maxWidth);
                    } else {
                        if (currentX - x + client.textRenderer.getWidth(beforeText) > maxWidth) {
                            currentY += 10;
                            currentX = x;
                        }
                        context.drawText(client.textRenderer, beforeText, currentX, currentY, 0xFFFFFFFF, false);
                        currentX += client.textRenderer.getWidth(beforeText);
                    }
                }
            }
            
            String hexColor = matcher.group(1);
            String coloredText = matcher.group(2);
            
            try {
                int color = Integer.parseInt(hexColor, 16);
                
                String playerName = client.player.getName().getString();
                if (coloredText.contains(playerName)) {
                    currentX = drawTextWithMention(context, client, coloredText, playerName, mentionColor, currentX, currentY, maxWidth);
                } else {
                    if (currentX - x + client.textRenderer.getWidth(coloredText) > maxWidth) {
                        currentY += 10;
                        currentX = x;
                    }
                    net.minecraft.text.MutableText coloredPart = Text.literal(coloredText);
                    coloredPart.setStyle(net.minecraft.text.Style.EMPTY.withColor(net.minecraft.text.TextColor.fromRgb(color)));
                    context.drawText(client.textRenderer, coloredPart, currentX, currentY, 0xFFFFFFFF, false);
                    currentX += client.textRenderer.getWidth(coloredText);
                }
            } catch (NumberFormatException e) {
                context.drawText(client.textRenderer, coloredText, currentX, currentY, 0xFFFFFFFF, false);
                currentX += client.textRenderer.getWidth(coloredText);
            }
            
            lastEnd = matcher.end();
        }
        
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            if (!remainingText.isEmpty()) {
                String playerName = client.player.getName().getString();
                if (remainingText.contains(playerName)) {
                    drawTextWithMention(context, client, remainingText, playerName, mentionColor, currentX, currentY, maxWidth);
                } else {
                    if (currentX - x + client.textRenderer.getWidth(remainingText) > maxWidth) {
                        currentY += 10;
                        currentX = x;
                    }
                    context.drawText(client.textRenderer, remainingText, currentX, currentY, 0xFFFFFFFF, false);
                }
            }
        }
    }
    
    private int drawTextWithMention(DrawContext context, MinecraftClient client, String text, String playerName, int mentionColor, int startX, int y, int maxWidth) {
        int idx = text.indexOf(playerName);
        if (idx == -1) {
            context.drawText(client.textRenderer, text, startX, y, 0xFFFFFFFF, false);
            return startX + client.textRenderer.getWidth(text);
        }
        
        String before = text.substring(0, idx);
        String after = text.substring(idx + playerName.length());
        
        int currentX = startX;
        
        if (!before.isEmpty()) {
            context.drawText(client.textRenderer, before, currentX, y, 0xFFFFFFFF, false);
            currentX += client.textRenderer.getWidth(before);
        }
        
        net.minecraft.text.MutableText coloredMention = Text.literal(playerName);
        coloredMention.setStyle(net.minecraft.text.Style.EMPTY
            .withColor(net.minecraft.text.TextColor.fromRgb(mentionColor))
            .withBold(true));
        context.drawText(client.textRenderer, coloredMention, currentX, y, 0xFFFFFFFF, false);
        currentX += client.textRenderer.getWidth(playerName);
        
        if (!after.isEmpty()) {
            context.drawText(client.textRenderer, after, currentX, y, 0xFFFFFFFF, false);
            currentX += client.textRenderer.getWidth(after);
        }
        
        return currentX;
    }
    
    private void drawCircle(DrawContext context, int centerX, int centerY, int radius, int color) {
        for (int px = -radius; px <= radius; px++) {
            for (int py = -radius; py <= radius; py++) {
                if (px * px + py * py <= radius * radius) {
                    context.fill(centerX + px, centerY + py, centerX + px + 1, centerY + py + 1, color);
                }
            }
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int contentY = scaledY + 12;
        int hueBarY = contentY + PICKER_SIZE + SPACING;
        int resetButtonY = hueBarY + HUE_BAR_HEIGHT + 8;
        
        if (scaledMouseX >= scaledX && scaledMouseX < scaledX + PICKER_SIZE
        && scaledMouseY >= contentY && scaledMouseY < contentY + PICKER_SIZE) {
            draggingPicker = true;
            updatePickerValue(scaledMouseX, scaledMouseY, scaledX, contentY);
            playClickSound();
            return;
        }
        
        int resetHitboxTop = resetButtonY - 4;
        int resetHitboxBottom = resetButtonY + RESET_BUTTON_HEIGHT + 4;
        
        if (scaledMouseX >= scaledX && scaledMouseX < scaledX + RESET_BUTTON_WIDTH
            && scaledMouseY >= resetHitboxTop && scaledMouseY < resetHitboxBottom) {
            resetToDefault();
            playClickSound();
            return;
        }
        
        int hueBarHitboxTop = hueBarY - 4;
        int hueBarHitboxBottom = Math.min(hueBarY + HUE_BAR_HEIGHT + 4, resetHitboxTop);
        
        if (scaledMouseX >= scaledX && scaledMouseX < scaledX + PICKER_SIZE
        && scaledMouseY >= hueBarHitboxTop && scaledMouseY < hueBarHitboxBottom) {
            draggingHueBar = true;
            updateHueValue(scaledMouseX, scaledX);
            playClickSound();
            return;
        }
    }
    
    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(
            net.minecraft.client.sound.PositionedSoundInstance.master(
                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }
    
    private void resetToDefault() {
        int r = (defaultColor >> 16) & 0xFF;
        int g = (defaultColor >> 8) & 0xFF;
        int b = defaultColor & 0xFF;
        float[] hsb = rgbToHsb(r, g, b);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        
        if (onColorChange != null) {
            onColorChange.accept(defaultColor);
        }
    }
    
    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int contentY = scaledY + 12;
        
        if (draggingPicker) {
            updatePickerValue(scaledMouseX, scaledMouseY, scaledX, contentY);
        } else if (draggingHueBar) {
            updateHueValue(scaledMouseX, scaledX);
        }
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
        draggingPicker = false;
        draggingHueBar = false;
    }
    
    private void updatePickerValue(int mouseX, int mouseY, int pickerX, int pickerY) {
        saturation = Math.max(0.0f, Math.min(1.0f, (float)(mouseX - pickerX) / PICKER_SIZE));
        brightness = Math.max(0.0f, Math.min(1.0f, 1.0f - (float)(mouseY - pickerY) / PICKER_SIZE));
        
        if (onColorChange != null) {
            int rgb = hsbToRgb(hue, saturation, brightness);
            onColorChange.accept(rgb);
        }
    }
    
    private void updateHueValue(int mouseX, int barX) {
        hue = Math.max(0.0f, Math.min(1.0f, (float)(mouseX - barX) / PICKER_SIZE));
        
        if (onColorChange != null) {
            int rgb = hsbToRgb(hue, saturation, brightness);
            onColorChange.accept(rgb);
        }
    }
    
    private float[] rgbToHsb(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;
        
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;
        
        float h = 0;
        if (delta != 0) {
            if (max == rf) {
                h = ((gf - bf) / delta) % 6;
            } else if (max == gf) {
                h = ((bf - rf) / delta) + 2;
            } else {
                h = ((rf - gf) / delta) + 4;
            }
            h /= 6;
            if (h < 0) h += 1;
        }
        
        float s = max == 0 ? 0 : delta / max;
        float v = max;
        
        return new float[]{h, s, v};
    }
    
    private int hsbToRgb(float h, float s, float b) {
        int rgb = java.awt.Color.HSBtoRGB(h, s, b);
        return rgb & 0xFFFFFF;
    }
    
    private String[] wrapText(net.minecraft.client.font.TextRenderer renderer, String text, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (renderer.getWidth(testLine) <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    public int getSelectedColor() {
        return hsbToRgb(hue, saturation, brightness);
    }
    
    public void setSelectedColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        float[] hsb = rgbToHsb(r, g, b);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Color picker"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
