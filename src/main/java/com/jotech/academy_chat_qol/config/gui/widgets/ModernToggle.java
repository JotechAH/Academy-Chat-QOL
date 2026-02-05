package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class ModernToggle extends ClickableWidget {
    
    private boolean enabled;
    private final Consumer<Boolean> onToggle;
    private final String label;
    private final float scale;
    
    private static final int TOGGLE_WIDTH = 36;
    private static final int TOGGLE_HEIGHT = 18;
    private static final int TOGGLE_PADDING = 2;
    private static final int KNOB_SIZE = 14;
    
    private static final int COLOR_BG_OFF = 0xFF3A3A3A;
    private static final int COLOR_BG_ON = 0xFF44AA44;
    private static final int COLOR_BG_HOVER_OFF = 0xFF4A4A4A;
    private static final int COLOR_BG_HOVER_ON = 0xFF55BB55;
    private static final int COLOR_KNOB = 0xFFFFFFFF;
    
    public ModernToggle(int x, int y, int width, String label, boolean initialValue, float scale, Consumer<Boolean> onToggle) {
        super(x, y, width, 20, Text.literal(label));
        this.label = label;
        this.enabled = initialValue;
        this.scale = scale;
        this.onToggle = onToggle;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        
        int toggleX = scaledX + scaledWidth - TOGGLE_WIDTH;
        int toggleY = scaledY + (20 - TOGGLE_HEIGHT) / 2;
        
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        boolean hovered = scaledMouseX >= toggleX && scaledMouseX < toggleX + TOGGLE_WIDTH
                       && scaledMouseY >= toggleY && scaledMouseY < toggleY + TOGGLE_HEIGHT;
        
        int labelColor = 0xFFFFFFFF;
        context.drawText(client.textRenderer, label, 
                        scaledX, scaledY + (20 - 8) / 2, 
                        labelColor, false);
        
        int bgColor;
        if (enabled) {
            bgColor = hovered ? COLOR_BG_HOVER_ON : COLOR_BG_ON;
        } else {
            bgColor = hovered ? COLOR_BG_HOVER_OFF : COLOR_BG_OFF;
        }
        
        context.fill(toggleX, toggleY, toggleX + TOGGLE_WIDTH, toggleY + TOGGLE_HEIGHT, bgColor);
        
        int knobX;
        if (enabled) {
            knobX = toggleX + TOGGLE_WIDTH - TOGGLE_PADDING - KNOB_SIZE;
        } else {
            knobX = toggleX + TOGGLE_PADDING;
        }
        int knobY = toggleY + TOGGLE_PADDING;
        
        context.fill(knobX, knobY, knobX + KNOB_SIZE, knobY + KNOB_SIZE, COLOR_KNOB);
        
        context.fill(knobX, knobY, knobX + KNOB_SIZE, knobY + 1, 0xFF000000);
        context.fill(knobX, knobY + KNOB_SIZE - 1, knobX + KNOB_SIZE, knobY + KNOB_SIZE, 0xFF000000);
        context.fill(knobX, knobY, knobX + 1, knobY + KNOB_SIZE, 0xFF000000);
        context.fill(knobX + KNOB_SIZE - 1, knobY, knobX + KNOB_SIZE, knobY + KNOB_SIZE, 0xFF000000);
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        int scaledX = (int)(this.getX() / scale);
        int scaledY = (int)(this.getY() / scale);
        int scaledWidth = (int)(this.width / scale);
        int scaledMouseX = (int)(mouseX / scale);
        int scaledMouseY = (int)(mouseY / scale);
        
        int toggleX = scaledX + scaledWidth - TOGGLE_WIDTH;
        int toggleY = scaledY + (20 - TOGGLE_HEIGHT) / 2;
        
        if (scaledMouseX >= toggleX && scaledMouseX < toggleX + TOGGLE_WIDTH
         && scaledMouseY >= toggleY && scaledMouseY < toggleY + TOGGLE_HEIGHT) {
            this.enabled = !this.enabled;
            if (onToggle != null) {
                onToggle.accept(this.enabled);
            }
            MinecraftClient.getInstance().getSoundManager().play(
                net.minecraft.client.sound.PositionedSoundInstance.master(
                    net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
                )
            );
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void playDownSound(net.minecraft.client.sound.SoundManager soundManager) {
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
                   Text.literal(label + ": " + (enabled ? "ON" : "OFF")));
    }
}
