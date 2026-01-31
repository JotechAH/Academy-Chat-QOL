package com.jotech.academy_chat_qol.chat;

import java.util.ArrayList;
import java.util.List;

public class IgnoreData {
    private List<String> ignoredPlayers = new ArrayList<>();
    
    public List<String> getIgnoredPlayers() {
        return ignoredPlayers;
    }
    
    public void setIgnoredPlayers(List<String> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }
}