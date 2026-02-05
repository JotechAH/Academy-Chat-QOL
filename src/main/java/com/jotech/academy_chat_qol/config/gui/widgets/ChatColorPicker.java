package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class ChatColorPicker extends ClickableWidget {
    
    private float hue = 0.6f;
    private float saturation = 0.7f;
    private float brightness = 0.9f;
    private final Consumer<String> onColorChange;
    private final float scale;
    
    private static final int PICKER_SIZE = 100;
    private static final int HUE_BAR_HEIGHT = 12;
    private static final int SPACING = 8;
    
    private boolean draggingPicker = false;
    private boolean draggingHueBar = false;
    
    public ChatColorPicker(int x, int y, int width, int height, String initialColor, float scale, Consumer<String> onColorChange) {
        super(x, y, width, height, Text.empty());
        this.scale = scale;
        this.onColorChange = onColorChange;
        
        if (initialColor != null && initialColor.startsWith("#") && initialColor.length() == 7) {
            try {
                int color = Integer.parseInt(initialColor.substring(1), 16);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                float[] hsb = rgbToHsb(r, g, b);
                this.hue = hsb[0];
                this.saturation = hsb[1];
                this.brightness = hsb[2];
            } catch (Exception e) {
            }
        }
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        
        context.drawText(client.textRenderer, "Text Color:", 
                        scaledX, scaledY, 0xFFFFFFFF, false);
        
        int contentY = scaledY + 12;
        
        drawSaturationBrightnessPicker(context, scaledX, contentY);
        
        int hueBarY = contentY + PICKER_SIZE + SPACING;
        drawHueBar(context, scaledX, hueBarY, PICKER_SIZE);
        
        context.getMatrices().pop();
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
        
        if (scaledMouseX >= scaledX && scaledMouseX < scaledX + PICKER_SIZE
         && scaledMouseY >= contentY && scaledMouseY < contentY + PICKER_SIZE) {
            draggingPicker = true;
            updatePickerValue(scaledMouseX, scaledMouseY, scaledX, contentY);
            playClickSound();
            return;
        }
        
        int hueBarHitboxTop = hueBarY - 4;
        int hueBarHitboxBottom = hueBarY + HUE_BAR_HEIGHT + 4;
        
        if (scaledMouseX >= scaledX && scaledMouseX < scaledX + PICKER_SIZE
         && scaledMouseY >= hueBarHitboxTop && scaledMouseY < hueBarHitboxBottom) {
            draggingHueBar = true;
            updateHueValue(scaledMouseX, scaledX);
            playClickSound();
            return;
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
        notifyChange();
    }
    
    private void updateHueValue(int mouseX, int barX) {
        hue = Math.max(0.0f, Math.min(1.0f, (float)(mouseX - barX) / PICKER_SIZE));
        notifyChange();
    }
    
    private void notifyChange() {
        if (onColorChange != null) {
            String hexCode = String.format("%06X", hsbToRgb(hue, saturation, brightness));
            onColorChange.accept(hexCode);
        }
    }
    
    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(
            net.minecraft.client.sound.PositionedSoundInstance.master(
                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
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
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Chat color picker"));
    }
    
    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
}
