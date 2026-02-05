package com.jotech.academy_chat_qol.config.gui;

public enum ConfigTab {
    GENERAL("General", "Général"),
    CUSTOMIZATION("Chat Customization", "Personnalisation"),
    IGNORE("Ignore List", "Liste d'ignorés");
    
    private final String nameEN;
    private final String nameFR;
    
    ConfigTab(String nameEN, String nameFR) {
        this.nameEN = nameEN;
        this.nameFR = nameFR;
    }
    
    public String getName(boolean isFrench) {
        return isFrench ? nameFR : nameEN;
    }
    
    public String getNameEN() {
        return nameEN;
    }
    
    public String getNameFR() {
        return nameFR;
    }
}
