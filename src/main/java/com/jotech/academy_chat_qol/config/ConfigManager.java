package com.jotech.academy_chat_qol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("academy-chat-qol");
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("config.json5").toFile();
    
    private static ModConfig config;
    
    public static void load() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (CONFIG_FILE.exists()) {
            try {
                String content = Files.readString(CONFIG_FILE.toPath());
                content = removeJson5Comments(content);
                config = GSON.fromJson(content, ModConfig.class);
                if (config == null) {
                    config = new ModConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
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
            json5Content.append("   * Chat format configuration\n");
            json5Content.append("   * Do not modify {rank} otherwise player's rank will not appear\n");
            json5Content.append("   * Use {nickname} for player name\n");
            json5Content.append("   * Use {message} for the message content\n");
            json5Content.append("   * Use <#HEXCOLOR>text</#HEXCOLOR> for colored text\n");
            json5Content.append("   * \n");
            json5Content.append("   * EXAMPLE:\n");
            json5Content.append("   * {rank} <#FF5555>{nickname} ></#FF5555> <#FFFFFF>{message}</#FFFFFF>\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  /* ========================================\n");
            json5Content.append("   * FRANÇAIS - FRENCH\n");
            json5Content.append("   * ========================================\n");
            json5Content.append("   * Configuration du format du chat\n");
            json5Content.append("   * Ne pas modifier {rank} sinon les rangs des joueurs ne s'afficherons pas\n");
            json5Content.append("   * Utilisez {nickname} pour le nom du joueur\n");
            json5Content.append("   * Utilisez {message} pour le contenu du message\n");
            json5Content.append("   * Utilisez <#COULEURHEX>texte</#COULEURHEX> pour du texte coloré\n");
            json5Content.append("   * \n");
            json5Content.append("   * EXEMPLE:\n");
            json5Content.append("   * {rank} <#FF5555>{nickname} ></#FF5555> <#FFFFFF>{message}</#FFFFFF>\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  \"chatDisplay\": \"").append(escapeJson(config.chatDisplay)).append("\",\n");
            
            json5Content.append("  \n");
            json5Content.append("  /* ========================================\n");
            json5Content.append("   * ENGLISH - ANGLAIS\n");
            json5Content.append("   * ========================================\n");
            json5Content.append("   * When someone mentions your username in chat\n");
            json5Content.append("   * Mentions will appear as: YourName (bold and colored)\n");
            json5Content.append("   * Mention text color (hex format)\n");
            json5Content.append("   * And a sound will play when mentioned\n");
            json5Content.append("   * See 'available_sounds.txt' file for a full list of sounds\n");
            json5Content.append("   * Mention sound volume\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  /* ========================================\n");
            json5Content.append("   * FRANÇAIS - FRENCH\n");
            json5Content.append("   * ========================================\n");
            json5Content.append("   * Quand quelqu'un mentionne votre pseudo dans le chat\n");
            json5Content.append("   * Les mentions apparaîtront comme: @VotrePseudo (gras et coloré)\n");
            json5Content.append("   * Couleur du texte de mention (en format HEX)\n");
            json5Content.append("   * Un son jouera également lorsque vous êtes mentionné\n");
            json5Content.append("   * Voir le fichier 'available_sounds.txt' pour la liste de tous les sons disponible\n");
            json5Content.append("   * Volume sonore de la mention\n");
            json5Content.append("   */\n");
            json5Content.append("  \n");
            json5Content.append("  \"mentionColor\": \"").append(config.mentionColor).append("\",\n");
            json5Content.append("  \"mentionSound\": \"").append(config.mentionSound).append("\",\n");
            json5Content.append("  \"mentionVolume\": ").append(config.mentionVolume).append("\n");
            json5Content.append("}\n");
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
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

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    public static ModConfig getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }
    
    public static Path getConfigDirectory() {
        return CONFIG_DIR;
    }
}