package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class SimpleButton extends ClickableWidget {
    
    private final String label;
    private final Runnable onClick;
    private final float scale;
    
    public SimpleButton(int x, int y, int width, int height, String label, float scale, Runnable onClick) {
        super(x, y, width, height, Text.literal(label));
        this.label = label;
        this.scale = scale;
        this.onClick = onClick;
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
        
        boolean hovered = this.isHovered();
        
        int bgColor = hovered ? 0xFF4A4A4A : 0xFF3A3A3A;
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, bgColor);
        
        int borderColor = hovered ? 0xFF5DADE2 : 0xFF555555;
        context.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + 1, borderColor);
        context.fill(scaledX, scaledY + scaledHeight - 1, scaledX + scaledWidth, scaledY + scaledHeight, borderColor);
        context.fill(scaledX, scaledY, scaledX + 1, scaledY + scaledHeight, borderColor);
        context.fill(scaledX + scaledWidth - 1, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, borderColor);
        
        int textX = scaledX + (scaledWidth - client.textRenderer.getWidth(label)) / 2;
        int textY = scaledY + (scaledHeight - client.textRenderer.fontHeight) / 2;
        context.drawText(client.textRenderer, label, textX, textY, 0xFFFFFFFF, false);
        
        context.getMatrices().pop();
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onClick != null) {
            onClick.run();
            playClickSound();
        }
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
                   Text.literal(label));
    }
}
