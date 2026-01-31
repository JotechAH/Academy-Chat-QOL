package com.jotech.academy_chat_qol.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class IgnoreManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("academy-chat-qol");
    private static final File IGNORE_FILE = CONFIG_DIR.resolve("ignored.json5").toFile();
    
    private static IgnoreData ignoreData;
    
    public static void load() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (IGNORE_FILE.exists()) {
            try {
                String content = Files.readString(IGNORE_FILE.toPath());
                content = removeJson5Comments(content);
                ignoreData = GSON.fromJson(content, IgnoreData.class);
                if (ignoreData == null) {
                    ignoreData = new IgnoreData();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ignoreData = new IgnoreData();
            }
        } else {
            ignoreData = new IgnoreData();
            save();
        }
    }
    
    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            
            StringBuilder json5Content = new StringBuilder();
            json5Content.append("{\n");
            json5Content.append("  /* ========================================\n");
            json5Content.append("   * ENGLISH - ANGLAIS\n");
            json5Content.append("   * ========================================\n");
            json5Content.append("   * List of ignored players (case sensitive so Player123 isn't equal to player123)\n");
            json5Content.append("   * Add or remove player names to manage your ignore list\n");
            json5Content.append("   * You can also use /ignore <player> and /unignore <player> in-game\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  /* ========================================\n");
            json5Content.append("   * FRANÇAIS - FRENCH\n");
            json5Content.append("   * ========================================\n");
            json5Content.append("   * Liste des joueurs ignorés (sensible à la casse donc Joueur123 n'est pas égal à joueur123)\n");
            json5Content.append("   * Ajoutez ou retirez des noms de joueurs pour gérer votre liste\n");
            json5Content.append("   * Vous pouvez aussi utiliser /ignore <joueur> et /unignore <joueur> en jeu\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  \"ignoredPlayers\": [\n");
            
            List<String> players = ignoreData.getIgnoredPlayers();
            for (int i = 0; i < players.size(); i++) {
                json5Content.append("    \"").append(players.get(i)).append("\"");
                if (i < players.size() - 1) {
                    json5Content.append(",");
                }
                json5Content.append("\n");
            }
            
            json5Content.append("  ]\n");
            json5Content.append("}\n");
            
            try (FileWriter writer = new FileWriter(IGNORE_FILE)) {
                writer.write(json5Content.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String removeJson5Comments(String json5) {
        json5 = json5.replaceAll("/\\*.*?\\*/", "");
        json5 = json5.replaceAll("//.*?\\n", "\n");
        return json5;
    }
    
    public static boolean isIgnored(String playerName) {
        if (ignoreData == null) {
            load();
        }
        return ignoreData.getIgnoredPlayers().contains(playerName);
    }

    public static boolean shouldIgnoreMessage(String messageText) {
        if (ignoreData == null) {
            load();
        }
        
        List<String> ignoredPlayers = ignoreData.getIgnoredPlayers();
        
        if (ignoredPlayers.isEmpty()) {
            return false;
        }
        
        for (String ignoredPlayer : ignoredPlayers) {
            if (messageText.contains(ignoredPlayer)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean addIgnoredPlayer(String playerName) {
        if (ignoreData == null) {
            load();
        }
        if (!ignoreData.getIgnoredPlayers().contains(playerName)) {
            ignoreData.getIgnoredPlayers().add(playerName);
            save();
            return true;
        }
        return false;
    }
    
    public static boolean removeIgnoredPlayer(String playerName) {
        if (ignoreData == null) {
            load();
        }
        boolean removed = ignoreData.getIgnoredPlayers().remove(playerName);
        if (removed) {
            save();
        }
        return removed;
    }
    
    public static List<String> getIgnoredPlayers() {
        if (ignoreData == null) {
            load();
        }
        return new ArrayList<>(ignoreData.getIgnoredPlayers());
    }
    
    public static Path getConfigDirectory() {
        return CONFIG_DIR;
    }
}