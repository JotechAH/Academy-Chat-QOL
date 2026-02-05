package com.jotech.academy_chat_qol.config;

public class ModConfig {
    public String chatDisplay = "{rank} <#AAAAAA>{nickname} §l></#AAAAAA> §r<#FFFFFF>{message}</#FFFFFF>";
    public String mentionColor = "#5DADE2";
    public String mentionSound = "cobblemon:evolution.notification";
    public float mentionVolume = 1.0f;
    
    public boolean disableMentions = false;
    public boolean disableIgnore = true;
    
    public String getChatDisplay() {
        return chatDisplay;
    }
    
    public String getMentionColor() {
        return mentionColor;
    }
    
    public String getMentionSound() {
        return mentionSound;
    }
    
    public float getMentionVolume() {
        return mentionVolume;
    }
    
    public boolean isDisableMentions() {
        return disableMentions;
    }
    
    public boolean isDisableIgnore() {
        return disableIgnore;
    }
    
    public void setChatDisplay(String chatDisplay) {
        this.chatDisplay = chatDisplay;
        ConfigManager.save();
    }
    
    public void setMentionColor(String mentionColor) {
        this.mentionColor = mentionColor;
        ConfigManager.save();
    }
    
    public void setMentionSound(String mentionSound) {
        this.mentionSound = mentionSound;
        ConfigManager.save();
    }
    
    public void setMentionVolume(float mentionVolume) {
        this.mentionVolume = mentionVolume;
        ConfigManager.save();
    }
    
    public void setDisableMentions(boolean disableMentions) {
        this.disableMentions = disableMentions;
        ConfigManager.save();
    }
    
    public void setDisableIgnore(boolean disableIgnore) {
        this.disableIgnore = disableIgnore;
        ConfigManager.save();
    }
}
