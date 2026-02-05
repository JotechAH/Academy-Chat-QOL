package com.jotech.academy_chat_qol.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

public class ConfirmationOverlay {
    
    private boolean visible = false;
    private Runnable onConfirm;
    private Runnable onCancel;
    private List<String> changesList;
    
    private static final int OVERLAY_WIDTH = 400;
    private static final int HEADER_HEIGHT = 35;
    private static final int BUTTON_HEIGHT = 30;
    private static final int PADDING = 15;
    
    public void show(List<String> changes, Runnable onConfirm, Runnable onCancel) {
        this.changesList = changes;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.visible = true;
    }
    
    public void hide() {
        this.visible = false;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void render(DrawContext context, int screenWidth, int screenHeight, int mouseX, int mouseY, float scale) {
        if (!visible) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.fill(0, 0, screenWidth, screenHeight, 0x99000000);
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int contentHeight = HEADER_HEIGHT + PADDING + 12 + (changesList.size() * 15) + 20 + BUTTON_HEIGHT + PADDING;
        int overlayHeight = Math.max(200, contentHeight);
        
        int overlayX = ((int)(screenWidth / scale) - OVERLAY_WIDTH) / 2;
        int overlayY = ((int)(screenHeight / scale) - overlayHeight) / 2;
        
        context.fill(overlayX, overlayY, overlayX + OVERLAY_WIDTH, overlayY + overlayHeight, 0xFF2A2A2A);
        
        context.fill(overlayX, overlayY, overlayX + OVERLAY_WIDTH, overlayY + HEADER_HEIGHT, 0xFF1A1A1A);
        
        context.fill(overlayX, overlayY, overlayX + OVERLAY_WIDTH, overlayY + 2, 0xFF5DADE2);
        context.fill(overlayX, overlayY + overlayHeight - 2, overlayX + OVERLAY_WIDTH, overlayY + overlayHeight, 0xFF5DADE2);
        context.fill(overlayX, overlayY, overlayX + 2, overlayY + overlayHeight, 0xFF5DADE2);
        context.fill(overlayX + OVERLAY_WIDTH - 2, overlayY, overlayX + OVERLAY_WIDTH, overlayY + overlayHeight, 0xFF5DADE2);
        
        String title = "Unsaved Changes";
        int titleWidth = client.textRenderer.getWidth(title);
        context.drawText(client.textRenderer, title, overlayX + (OVERLAY_WIDTH - titleWidth) / 2, overlayY + 12, 0xFFFFFFFF, false);
        
        int currentY = overlayY + HEADER_HEIGHT + PADDING;
        
        String message = "You have unsaved changes. Close without saving?";
        int msgWidth = client.textRenderer.getWidth(message);
        context.drawText(client.textRenderer, message, overlayX + (OVERLAY_WIDTH - msgWidth) / 2, currentY, 0xFFE0E0E0, false);
        
        currentY += 20;
        
        for (String change : changesList) {
            int changeWidth = client.textRenderer.getWidth(change);
            context.drawText(client.textRenderer, change, overlayX + (OVERLAY_WIDTH - changeWidth) / 2, currentY, 0xFFFFD700, false);
            currentY += 15;
        }
        
        currentY += 20;
        
        int buttonWidth = 100;
        int buttonSpacing = 20;
        int discardX = overlayX + (OVERLAY_WIDTH / 2) - buttonWidth - (buttonSpacing / 2);
        int goBackX = overlayX + (OVERLAY_WIDTH / 2) + (buttonSpacing / 2);
        
        boolean discardHovered = scaledMouseX >= discardX && scaledMouseX < discardX + buttonWidth
                              && scaledMouseY >= currentY && scaledMouseY < currentY + BUTTON_HEIGHT;
        boolean goBackHovered = scaledMouseX >= goBackX && scaledMouseX < goBackX + buttonWidth
                             && scaledMouseY >= currentY && scaledMouseY < currentY + BUTTON_HEIGHT;
        
        int discardBg = discardHovered ? 0xFFFF5555 : 0xFFFF0000;
        context.fill(discardX, currentY, discardX + buttonWidth, currentY + BUTTON_HEIGHT, discardBg);
        context.fill(discardX, currentY, discardX + buttonWidth, currentY + 1, 0xFFFF8888);
        context.fill(discardX, currentY + BUTTON_HEIGHT - 1, discardX + buttonWidth, currentY + BUTTON_HEIGHT, 0xFFAA0000);
        context.fill(discardX, currentY, discardX + 1, currentY + BUTTON_HEIGHT, 0xFFFF8888);
        context.fill(discardX + buttonWidth - 1, currentY, discardX + buttonWidth, currentY + BUTTON_HEIGHT, 0xFFAA0000);
        
        String discardText = "Discard";
        int discardTextWidth = client.textRenderer.getWidth(discardText);
        context.drawText(client.textRenderer, discardText, discardX + (buttonWidth - discardTextWidth) / 2, currentY + 10, 0xFFFFFFFF, false);
        
        int goBackBg = goBackHovered ? 0xFF4A4A4A : 0xFF3A3A3A;
        context.fill(goBackX, currentY, goBackX + buttonWidth, currentY + BUTTON_HEIGHT, goBackBg);
        context.fill(goBackX, currentY, goBackX + buttonWidth, currentY + 1, 0xFF5DADE2);
        context.fill(goBackX, currentY + BUTTON_HEIGHT - 1, goBackX + buttonWidth, currentY + BUTTON_HEIGHT, 0xFF2A2A2A);
        context.fill(goBackX, currentY, goBackX + 1, currentY + BUTTON_HEIGHT, 0xFF5DADE2);
        context.fill(goBackX + buttonWidth - 1, currentY, goBackX + buttonWidth, currentY + BUTTON_HEIGHT, 0xFF2A2A2A);
        
        String goBackText = "Go Back";
        int goBackTextWidth = client.textRenderer.getWidth(goBackText);
        context.drawText(client.textRenderer, goBackText, goBackX + (buttonWidth - goBackTextWidth) / 2, currentY + 10, 0xFFFFFFFF, false);
        
        context.getMatrices().pop();
    }
    
    public boolean handleClick(int screenWidth, int screenHeight, int mouseX, int mouseY, float scale) {
        if (!visible) return false;
        
        MinecraftClient client = MinecraftClient.getInstance();
        
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int contentHeight = HEADER_HEIGHT + PADDING + 12 + (changesList.size() * 15) + 20 + BUTTON_HEIGHT + PADDING;
        int overlayHeight = Math.max(200, contentHeight);
        
        int overlayX = ((int)(screenWidth / scale) - OVERLAY_WIDTH) / 2;
        int overlayY = ((int)(screenHeight / scale) - overlayHeight) / 2;
        
        int buttonY = overlayY + HEADER_HEIGHT + PADDING + 12 + (changesList.size() * 15) + 20;
        
        int buttonWidth = 100;
        int buttonSpacing = 20;
        int discardX = overlayX + (OVERLAY_WIDTH / 2) - buttonWidth - (buttonSpacing / 2);
        int goBackX = overlayX + (OVERLAY_WIDTH / 2) + (buttonSpacing / 2);
        
        if (scaledMouseX >= discardX && scaledMouseX < discardX + buttonWidth
         && scaledMouseY >= buttonY && scaledMouseY < buttonY + BUTTON_HEIGHT) {
            playClickSound();
            hide();
            if (onConfirm != null) onConfirm.run();
            return true;
        }
        
        if (scaledMouseX >= goBackX && scaledMouseX < goBackX + buttonWidth
         && scaledMouseY >= buttonY && scaledMouseY < buttonY + BUTTON_HEIGHT) {
            playClickSound();
            hide();
            if (onCancel != null) onCancel.run();
            return true;
        }
        
        return true;
    }
    
    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(
            net.minecraft.client.sound.PositionedSoundInstance.master(
                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }
}
