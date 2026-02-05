package com.jotech.academy_chat_qol.config.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class TempConfigValues {
    
    private float mentionVolume;
    private int mentionColor;
    private Map<String, Boolean> booleanFields = new HashMap<>();
    private String chatDisplay;
    private List<String> ignoredPlayers = new ArrayList<>();
    
    public TempConfigValues() {
    }
    
    public float getMentionVolume() {
        return mentionVolume;
    }
    
    public void setMentionVolume(float mentionVolume) {
        this.mentionVolume = mentionVolume;
    }
    
    public int getMentionColor() {
        return mentionColor;
    }
    
    public void setMentionColor(int mentionColor) {
        this.mentionColor = mentionColor;
    }
    
    public void setBooleanField(String fieldName, boolean value) {
        booleanFields.put(fieldName, value);
    }
    
    public boolean getBooleanField(String fieldName, boolean defaultValue) {
        return booleanFields.getOrDefault(fieldName, defaultValue);
    }
    
    public Map<String, Boolean> getAllBooleanFields() {
        return booleanFields;
    }
    
    public String getChatDisplay() {
        return chatDisplay;
    }
    
    public void setChatDisplay(String chatDisplay) {
        this.chatDisplay = chatDisplay;
    }
    
    public List<String> getIgnoredPlayers() {
        return ignoredPlayers;
    }
    
    public void setIgnoredPlayers(List<String> ignoredPlayers) {
        this.ignoredPlayers = new ArrayList<>(ignoredPlayers);
    }
    
    public void addIgnoredPlayer(String playerName) {
        if (!ignoredPlayers.contains(playerName)) {
            ignoredPlayers.add(playerName);
        }
    }
    
    public void removeIgnoredPlayer(String playerName) {
        ignoredPlayers.remove(playerName);
    }
}
