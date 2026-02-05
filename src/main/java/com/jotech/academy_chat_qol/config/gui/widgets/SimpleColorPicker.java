package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;

public class SimpleColorPicker extends ClickableWidget {
    
    private int selectedColor;
    private final Consumer<Integer> onColorChange;
    private final float scale;
    
    private static final int PREVIEW_SIZE = 18;
    private static final int SPACING = 3;
    
    private static final int[] PRESET_COLORS = {
        0xFF0000,
        0xFF7F00,
        0xFFFF00,
        0x00FF00,
        0x00FFFF,
        0x0000FF,
        0x7F00FF,
        0xFF00FF,
        0xFFFFFF,
        0xAAAAAA,
        0x555555,
        0x000000,
        0x5DADE2,
        0x44AA44,
        0xAA4444,
    };
    
    public SimpleColorPicker(int x, int y, int width, int height, int initialColor, float scale, Consumer<Integer> onColorChange) {
        super(x, y, width, height, Text.empty());
        this.selectedColor = initialColor;
        this.scale = scale;
        this.onColorChange = onColorChange;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        
        context.drawText(client.textRenderer, "Mention Color:", 
                        scaledX, scaledY, 0xFFFFFFFF, false);
        
        int startY = scaledY + 12;
        
        int cols = 5;
        int colorIndex = 0;
        
        for (int row = 0; row < 3 && colorIndex < PRESET_COLORS.length; row++) {
            for (int col = 0; col < cols && colorIndex < PRESET_COLORS.length; col++) {
                int color = PRESET_COLORS[colorIndex];
                
                int boxX = scaledX + col * (PREVIEW_SIZE + SPACING);
                int boxY = startY + row * (PREVIEW_SIZE + SPACING);
                
                int scaledMouseX = (int)(mouseX / scale);
                int scaledMouseY = (int)(mouseY / scale);
                boolean hovered = scaledMouseX >= boxX && scaledMouseX < boxX + PREVIEW_SIZE
                               && scaledMouseY >= boxY && scaledMouseY < boxY + PREVIEW_SIZE;
                
                context.fill(boxX, boxY, boxX + PREVIEW_SIZE, boxY + PREVIEW_SIZE, 0xFF000000 | color);
                
                int borderColor = (color == selectedColor) ? 0xFFFFFFFF : (hovered ? 0xFFAAAAAA : 0xFF555555);
                int borderThickness = (color == selectedColor) ? 2 : 1;
                
                context.fill(boxX - borderThickness, boxY - borderThickness, 
                           boxX + PREVIEW_SIZE + borderThickness, boxY, borderColor);
                context.fill(boxX - borderThickness, boxY + PREVIEW_SIZE, 
                           boxX + PREVIEW_SIZE + borderThickness, boxY + PREVIEW_SIZE + borderThickness, borderColor);
                context.fill(boxX - borderThickness, boxY, 
                           boxX, boxY + PREVIEW_SIZE, borderColor);
                context.fill(boxX + PREVIEW_SIZE, boxY, 
                           boxX + PREVIEW_SIZE + borderThickness, boxY + PREVIEW_SIZE, borderColor);
                
                colorIndex++;
            }
        }
        
        int paletteWidth = 5 * (PREVIEW_SIZE + SPACING);
        int previewX = scaledX + paletteWidth + 10;
        int previewY = startY + 10;
        
        String beforeText = "Player123: This is a test mention message for ";
        String playerName = client.player.getName().getString();
        String afterText = " in chat";
        
        int currentX = previewX;
        int currentY = previewY;
        
        String[] lines1 = wrapText(client.textRenderer, beforeText, 150);
        for (String line : lines1) {
            context.drawText(client.textRenderer, line, currentX, currentY, 0xFFFFFFFF, false);
            currentY += 10;
        }
        
        net.minecraft.text.MutableText coloredMention = net.minecraft.text.Text.literal(playerName);
        coloredMention.setStyle(net.minecraft.text.Style.EMPTY
            .withColor(net.minecraft.text.TextColor.fromRgb(selectedColor))
            .withBold(true));
        context.drawText(client.textRenderer, coloredMention, currentX, currentY, 0xFFFFFFFF, false);
        currentY += 10;
        
        context.drawText(client.textRenderer, afterText, currentX, currentY, 0xFFFFFFFF, false);
        
        context.getMatrices().pop();
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
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int startY = scaledY + 12;
        int cols = 5;
        int colorIndex = 0;
        
        for (int row = 0; row < 3 && colorIndex < PRESET_COLORS.length; row++) {
            for (int col = 0; col < cols && colorIndex < PRESET_COLORS.length; col++) {
                int boxX = scaledX + col * (PREVIEW_SIZE + SPACING);
                int boxY = startY + row * (PREVIEW_SIZE + SPACING);
                
                if (scaledMouseX >= boxX && scaledMouseX < boxX + PREVIEW_SIZE
                 && scaledMouseY >= boxY && scaledMouseY < boxY + PREVIEW_SIZE) {
                    this.selectedColor = PRESET_COLORS[colorIndex];
                    if (onColorChange != null) {
                        onColorChange.accept(this.selectedColor);
                    }
                    return;
                }
                
                colorIndex++;
            }
        }
    }
    
    public int getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Color picker: #" + String.format("%06X", selectedColor)));
    }
}
