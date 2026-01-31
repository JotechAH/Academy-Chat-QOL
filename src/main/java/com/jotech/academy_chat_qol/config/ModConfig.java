package com.jotech.academy_chat_qol.config;

public class ModConfig {
    public String chatDisplay = "{rank} <#AAAAAA>{nickname} §l></#AAAAAA> §r<#FFFFFF>{message}</#FFFFFF>";
    public String mentionColor = "#5DADE2";
    public String mentionSound = "cobblemon:evolution.notification";
    public float mentionVolume = 1.0f;
    
    public String getChatDisplay() {
        return chatDisplay;
    }
    
    public void setChatDisplay(String chatDisplay) {
        this.chatDisplay = chatDisplay;
        ConfigManager.save();
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
    
    public float getMentionPitch() {
        return 1.0f;
    }
}