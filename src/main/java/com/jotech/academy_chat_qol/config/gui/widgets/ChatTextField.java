package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class ChatTextField extends ClickableWidget {
    
    private String text;
    private final Consumer<String> onTextChange;
    private final float scale;
    
    private int cursorPosition = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean focused = false;
    private int scrollOffset = 0;
    
    private static final int PADDING = 5;
    private static final int CURSOR_BLINK_SPEED = 12;
    private int tickCounter = 0;
    private boolean dragging = false;
    
    public ChatTextField(int x, int y, int width, int height, String initialText, float scale, Consumer<String> onTextChange) {
        super(x, y, width, height, Text.empty());
        this.text = initialText != null ? initialText : "";
        this.scale = scale;
        this.onTextChange = onTextChange;
        this.cursorPosition = text.length();
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
        int textAreaWidth = scaledWidth - PADDING * 2;
        
        if (focused) {
            String beforeCursor = text.substring(0, cursorPosition);
            int cursorXPos = client.textRenderer.getWidth(beforeCursor);
            
            if (cursorXPos - scrollOffset > textAreaWidth) {
                scrollOffset = cursorXPos - textAreaWidth + 10;
            }
            if (cursorXPos - scrollOffset < 0) {
                scrollOffset = Math.max(0, cursorXPos - 10);
            }
        }
        
        int clipLeft = scaledX + PADDING;
        int clipRight = scaledX + scaledWidth - PADDING;
        
        if (hasSelection()) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            
            String beforeSel = text.substring(0, start);
            String selected = text.substring(start, end);
            
            int selStartX = textX + client.textRenderer.getWidth(beforeSel) - scrollOffset;
            int selWidth = client.textRenderer.getWidth(selected);
            
            int visibleSelLeft = Math.max(selStartX, clipLeft);
            int visibleSelRight = Math.min(selStartX + selWidth, clipRight);
            
            if (visibleSelRight > visibleSelLeft) {
                context.fill(visibleSelLeft, textY - 1, visibleSelRight, textY + client.textRenderer.fontHeight + 1, 0x803399FF);
            }
        }
        
        int currentX = textX - scrollOffset;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charWidth = client.textRenderer.getWidth(charStr);
            
            if (currentX + charWidth > clipLeft && currentX < clipRight) {
                context.drawText(client.textRenderer, charStr, currentX, textY, 0xFFE0E0E0, false);
            }
            
            currentX += charWidth;
        }
        
        if (focused && (tickCounter / CURSOR_BLINK_SPEED) % 2 == 0) {
            String beforeCursor = text.substring(0, cursorPosition);
            int cursorX = textX + client.textRenderer.getWidth(beforeCursor) - scrollOffset;
            
            if (cursorX >= clipLeft && cursorX < clipRight) {
                context.fill(cursorX, textY - 1, cursorX + 1, textY + client.textRenderer.fontHeight + 1, 0xFFFFFFFF);
            }
        }
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        focused = true;
        
        MinecraftClient client = MinecraftClient.getInstance();
        int scaledX = (int)(this.getX() / scale);
        int scaledMouseX = (int)(mouseX / scale);
        
        int clickX = scaledMouseX - scaledX - PADDING + scrollOffset;
        
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
        
        selectionStart = cursorPosition;
        selectionEnd = cursorPosition;
        dragging = true;
    }
    
    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (!dragging) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        int scaledX = (int)(this.getX() / scale);
        int scaledMouseX = (int)(mouseX / scale);
        
        int clickX = scaledMouseX - scaledX - PADDING + scrollOffset;
        
        int width = 0;
        int newPos = text.length();
        for (int i = 0; i <= text.length(); i++) {
            if (i < text.length()) {
                int charWidth = client.textRenderer.getWidth(String.valueOf(text.charAt(i)));
                if (width + charWidth / 2 > clickX) {
                    newPos = i;
                    break;
                }
                width += charWidth;
            }
        }
        
        selectionEnd = newPos;
        cursorPosition = newPos;
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
        if (selectionStart == selectionEnd) {
            selectionStart = -1;
            selectionEnd = -1;
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;
        
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (keyCode == 65 && (modifiers & 2) != 0) {
            selectionStart = 0;
            selectionEnd = text.length();
            cursorPosition = text.length();
            return true;
        }
        
        if (keyCode == 67 && (modifiers & 2) != 0) {
            if (hasSelection()) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                client.keyboard.setClipboard(text.substring(start, end));
            }
            return true;
        }
        
        if (keyCode == 86 && (modifiers & 2) != 0) {
            insertText(client.keyboard.getClipboard());
            return true;
        }
        
        if (keyCode == 88 && (modifiers & 2) != 0) {
            if (hasSelection()) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                client.keyboard.setClipboard(text.substring(start, end));
                deleteSelection();
            }
            return true;
        }
        
        if (keyCode == 259) {
            if (hasSelection()) {
                deleteSelection();
            } else if (cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
                notifyChange();
            }
            return true;
        }
        
        if (keyCode == 261) {
            if (hasSelection()) {
                deleteSelection();
            } else if (cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                notifyChange();
            }
            return true;
        }
        
        if (keyCode == 263) {
            if ((modifiers & 1) != 0) {
                if (selectionStart == -1) selectionStart = cursorPosition;
                if (cursorPosition > 0) cursorPosition--;
                selectionEnd = cursorPosition;
            } else {
                if (cursorPosition > 0) cursorPosition--;
                selectionStart = -1;
                selectionEnd = -1;
            }
            return true;
        }
        
        if (keyCode == 262) {
            if ((modifiers & 1) != 0) {
                if (selectionStart == -1) selectionStart = cursorPosition;
                if (cursorPosition < text.length()) cursorPosition++;
                selectionEnd = cursorPosition;
            } else {
                if (cursorPosition < text.length()) cursorPosition++;
                selectionStart = -1;
                selectionEnd = -1;
            }
            return true;
        }
        
        if (keyCode == 268) {
            cursorPosition = 0;
            selectionStart = -1;
            selectionEnd = -1;
            return true;
        }
        
        if (keyCode == 269) {
            cursorPosition = text.length();
            selectionStart = -1;
            selectionEnd = -1;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        insertText(String.valueOf(chr));
        return true;
    }
    
    private void insertText(String toInsert) {
        if (hasSelection()) deleteSelection();
        
        text = text.substring(0, cursorPosition) + toInsert + text.substring(cursorPosition);
        cursorPosition += toInsert.length();
        notifyChange();
    }
    
    private void deleteSelection() {
        if (!hasSelection()) return;
        
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);
        
        text = text.substring(0, start) + text.substring(end);
        cursorPosition = start;
        selectionStart = -1;
        selectionEnd = -1;
        notifyChange();
    }
    
    private boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd;
    }
    
    private void notifyChange() {
        if (onTextChange != null) {
            onTextChange.accept(text);
        }
    }
    
    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = this.text.length();
        this.selectionStart = -1;
        this.selectionEnd = -1;
        notifyChange();
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
    
    public void wrapSelection(String prefix, String suffix) {
        if (hasSelection()) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            
            String before = text.substring(0, start);
            String selected = text.substring(start, end);
            String after = text.substring(end);
            
            text = before + prefix + selected + suffix + after;
            cursorPosition = start + prefix.length() + selected.length();
            selectionStart = start + prefix.length();
            selectionEnd = cursorPosition;
            notifyChange();
        } else {
            text = text.substring(0, cursorPosition) + prefix + text.substring(cursorPosition);
            cursorPosition += prefix.length();
            notifyChange();
        }
    }
    
    public void applyColorToSelection(String hexColor) {
        String openTag = "<#" + hexColor + ">";
        String closeTag = "</#" + hexColor + ">";
        
        if (hasSelection()) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            
            String before = text.substring(0, start);
            String selected = text.substring(start, end);
            String after = text.substring(end);
            
            if (start >= 9 && before.length() >= 9 && after.length() >= 10) {
                String possibleOpen = before.substring(before.length() - 9);
                String possibleClose = after.substring(0, 10);
                
                if (possibleOpen.matches("<#[0-9A-Fa-f]{6}>") && possibleClose.matches("</#[0-9A-Fa-f]{6}>")) {
                    before = before.substring(0, before.length() - 9);
                    after = after.substring(10);
                }
            }
            
            text = before + openTag + selected + closeTag + after;
            int newStart = before.length() + openTag.length();
            cursorPosition = newStart + selected.length();
            selectionStart = newStart;
            selectionEnd = cursorPosition;
        } else {
            text = text.substring(0, cursorPosition) + openTag + closeTag + text.substring(cursorPosition);
            cursorPosition += openTag.length();
        }
        
        notifyChange();
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Chat format text field"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
