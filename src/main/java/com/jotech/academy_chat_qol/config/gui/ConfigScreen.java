package com.jotech.academy_chat_qol.config.gui;

import com.jotech.academy_chat_qol.config.ConfigManager;
import com.jotech.academy_chat_qol.config.ModConfig;
import com.jotech.academy_chat_qol.config.UpdateChecker;
import com.jotech.academy_chat_qol.config.gui.widgets.ModernSlider;
import com.jotech.academy_chat_qol.config.gui.widgets.ModernToggle;
import com.jotech.academy_chat_qol.config.gui.widgets.ModernColorPicker;
import com.jotech.academy_chat_qol.config.gui.widgets.ChatTextField;
import com.jotech.academy_chat_qol.config.gui.widgets.ChatPreview;
import com.jotech.academy_chat_qol.config.gui.widgets.SimpleButton;
import com.jotech.academy_chat_qol.config.gui.widgets.ChatColorPicker;
import com.jotech.academy_chat_qol.config.gui.widgets.FormattingButton;
import com.jotech.academy_chat_qol.config.gui.widgets.IgnoreTextField;
import com.jotech.academy_chat_qol.config.gui.widgets.IgnoreList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends Screen {
    
    private static final int BASE_WIDTH = 580;
    private static final int BASE_HEIGHT = 348;
    
    private static final int BACKGROUND_DARK = 0xFF1A1A1A;
    private static final int BACKGROUND_LIGHT = 0xFF2A2A2A;
    
    private static final int COLOR_TEXT = 0xFFFFFF;
    private static final int COLOR_TAB_ACTIVE = 0x3A3A3A;
    private static final int COLOR_TAB_INACTIVE = 0x2A2A2A;
    private static final int COLOR_TAB_HOVER = 0x4A4A4A;
    private static final int COLOR_UPDATE_AVAILABLE = 0xFFAA00;
    
    private ConfigTab currentTab = ConfigTab.GENERAL;
    private final Screen parent;
    private boolean isFrench;
    private TempConfigValues tempValues;
    private ConfirmationOverlay confirmationOverlay = new ConfirmationOverlay();
    
    private int scaledWidth;
    private int scaledHeight;
    private int menuX;
    private int menuY;
    private float scale;
    
    private static final int TAB_HEIGHT = 24;
    private static final int HEADER_HEIGHT = 50;
    private static final int FOOTER_HEIGHT = 25;
    
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_MARGIN = 10;
    
    private boolean saveButtonHovered = false;
    private boolean cancelButtonHovered = false;
    
    public ConfigScreen(Screen parent) {
        super(Text.literal("Academy Chat QOL - Config"));
        this.parent = parent;
        this.isFrench = MinecraftClient.getInstance().getLanguageManager().getLanguage().startsWith("fr_");
    }
    
    @Override
    protected void init() {
        super.init();
        
        if (tempValues == null) {
            tempValues = new TempConfigValues();
            ModConfig config = ConfigManager.getConfig();
            tempValues.setMentionVolume(config.getMentionVolume());
            
            try {
                String hex = config.getMentionColor().replace("#", "");
                int color = Integer.parseInt(hex, 16);
                tempValues.setMentionColor(color);
            } catch (Exception e) {
                tempValues.setMentionColor(0x5DADE2);
            }
            
            tempValues.setChatDisplay(config.getChatDisplay());
            
            tempValues.setIgnoredPlayers(com.jotech.academy_chat_qol.chat.IgnoreManager.getIgnoredPlayers());
            
        }
        
        calculateScaling();
        
        initCurrentTab();
    }
    
    private void calculateScaling() {
        float scaleX = (float) this.width / 1920f;
        float scaleY = (float) this.height / 1080f;
        
        this.scale = Math.min(scaleX, scaleY);
        
        this.scale = Math.max(0.5f, Math.min(2.0f, this.scale));
        
        this.scaledWidth = (int) (BASE_WIDTH * this.scale);
        this.scaledHeight = (int) (BASE_HEIGHT * this.scale);
        
        this.menuX = (this.width - this.scaledWidth) / 2;
        this.menuY = (this.height - this.scaledHeight) / 2;
    }
    
    private void initCurrentTab() {
        this.clearChildren();
        
        switch (currentTab) {
            case GENERAL:
                initGeneralTab();
                break;
            case CUSTOMIZATION:
                initCustomizationTab();
                break;
            case IGNORE:
                initIgnoreTab();
                break;
        }
    }
    
    private void initGeneralTab() {
        int contentX = menuX + (int)(15 * scale);
        int contentY = menuY + (int)((HEADER_HEIGHT + 10) * scale);
        int contentWidth = (int)((BASE_WIDTH - 30) * scale);
        
        int currentY = contentY;
        
        ModernSlider volumeSlider = new ModernSlider(
            contentX, 
            currentY + (int)(12 * scale),
            contentWidth - (int)(50 * scale),
            (int)(16 * scale),
            tempValues.getMentionVolume(),
            scale,
            (value) -> {
                tempValues.setMentionVolume(value);
            }
        );
        this.addDrawableChild(volumeSlider);
        currentY += (int)(35 * scale);
        
        currentY += (int)(5 * scale);
        
        ModernColorPicker colorPicker = new ModernColorPicker(
            contentX,
            currentY,
            contentWidth,
            (int)(160 * scale),
            tempValues.getMentionColor(),
            scale,
            (color) -> {
                tempValues.setMentionColor(color);
            }
        );
        this.addDrawableChild(colorPicker);
        currentY += (int)(165 * scale);
        
        currentY += (int)(5 * scale);
        
        List<BooleanField> booleanFields = getBooleanFields();
        
        int cols = 3;
        int toggleWidth = (contentWidth - (int)(20 * scale)) / cols;
        int toggleHeight = (int)(22 * scale);
        int row = 0;
        int col = 0;
        
        for (BooleanField field : booleanFields) {
            int toggleX = contentX + col * toggleWidth;
            int toggleY = currentY + row * toggleHeight;
            
            boolean currentValue = tempValues.getBooleanField(field.field.getName(), field.value);
            
            ModernToggle toggle = new ModernToggle(
                toggleX,
                toggleY,
                toggleWidth - (int)(10 * scale),
                field.displayName,
                currentValue,
                scale,
                (enabled) -> {
                    tempValues.setBooleanField(field.field.getName(), enabled);
                }
            );
            this.addDrawableChild(toggle);
            
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
    }
    
    private List<BooleanField> getBooleanFields() {
        List<BooleanField> fields = new ArrayList<>();
        ModConfig config = ConfigManager.getConfig();
        
        try {
            for (Field field : ModConfig.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) 
                 || Modifier.isFinal(field.getModifiers())
                 || field.getType() != boolean.class) {
                    continue;
                }
                
                field.setAccessible(true);
                boolean value = field.getBoolean(config);
                
                String displayName = formatFieldName(field.getName());
                
                fields.add(new BooleanField(field, displayName, value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return fields;
    }
    
    private String formatFieldName(String fieldName) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            
            if (i == 0) {
                result.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                result.append(' ').append(c);
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private static class BooleanField {
        final Field field;
        final String displayName;
        final boolean value;
        
        BooleanField(Field field, String displayName, boolean value) {
            this.field = field;
            this.displayName = displayName;
            this.value = value;
        }
    }
    
    private ChatTextField chatTextField;
    private ChatPreview chatPreview;
    
    private void initCustomizationTab() {
        int contentX = menuX + (int)(15 * scale);
        int contentY = menuY + (int)((HEADER_HEIGHT + 10) * scale);
        int contentWidth = (int)((BASE_WIDTH - 30) * scale);
        
        int leftColumnWidth = (int)(120 * scale);
        int rightColumnX = contentX + leftColumnWidth + (int)(10 * scale);
        int rightColumnWidth = contentWidth - leftColumnWidth - (int)(10 * scale);
        
        int currentY = contentY;
        
        ChatColorPicker colorPicker = new ChatColorPicker(
            contentX,
            currentY,
            leftColumnWidth,
            (int)(140 * scale),
            "#FFFFFF",
            scale,
            (hexColor) -> {
                if (chatTextField != null && chatTextField.isFocused()) {
                    chatTextField.applyColorToSelection(hexColor);
                }
            }
        );
        this.addDrawableChild(colorPicker);
        
        int formattingY = currentY + (int)(145 * scale);
        int buttonSize = (int)(35 * scale);
        int buttonSpacing = (int)(5 * scale);
        
        String[] formattingLabels = {"&l", "&o", "&n", "&m", "&k", "&r"};
        
        for (int i = 0; i < formattingLabels.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int btnX = contentX + col * (buttonSize + buttonSpacing);
            int btnY = formattingY + row * (buttonSize + buttonSpacing);
            
            final String code = formattingLabels[i];
            
            FormattingButton formattingBtn = new FormattingButton(
                btnX, btnY, buttonSize, buttonSize,
                code,
                scale,
                () -> {
                    if (chatTextField != null) {
                        if (code.equals("&r")) {
                            chatTextField.wrapSelection("&r", "");
                        } else {
                            chatTextField.wrapSelection(code, "&r");
                        }
                    }
                }
            );
            this.addDrawableChild(formattingBtn);
        }
        
        int defaultButtonY = formattingY + 2 * (buttonSize + buttonSpacing) + (int)(8 * scale);
        int defaultButtonWidth = 3 * buttonSize + 2 * buttonSpacing;
        SimpleButton defaultButton = new SimpleButton(
            contentX,
            defaultButtonY,
            defaultButtonWidth,
            (int)(20 * scale),
            "Default",
            scale,
            () -> {
                String defaultFormat = new ModConfig().getChatDisplay().replace("§", "&");
                if (chatTextField != null) {
                    chatTextField.setText(defaultFormat);
                }
                if (chatPreview != null) {
                    chatPreview.setChatFormat(defaultFormat);
                }
                tempValues.setChatDisplay(defaultFormat);
            }
        );
        this.addDrawableChild(defaultButton);
        
        int placeholderY = currentY;
        int placeholderWidth = (int)(rightColumnWidth / 3 - 5);
        
        String[] placeholders = {"{rank}", "{nickname}", "{message}"};
        for (int i = 0; i < placeholders.length; i++) {
            final String placeholder = placeholders[i];
            SimpleButton placeholderBtn = new SimpleButton(
                rightColumnX + i * (placeholderWidth + (int)(5 * scale)),
                placeholderY,
                placeholderWidth,
                (int)(20 * scale),
                placeholder,
                scale,
                () -> {
                    if (chatTextField != null) {
                        chatTextField.wrapSelection(placeholder, "");
                    }
                }
            );
            this.addDrawableChild(placeholderBtn);
        }
        
        int textFieldY = placeholderY + (int)(25 * scale);
        String initialChatDisplay = tempValues.getChatDisplay() != null 
            ? tempValues.getChatDisplay() 
            : ConfigManager.getConfig().getChatDisplay();
        initialChatDisplay = initialChatDisplay.replace("§", "&");
        
        chatTextField = new ChatTextField(
            rightColumnX,
            textFieldY,
            rightColumnWidth,
            (int)(40 * scale),
            initialChatDisplay,
            scale,
            (newText) -> {
                tempValues.setChatDisplay(newText);
                if (chatPreview != null) {
                    chatPreview.setChatFormat(newText);
                }
            }
        );
        this.addDrawableChild(chatTextField);
        
        int previewY = textFieldY + (int)(45 * scale);
        int previewHeight = (int)(70 * scale);
        
        chatPreview = new ChatPreview(
            rightColumnX,
            previewY,
            rightColumnWidth,
            previewHeight,
            tempValues.getChatDisplay() != null ? tempValues.getChatDisplay() : ConfigManager.getConfig().getChatDisplay(),
            scale
        );
        this.addDrawableChild(chatPreview);
    }
    
    private IgnoreTextField ignoreTextField;
    private IgnoreList ignoreList;
    
    private void initIgnoreTab() {
        int contentX = menuX + (int)(15 * scale);
        int contentY = menuY + (int)((HEADER_HEIGHT + 10) * scale);
        int contentWidth = (int)((BASE_WIDTH - 30) * scale);
        
        int currentY = contentY;
        
        MinecraftClient client = MinecraftClient.getInstance();
        String labelText = "Case Sensitive (Player123 ≠ player123)";
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
            contentX,
            currentY,
            contentWidth,
            (int)(10 * scale),
            Text.literal(labelText),
            client.textRenderer
        ) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.getMatrices().push();
                context.getMatrices().scale(scale, scale, 1.0f);
                
                int scaledX = (int)(this.getX() / scale);
                int scaledY = (int)(this.getY() / scale);
                
                context.drawText(client.textRenderer, labelText, scaledX, scaledY, 0xFFAAAAAA, false);
                
                context.getMatrices().pop();
            }
        });
        
        currentY += (int)(12 * scale);
        
        ignoreTextField = new IgnoreTextField(
            contentX,
            currentY,
            contentWidth,
            (int)(25 * scale),
            scale,
            (text) -> {},
            (playerName) -> tempValues.getIgnoredPlayers().contains(playerName),
            () -> {
                String playerName = ignoreTextField.getText().trim();
                if (playerName.isEmpty()) return;
                
                if (tempValues.getIgnoredPlayers().contains(playerName)) {
                    tempValues.removeIgnoredPlayer(playerName);
                } else {
                    tempValues.addIgnoredPlayer(playerName);
                }
                
                ignoreTextField.setText("");
                if (ignoreList != null) {
                    ignoreList.updateList(tempValues.getIgnoredPlayers());
                }
            }
        );
        this.addDrawableChild(ignoreTextField);
        
        int listY = currentY + (int)(35 * scale);
        int listHeight = (int)((BASE_HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT - 62) * scale);
        
        ignoreList = new IgnoreList(
            contentX,
            listY,
            contentWidth,
            listHeight,
            new java.util.ArrayList<>(tempValues.getIgnoredPlayers()),
            scale,
            (playerName) -> {
                tempValues.removeIgnoredPlayer(playerName);
                ignoreList.updateList(tempValues.getIgnoredPlayers());
            }
        );
        this.addDrawableChild(ignoreList);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xCC000000);
        
        context.getMatrices().push();
        context.getMatrices().translate(menuX, menuY, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        drawMenu(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
        
        super.render(context, mouseX, mouseY, delta);
        
        confirmationOverlay.render(context, this.width, this.height, mouseX, mouseY, scale);
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }
    
    private void drawMenu(DrawContext context, int mouseX, int mouseY, float delta) {
        int scaledMouseX = (int) ((mouseX - menuX) / scale);
        int scaledMouseY = (int) ((mouseY - menuY) / scale);
        
        drawTexturedRect(context, 0, 0, BASE_WIDTH, HEADER_HEIGHT, BACKGROUND_DARK);
        
        drawTexturedRect(context, 0, HEADER_HEIGHT, BASE_WIDTH, 
                        BASE_HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT, BACKGROUND_LIGHT);
        
        drawTexturedRect(context, 0, BASE_HEIGHT - FOOTER_HEIGHT, 
                        BASE_WIDTH, FOOTER_HEIGHT, BACKGROUND_DARK);
        
        drawTabs(context, scaledMouseX, scaledMouseY);
        
        drawTitle(context);
        
        if (UpdateChecker.isUpdateAvailable()) {
            drawUpdateIndicator(context, scaledMouseX, scaledMouseY);
        }
        
        drawFooterButtons(context, scaledMouseX, scaledMouseY);
        
        drawTabContent(context, scaledMouseX, scaledMouseY, delta);
    }
    
    private void drawTexturedRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }
    
    private void drawTabs(DrawContext context, int mouseX, int mouseY) {
        int tabWidth = BASE_WIDTH / 3;
        int tabY = 24;
        
        for (int i = 0; i < ConfigTab.values().length; i++) {
            ConfigTab tab = ConfigTab.values()[i];
            int tabX = i * tabWidth;
            
            boolean hovered = mouseX >= tabX && mouseX < tabX + tabWidth 
                           && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT;
            
            int tabColor = tab == currentTab ? COLOR_TAB_ACTIVE : 
                          hovered ? COLOR_TAB_HOVER : COLOR_TAB_INACTIVE;
            
            context.fill(tabX, tabY, tabX + tabWidth - 2, tabY + TAB_HEIGHT, tabColor);
            
            String tabName = tab.getName(isFrench);
            int textWidth = textRenderer.getWidth(tabName);
            int textX = tabX + (tabWidth - textWidth) / 2;
            int textY = tabY + (TAB_HEIGHT - textRenderer.fontHeight) / 2;
            
            int textColor = tab == currentTab ? 0xFFFFFF : 0xAAAAAA;
            context.drawText(textRenderer, tabName, textX, textY, textColor, false);
            
            if (tab == currentTab) {
                int barWidth = textWidth - 10;
                int barHeight = 2;
                int barX = tabX + (tabWidth - barWidth) / 2;
                int barY = tabY + TAB_HEIGHT - 1;
                
                int barColor = 0xFF5DADE2;
                context.fill(barX, barY, barX + barWidth, barY + barHeight, barColor);
            }
        }
    }
    
    private void drawTitle(DrawContext context) {
        String title = "Academy Chat QOL - Config";
        int titleX = (BASE_WIDTH - textRenderer.getWidth(title)) / 2;
        int titleY = 8;
        context.drawText(textRenderer, title, titleX, titleY, COLOR_TEXT, true);
    }
    
    private void drawUpdateIndicator(DrawContext context, int mouseX, int mouseY) {
        String updateText = "Update Available";
        int textWidth = textRenderer.getWidth(updateText);
        int padding = 5;
        
        int boxWidth = textWidth + padding * 2;
        int boxHeight = 14;
        int boxX = BASE_WIDTH - boxWidth - 5;
        int boxY = 3;
        
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xAA000000);
        
        boolean hovered = mouseX >= boxX && mouseX < boxX + boxWidth 
                       && mouseY >= boxY && mouseY < boxY + boxHeight;
        
        context.fill(boxX, boxY, boxX + boxWidth, boxY + 1, COLOR_UPDATE_AVAILABLE);
        context.fill(boxX, boxY + boxHeight - 1, boxX + boxWidth, boxY + boxHeight, COLOR_UPDATE_AVAILABLE);
        context.fill(boxX, boxY, boxX + 1, boxY + boxHeight, COLOR_UPDATE_AVAILABLE);
        context.fill(boxX + boxWidth - 1, boxY, boxX + boxWidth, boxY + boxHeight, COLOR_UPDATE_AVAILABLE);
        
        context.drawText(textRenderer, updateText, boxX + padding, boxY + 3, 
                        hovered ? 0xFFFFFF : COLOR_UPDATE_AVAILABLE, false);
    }
    
    private void drawTabContent(DrawContext context, int mouseX, int mouseY, float delta) {
        switch (currentTab) {
            case GENERAL:
                drawGeneralTabContent(context, mouseX, mouseY);
                break;
            case CUSTOMIZATION:
                break;
            case IGNORE:
                break;
        }
    }
    
    private void drawGeneralTabContent(DrawContext context, int mouseX, int mouseY) {
        int contentX = 15;
        int contentY = HEADER_HEIGHT + 10;
        
        context.drawText(textRenderer, "Mention Volume:", 
                        contentX, contentY, 0xFFFFFFFF, false);
        
        int percentage = (int)(tempValues.getMentionVolume() * 100);
        String percentText = percentage + "%";
        int percentX = BASE_WIDTH - 35;
        context.drawText(textRenderer, percentText, 
                        percentX, contentY + 15, 0xFFAAAAAA, false);
    }
    
    private void drawFooterButtons(DrawContext context, int mouseX, int mouseY) {
        int footerY = BASE_HEIGHT - FOOTER_HEIGHT;
        
        int cancelX = BUTTON_MARGIN;
        int cancelY = footerY + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2;
        
        cancelButtonHovered = mouseX >= cancelX && mouseX < cancelX + BUTTON_WIDTH
                           && mouseY >= cancelY && mouseY < cancelY + BUTTON_HEIGHT;
        
        drawButton(context, cancelX, cancelY, BUTTON_WIDTH, BUTTON_HEIGHT, 
                  isFrench ? "Annuler" : "Cancel", cancelButtonHovered, 0xFFAA4444);
        
        int saveX = BASE_WIDTH - BUTTON_WIDTH - BUTTON_MARGIN;
        int saveY = footerY + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2;
        
        saveButtonHovered = mouseX >= saveX && mouseX < saveX + BUTTON_WIDTH
                         && mouseY >= saveY && mouseY < saveY + BUTTON_HEIGHT;
        
        drawButton(context, saveX, saveY, BUTTON_WIDTH, BUTTON_HEIGHT,
                  isFrench ? "Sauvegarder" : "Save", saveButtonHovered, 0xFF44AA44);
    }
    
    private void drawButton(DrawContext context, int x, int y, int width, int height, 
                           String text, boolean hovered, int accentColor) {
        int bgColor;
        if (hovered) {
            int r = (accentColor >> 16) & 0xFF;
            int g = (accentColor >> 8) & 0xFF;
            int b = accentColor & 0xFF;
            bgColor = 0xFF000000 | ((r / 3) << 16) | ((g / 3) << 8) | (b / 3);
        } else {
            bgColor = 0xFF2A2A2A;
        }
        
        context.fill(x, y, x + width, y + height, bgColor);
        
        int borderThickness = hovered ? 2 : 1;
        int borderColor = hovered ? accentColor : 0xFF555555;
        
        context.fill(x, y, x + width, y + borderThickness, borderColor);
        context.fill(x, y + height - borderThickness, x + width, y + height, borderColor);
        context.fill(x, y, x + borderThickness, y + height, borderColor);
        context.fill(x + width - borderThickness, y, x + width, y + height, borderColor);
        
        int textX = x + (width - textRenderer.getWidth(text)) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;
        int textColor = hovered ? 0xFFFFFF : 0xCCCCCC;
        context.drawText(textRenderer, text, textX, textY, textColor, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        
        if (confirmationOverlay.isVisible()) {
            return confirmationOverlay.handleClick(this.width, this.height, (int)mouseX, (int)mouseY, scale);
        }
        
        int scaledMouseX = (int) ((mouseX - menuX) / scale);
        int scaledMouseY = (int) ((mouseY - menuY) / scale);
        
        if (handleTabClick(scaledMouseX, scaledMouseY)) {
            return true;
        }
        
        if (handleUpdateClick(scaledMouseX, scaledMouseY)) {
            return true;
        }
        
        if (handleButtonClick(scaledMouseX, scaledMouseY)) {
            return true;
        }
        
        if (chatTextField != null && chatTextField.isFocused()) {
            boolean clickedInside = false;
            for (var widget : this.children()) {
                if (widget == chatTextField && widget.mouseClicked(mouseX, mouseY, button)) {
                    clickedInside = true;
                    break;
                }
            }
            if (!clickedInside) {
                chatTextField.setFocused(false);
            }
        }
        
        if (ignoreTextField != null && ignoreTextField.isFocused()) {
            boolean clickedInside = false;
            for (var widget : this.children()) {
                if (widget == ignoreTextField && widget.mouseClicked(mouseX, mouseY, button)) {
                    clickedInside = true;
                    break;
                }
            }
            if (!clickedInside) {
                ignoreTextField.setFocused(false);
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private boolean handleButtonClick(int mouseX, int mouseY) {
        int footerY = BASE_HEIGHT - FOOTER_HEIGHT;
        
        int cancelX = BUTTON_MARGIN;
        int cancelY = footerY + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2;
        
        if (mouseX >= cancelX && mouseX < cancelX + BUTTON_WIDTH
         && mouseY >= cancelY && mouseY < cancelY + BUTTON_HEIGHT) {
            ConfigManager.reload();
            tempValues = null;
            this.close();
            return true;
        }
        
        int saveX = BASE_WIDTH - BUTTON_WIDTH - BUTTON_MARGIN;
        int saveY = footerY + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2;
        
        if (mouseX >= saveX && mouseX < saveX + BUTTON_WIDTH
         && mouseY >= saveY && mouseY < saveY + BUTTON_HEIGHT) {
            applyTempValuesToConfig();
            ConfigManager.save();
            this.close();
            return true;
        }
        
        return false;
    }
    
    private void applyTempValuesToConfig() {
        ModConfig config = ConfigManager.getConfig();
        
        config.setMentionVolume(tempValues.getMentionVolume());
        
        config.setMentionColor(String.format("#%06X", tempValues.getMentionColor()));
        
        if (tempValues.getChatDisplay() != null) {
            String chatDisplay = tempValues.getChatDisplay().replace("&", "§");
            config.setChatDisplay(chatDisplay);
        }
        
        try {
            for (Map.Entry<String, Boolean> entry : tempValues.getAllBooleanFields().entrySet()) {
                Field field = ModConfig.class.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.setBoolean(config, entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (String player : com.jotech.academy_chat_qol.chat.IgnoreManager.getIgnoredPlayers()) {
            if (!tempValues.getIgnoredPlayers().contains(player)) {
                com.jotech.academy_chat_qol.chat.IgnoreManager.removeIgnoredPlayer(player);
            }
        }
        for (String player : tempValues.getIgnoredPlayers()) {
            com.jotech.academy_chat_qol.chat.IgnoreManager.addIgnoredPlayer(player);
        }
    }
    
    private boolean handleTabClick(int mouseX, int mouseY) {
        int tabWidth = BASE_WIDTH / 3;
        int tabY = 24;
        
        if (mouseY < tabY || mouseY > tabY + TAB_HEIGHT) {
            return false;
        }
        
        for (int i = 0; i < ConfigTab.values().length; i++) {
            int tabX = i * tabWidth;
            if (mouseX >= tabX && mouseX < tabX + tabWidth) {
                ConfigTab newTab = ConfigTab.values()[i];
                if (newTab != currentTab) {
                    currentTab = newTab;
                    initCurrentTab();
                }
                return true;
            }
        }
        
        return false;
    }
    
    private boolean handleUpdateClick(int mouseX, int mouseY) {
        if (!UpdateChecker.isUpdateAvailable()) {
            return false;
        }
        
        String updateText = "Update Available";
        int textWidth = textRenderer.getWidth(updateText);
        int padding = 5;
        
        int boxWidth = textWidth + padding * 2;
        int boxHeight = 14;
        int boxX = BASE_WIDTH - boxWidth - 5;
        int boxY = 3;
        
        if (mouseX >= boxX && mouseX < boxX + boxWidth 
         && mouseY >= boxY && mouseY < boxY + boxHeight) {
            this.client.keyboard.setClipboard("https://www.curseforge.com/minecraft/mc-mods/academy-chat-qol");
            if (this.client.player != null) {
                this.client.player.sendMessage(Text.literal("§7[Academy Chat QOL] §eCurseForge URL copied to clipboard!"), false);
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (chatTextField != null && chatTextField.isFocused()) {
            if (chatTextField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        if (ignoreTextField != null && ignoreTextField.isFocused()) {
            if (ignoreTextField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        if (keyCode == 256) {
            this.close();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chatTextField != null && chatTextField.isFocused()) {
            return chatTextField.charTyped(chr, modifiers);
        }
        
        if (ignoreTextField != null && ignoreTextField.isFocused()) {
            return ignoreTextField.charTyped(chr, modifiers);
        }
        
        return super.charTyped(chr, modifiers);
    }
    
    private boolean hasUnsavedChanges() {
        return !getChangesList().isEmpty();
    }
    
    private java.util.List<String> getChangesList() {
        java.util.List<String> changes = new java.util.ArrayList<>();
        if (tempValues == null) return changes;
        
        ModConfig config = ConfigManager.getConfig();
        int generalChanges = 0;
        int customizationChanges = 0;
        
        if (Math.abs(tempValues.getMentionVolume() - config.getMentionVolume()) > 0.01f) {
            generalChanges++;
        }
        
        try {
            String hex = config.getMentionColor().replace("#", "");
            int configColor = Integer.parseInt(hex, 16);
            if (tempValues.getMentionColor() != configColor) {
                generalChanges++;
            }
        } catch (Exception e) {}
        
        try {
            for (java.util.Map.Entry<String, Boolean> entry : tempValues.getAllBooleanFields().entrySet()) {
                java.lang.reflect.Field field = ModConfig.class.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (!entry.getValue().equals(field.getBoolean(config))) {
                    generalChanges++;
                }
            }
        } catch (Exception e) {}
        
        if (tempValues.getChatDisplay() != null && !tempValues.getChatDisplay().replace("&", "§").equals(config.getChatDisplay())) {
            customizationChanges++;
        }
        
        java.util.List<String> currentIgnored = com.jotech.academy_chat_qol.chat.IgnoreManager.getIgnoredPlayers();
        int ignoreChanges = 0;
        for (String player : tempValues.getIgnoredPlayers()) {
            if (!currentIgnored.contains(player)) ignoreChanges++;
        }
        for (String player : currentIgnored) {
            if (!tempValues.getIgnoredPlayers().contains(player)) ignoreChanges++;
        }
        
        if (generalChanges > 0) {
            changes.add("General (x" + generalChanges + ")");
        }
        if (customizationChanges > 0) {
            changes.add("Chat Customization (x" + customizationChanges + ")");
        }
        if (ignoreChanges > 0) {
            changes.add("Ignore List (x" + ignoreChanges + ")");
        }
        
        return changes;
    }
    
    @Override
    public void close() {
        if (hasUnsavedChanges()) {
            confirmationOverlay.show(
                getChangesList(),
                () -> {
                    ConfigManager.reload();
                    tempValues = null;
                    if (this.client != null) {
                        this.client.setScreen(parent);
                    }
                },
                () -> {}
            );
        } else {
            if (this.client != null) {
                this.client.setScreen(parent);
            }
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
}
