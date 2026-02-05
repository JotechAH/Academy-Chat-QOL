package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class IgnoreTextField extends ClickableWidget {
    
    private String text = "";
    private final Consumer<String> onTextChange;
    private final Runnable onAddRemove;
    private final java.util.function.Predicate<String> isIgnored;
    private final float scale;
    
    private int cursorPosition = 0;
    private boolean focused = false;
    private int tickCounter = 0;
    
    private static final int PADDING = 5;
    private static final int BUTTON_WIDTH = 20;
    private static final int CURSOR_BLINK_SPEED = 36;
    
    public IgnoreTextField(int x, int y, int width, int height, float scale, 
                          Consumer<String> onTextChange, 
                          java.util.function.Predicate<String> isIgnored,
                          Runnable onAddRemove) {
        super(x, y, width, height, Text.empty());
        this.scale = scale;
        this.onTextChange = onTextChange;
        this.isIgnored = isIgnored;
        this.onAddRemove = onAddRemove;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        tickCounter++;
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledHeight = (int)(this.height / scale);
        
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, 0xFF1A1A1A);
        
        int borderColor = focused ? 0xFF5DADE2 : 0xFF3A3A3A;
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + 1, borderColor);
        context.fill(scaledX, scaledY + scaledHeight - 1, scaledX + scaledWidth, scaledY + scaledHeight, borderColor);
        context.fill(scaledX, scaledY, scaledX + 1, scaledY + scaledHeight, borderColor);
        context.fill(scaledX + scaledWidth - 1, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, borderColor);
        
        int textX = scaledX + PADDING;
        int textY = scaledY + (scaledHeight - client.textRenderer.fontHeight) / 2;
        
        String placeholder = "Enter player name...";
        if (text.isEmpty() && !focused) {
            context.drawText(client.textRenderer, placeholder, textX, textY, 0xFF666666, false);
        } else {
            context.drawText(client.textRenderer, text, textX, textY, 0xFFE0E0E0, false);
        }
        
        if (focused && (tickCounter / CURSOR_BLINK_SPEED) % 2 == 0) {
            String beforeCursor = text.substring(0, cursorPosition);
            int cursorX = textX + client.textRenderer.getWidth(beforeCursor);
            context.fill(cursorX, textY - 1, cursorX + 1, textY + client.textRenderer.fontHeight + 1, 0xFFFFFFFF);
        }
        
        if (!text.isEmpty()) {
            int btnX = scaledX + scaledWidth - BUTTON_WIDTH - 3;
            int btnY = scaledY + (scaledHeight - 16) / 2;
            
            boolean btnHovered = mouseX >= btnX * scale && mouseX < (btnX + BUTTON_WIDTH) * scale 
                              && mouseY >= btnY * scale && mouseY < (btnY + 16) * scale;
            
            boolean ignored = isIgnored.test(text);
            int btnColor = ignored ? (btnHovered ? 0xFFFF5555 : 0xFFFF0000) : (btnHovered ? 0xFF55FF55 : 0xFF00FF00);
            
            context.fill(btnX, btnY, btnX + BUTTON_WIDTH, btnY + 16, btnColor);
            
            String btnText = ignored ? "-" : "+";
            int btnTextX = btnX + (BUTTON_WIDTH - client.textRenderer.getWidth(btnText)) / 2;
            int btnTextY = btnY + (16 - client.textRenderer.fontHeight) / 2;
            context.drawText(client.textRenderer, btnText, btnTextX, btnTextY, 0xFFFFFFFF, false);
        }
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledHeight = (int)(this.height / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        if (!text.isEmpty()) {
            int btnX = scaledX + scaledWidth - BUTTON_WIDTH - 3;
            int btnY = scaledY + (scaledHeight - 16) / 2;
            
            if (scaledMouseX >= btnX && scaledMouseX < btnX + BUTTON_WIDTH 
             && scaledMouseY >= btnY && scaledMouseY < btnY + 16) {
                onAddRemove.run();
                playClickSound();
                return;
            }
        }
        
        focused = true;
        
        MinecraftClient client = MinecraftClient.getInstance();
        int clickX = scaledMouseX - scaledX - PADDING;
        
        int width = 0;
        cursorPosition = text.length();
        for (int i = 0; i <= text.length(); i++) {
            if (i < text.length()) {
                int charWidth = client.textRenderer.getWidth(String.valueOf(text.charAt(i)));
                if (width + charWidth / 2 > clickX) {
                    cursorPosition = i;
                    break;
                }
                width += charWidth;
            }
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;
        
        if (keyCode == 257 || keyCode == 335) {
            if (!text.isEmpty()) {
                onAddRemove.run();
                return true;
            }
        }
        
        if (keyCode == 259 && cursorPosition > 0) {
            text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
            cursorPosition--;
            notifyChange();
            return true;
        }
        
        if (keyCode == 261 && cursorPosition < text.length()) {
            text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            notifyChange();
            return true;
        }
        
        if (keyCode == 263 && cursorPosition > 0) {
            cursorPosition--;
            return true;
        }
        
        if (keyCode == 262 && cursorPosition < text.length()) {
            cursorPosition++;
            return true;
        }
        
        if (keyCode == 268) {
            cursorPosition = 0;
            return true;
        }
        
        if (keyCode == 269) {
            cursorPosition = text.length();
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        
        if (text.length() >= 16) return false;
        
        if (!isValidChar(chr)) return false;
        
        text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
        cursorPosition++;
        notifyChange();
        return true;
    }
    
    private boolean isValidChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }
    
    private void notifyChange() {
        if (onTextChange != null) {
            onTextChange.accept(text);
        }
    }
    
    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = this.text.length();
    }
    
    public String getText() {
        return text;
    }
    
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    public boolean isFocused() {
        return focused;
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
                   Text.literal("Ignore player field"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
