package com.jotech.academy_chat_qol.lang;

import net.minecraft.client.MinecraftClient;

public class Lang {
    
    public static String get(String key) {
        String language = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        
        if (language.startsWith("fr_")) {
            return getFrench(key);
        }
        
        return getEnglish(key);
    }
    
    private static String getEnglish(String key) {
        return switch (key) {
            case "acq.reload.success" -> "§a[Academy Chat QOL] §7Configuration reloaded successfully!";
            case "acq.reload.error" -> "§c[Academy Chat QOL] Error while reloading: ";
            
            case "acq.status.title" -> "§a§l=== Academy Chat QOL ===";
            case "acq.status.format" -> "§eChat format:";
            case "acq.status.ignored" -> "§eIgnored players: §f";
            case "acq.status.open_folder" -> " §7[§bOpen folder§7]";
            case "acq.status.hover" -> "§7Click to open:\n§f";
            case "acq.status.player123" -> "Player123";
            case "acq.status.testmessage" -> "Testing message";
            
            case "acq.ignore.success" -> "§a[Academy Chat QOL] §7You successfully ignore that player, you will no longer see any messages containing this playe's nickname.";
            case "acq.ignore.already" -> "§c[Academy Chat QOL] You are already ignoring this player.";
            
            case "acq.unignore.success" -> "§a[Academy Chat QOL] §7You are no longer ignoring this player.";
            case "acq.unignore.not_ignored" -> "§c[Academy Chat QOL] You were not ignoring this player.";
            
            default -> key;
        };
    }
    
    private static String getFrench(String key) {
        return switch (key) {
            case "acq.reload.success" -> "§a[Academy Chat QOL] §7Configuration rechargée avec succès !";
            case "acq.reload.error" -> "§c[Academy Chat QOL] Erreur lors du rechargement: ";
            
            case "acq.status.title" -> "§a§l=== Academy Chat QOL ===";
            case "acq.status.format" -> "§eFormat du chat :";
            case "acq.status.ignored" -> "§eJoueurs ignorés : §f";
            case "acq.status.open_folder" -> " §7[§bOuvrir le dossier§7]";
            case "acq.status.hover" -> "§7Cliquez pour ouvrir :\n§f";
            case "acq.status.player123" -> "Joueur123";
            case "acq.status.testmessage" -> "Message de test";
            
            case "acq.ignore.success" -> "§a[Academy Chat QOL] §7Vous ignorez maintenant ce joueur, vous ne verrez plus aucun message contenant le pseudo de ce joueur";
            case "acq.ignore.already" -> "§c[Academy Chat QOL] Vous ignorez déjà ce joueur.";
            
            case "acq.unignore.success" -> "§a[Academy Chat QOL] §7Vous n'ignorez plus ce joueur.";
            case "acq.unignore.not_ignored" -> "§c[Academy Chat QOL] Vous n'ignoriez pas ce joueur.";
            
            default -> key;
        };
    }
}