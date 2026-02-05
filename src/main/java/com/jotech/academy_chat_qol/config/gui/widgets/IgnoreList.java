package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreList extends ClickableWidget {
    
    private final List<String> ignoredPlayers;
    private final java.util.function.Consumer<String> onRemove;
    private final float scale;
    
    private int scrollOffset = 0;
    private String hoveredPlayer = null;
    
    private static final int PADDING = 8;
    private static final int ENTRY_HEIGHT = 24;
    private static final int COLUMNS = 4;
    private static final int SPACING = 8;
    
    public IgnoreList(int x, int y, int width, int height, List<String> ignoredPlayers, 
                     float scale, java.util.function.Consumer<String> onRemove) {
        super(x, y, width, height, Text.empty());
        this.ignoredPlayers = new ArrayList<>(ignoredPlayers);
        Collections.sort(this.ignoredPlayers, String.CASE_INSENSITIVE_ORDER);
        this.scale = scale;
        this.onRemove = onRemove;
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
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, 0xFF0F0F0F);
        
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + 1, 0xFF3A3A3A);
        context.fill(scaledX, scaledY + scaledHeight - 1, scaledX + scaledWidth, scaledY + scaledHeight, 0xFF3A3A3A);
        context.fill(scaledX, scaledY, scaledX + 1, scaledY + scaledHeight, 0xFF3A3A3A);
        context.fill(scaledX + scaledWidth - 1, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, 0xFF3A3A3A);
        
        if (ignoredPlayers.isEmpty()) {
            String emptyMessage1 = "Ignore list is empty";
            String emptyMessage2 = "Enter a nickname above to add someone";
            
            int msg1Width = client.textRenderer.getWidth(emptyMessage1);
            int msg2Width = client.textRenderer.getWidth(emptyMessage2);
            
            int msg1X = scaledX + (scaledWidth - msg1Width) / 2;
            int msg2X = scaledX + (scaledWidth - msg2Width) / 2;
            int msgY = scaledY + (scaledHeight - 20) / 2;
            
            context.drawText(client.textRenderer, emptyMessage1, msg1X, msgY, 0xFF666666, false);
            context.drawText(client.textRenderer, emptyMessage2, msg2X, msgY + 12, 0xFF666666, false);
            
            context.getMatrices().pop();
            return;
        }
        
        int columnWidth = (scaledWidth - PADDING * 2 - SPACING * (COLUMNS - 1)) / COLUMNS;
        
        hoveredPlayer = null;
        
        for (int i = 0; i < ignoredPlayers.size(); i++) {
            String player = ignoredPlayers.get(i);
            
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            
            int entryX = scaledX + PADDING + col * (columnWidth + SPACING);
            int entryY = scaledY + PADDING + row * (ENTRY_HEIGHT + SPACING) - scrollOffset;
            
            if (entryY + ENTRY_HEIGHT < scaledY + PADDING) continue;
            if (entryY > scaledY + scaledHeight - PADDING) break;
            
            boolean hovered = scaledMouseX >= entryX && scaledMouseX < entryX + columnWidth
                           && scaledMouseY >= entryY && scaledMouseY < entryY + ENTRY_HEIGHT;
            
            int bgColor = hovered ? 0xFF2A2A2A : 0xFF1A1A1A;
            context.fill(entryX, entryY, entryX + columnWidth, entryY + ENTRY_HEIGHT, bgColor);
            
            int borderColor = hovered ? 0xFF5DADE2 : 0xFF3A3A3A;
            context.fill(entryX, entryY, entryX + columnWidth, entryY + 1, borderColor);
            context.fill(entryX, entryY + ENTRY_HEIGHT - 1, entryX + columnWidth, entryY + ENTRY_HEIGHT, borderColor);
            context.fill(entryX, entryY, entryX + 1, entryY + ENTRY_HEIGHT, borderColor);
            context.fill(entryX + columnWidth - 1, entryY, entryX + columnWidth, entryY + ENTRY_HEIGHT, borderColor);
            
            if (hovered) {
                hoveredPlayer = player;
            }
            
            String displayName = player;
            int textWidth = client.textRenderer.getWidth(displayName);
            int maxWidth = hovered ? columnWidth - 30 : columnWidth - 10;
            
            if (textWidth > maxWidth) {
                while (textWidth > maxWidth && displayName.length() > 0) {
                    displayName = displayName.substring(0, displayName.length() - 1);
                    textWidth = client.textRenderer.getWidth(displayName + "...");
                }
                displayName = displayName + "...";
            }
            
            int textX = entryX + (columnWidth - client.textRenderer.getWidth(displayName)) / 2;
            if (hovered) textX = entryX + 5;
            
            context.drawText(client.textRenderer, displayName, textX, entryY + 8, 0xFFE0E0E0, false);
            
            if (hovered) {
                int btnX = entryX + columnWidth - 20;
                int btnY = entryY + 4;
                
                context.fill(btnX, btnY, btnX + 16, btnY + 16, 0xFFFF0000);
                context.drawText(client.textRenderer, "-", btnX + 5, btnY + 4, 0xFFFFFFFF, false);
            }
        }
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (hoveredPlayer != null) {
            onRemove.accept(hoveredPlayer);
            playClickSound();
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int totalRows = (int) Math.ceil((double) ignoredPlayers.size() / COLUMNS);
        int maxScroll = Math.max(0, totalRows * (ENTRY_HEIGHT + SPACING) - ((int)(this.height / scale) - PADDING * 2));
        
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 10));
        return true;
    }
    
    public void updateList(List<String> newList) {
        this.ignoredPlayers.clear();
        this.ignoredPlayers.addAll(newList);
        Collections.sort(this.ignoredPlayers, String.CASE_INSENSITIVE_ORDER);
    }
    
    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(
            net.minecraft.client.sound.PositionedSoundInstance.master(
                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Ignored players list"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
