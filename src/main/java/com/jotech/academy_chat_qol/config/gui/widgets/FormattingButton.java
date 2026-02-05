package com.jotech.academy_chat_qol.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.Style;

public class FormattingButton extends ClickableWidget {
    
    private final String styleCode;
    private final Runnable onClick;
    private final float scale;
    
    public FormattingButton(int x, int y, int width, int height, String styleCode, float scale, Runnable onClick) {
        super(x, y, width, height, Text.empty());
        this.styleCode = styleCode;
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
        
        String displayText = getDisplayText();
        Text styledText = Text.literal(displayText).setStyle(getStyle());
        
        int textX = scaledX + (scaledWidth - client.textRenderer.getWidth(styledText)) / 2;
        int textY = scaledY + (scaledHeight - client.textRenderer.fontHeight) / 2;
        context.drawText(client.textRenderer, styledText, textX, textY, 0xFFFFFFFF, false);
        
        context.getMatrices().pop();
    }
    
    private String getDisplayText() {
        switch (styleCode) {
            case "&l": return "Bold";
            case "&o": return "Italic";
            case "&n": return "Under";
            case "&m": return "Strike";
            case "&k": return "?????";
            case "&r": return "Reset";
            default: return "?";
        }
    }
    
    private Style getStyle() {
        Style style = Style.EMPTY;
        switch (styleCode) {
            case "&l": return style.withBold(true);
            case "&o": return style.withItalic(true);
            case "&n": return style.withUnderline(true);
            case "&m": return style.withStrikethrough(true);
            case "&k": return style.withObfuscated(true);
            default: return style;
        }
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
                   Text.literal(getDisplayText()));
    }
}
