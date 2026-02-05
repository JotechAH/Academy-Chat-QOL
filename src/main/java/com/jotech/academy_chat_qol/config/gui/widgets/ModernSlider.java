package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ModernSlider extends ClickableWidget {
    
    private float value;
    private final Consumer<Float> onValueChange;
    private final float scale;
    private boolean dragging = false;
    
    private static final int TRACK_HEIGHT = 4;
    private static final int HANDLE_WIDTH = 12;
    private static final int HANDLE_HEIGHT = 16;
    
    private static final int COLOR_TRACK_BG = 0xFF3A3A3A;
    private static final int COLOR_TRACK_FILLED = 0xFF5DADE2;
    private static final int COLOR_HANDLE = 0xFFFFFFFF;
    private static final int COLOR_HANDLE_HOVER = 0xFF5DADE2;
    
    public ModernSlider(int x, int y, int width, int height, float initialValue, float scale, Consumer<Float> onValueChange) {
        super(x, y, width, height, Text.empty());
        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        this.scale = scale;
        this.onValueChange = onValueChange;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledHeight = (int)(this.height / scale);
        
        int trackY = scaledY + (scaledHeight - TRACK_HEIGHT) / 2;
        
        context.fill(scaledX, trackY, scaledX + scaledWidth, trackY + TRACK_HEIGHT, COLOR_TRACK_BG);
        
        int filledWidth = (int) (scaledWidth * value);
        if (filledWidth > 0) {
            context.fill(scaledX, trackY, scaledX + filledWidth, trackY + TRACK_HEIGHT, COLOR_TRACK_FILLED);
        }
        
        int handleX = scaledX + (int) (scaledWidth * value) - HANDLE_WIDTH / 2;
        int handleY = scaledY + (scaledHeight - HANDLE_HEIGHT) / 2;
        
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        boolean handleHovered = scaledMouseX >= handleX && scaledMouseX < handleX + HANDLE_WIDTH
                             && scaledMouseY >= handleY && scaledMouseY < handleY + HANDLE_HEIGHT;
        
        int handleColor = (handleHovered || dragging) ? COLOR_HANDLE_HOVER : COLOR_HANDLE;
        context.fill(handleX, handleY, handleX + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, handleColor);
        
        context.fill(handleX, handleY, handleX + HANDLE_WIDTH, handleY + 1, 0xFF000000);
        context.fill(handleX, handleY + HANDLE_HEIGHT - 1, handleX + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, 0xFF000000);
        context.fill(handleX, handleY, handleX + 1, handleY + HANDLE_HEIGHT, 0xFF000000);
        context.fill(handleX + HANDLE_WIDTH - 1, handleY, handleX + HANDLE_WIDTH, handleY + HANDLE_HEIGHT, 0xFF000000);
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        updateValue(mouseX);
        dragging = true;
    }
    
    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (dragging) {
            updateValue(mouseX);
        }
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = false;
    }
    
    private void updateValue(double mouseX) {
        int scaledX = (int)(this.getX() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledMouseX = (int)(mouseX / scale);
        
        float newValue = (float) ((scaledMouseX - scaledX) / (double)scaledWidth);
        newValue = Math.max(0.0f, Math.min(1.0f, newValue));
        
        if (newValue != this.value) {
            this.value = newValue;
            if (onValueChange != null) {
                onValueChange.accept(this.value);
            }
        }
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = Math.max(0.0f, Math.min(1.0f, value));
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal("Slider: " + (int)(value * 100) + "%"));
    }
}
