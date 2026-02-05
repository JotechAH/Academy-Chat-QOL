package com.jotech.academy_chat_qol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("academy-chat-qol");
    private static final File OLD_CONFIG_FILE = CONFIG_DIR.resolve("config.json5").toFile();
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("config.json").toFile();
    
    private static ModConfig config;
    
    public static void load() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (OLD_CONFIG_FILE.exists() && !CONFIG_FILE.exists()) {
            System.out.println("[Academy Chat QOL] Migration de config.json5 vers config.json...");
            migrateFromJson5ToJson();
        }

        if (CONFIG_FILE.exists()) {
            try {
                String content = Files.readString(CONFIG_FILE.toPath());
                JsonObject existingJson = JsonParser.parseString(content).getAsJsonObject();

                ModConfig defaultConfig = new ModConfig();

                config = mergeConfigs(existingJson, defaultConfig);

                boolean hasNewFields = checkForNewFields(existingJson, defaultConfig);
                if (hasNewFields) {
                    System.out.println("[Academy Chat QOL] Nouvelles configurations détectées, mise à jour du fichier...");
                    save();
                }
                
            } catch (Exception e) {
                System.err.println("[Academy Chat QOL] Erreur lors du chargement de la config:");
                e.printStackTrace();
                config = new ModConfig();
                save();
            }
        } else {
            System.out.println("[Academy Chat QOL] Création d'un nouveau fichier de configuration...");
            config = new ModConfig();
            save();
        }
    }

    private static void migrateFromJson5ToJson() {
        try {
            String content = Files.readString(OLD_CONFIG_FILE.toPath());
            content = removeJson5Comments(content);
            
            JsonObject oldJson = JsonParser.parseString(content).getAsJsonObject();
            ModConfig defaultConfig = new ModConfig();
            config = mergeConfigs(oldJson, defaultConfig);
            
            save();

            File backupFile = CONFIG_DIR.resolve("config.json5.backup").toFile();
            OLD_CONFIG_FILE.renameTo(backupFile);
            System.out.println("[Academy Chat QOL] Migration réussie! Ancien fichier sauvegardé en config.json5.backup");
            
        } catch (Exception e) {
            System.err.println("[Academy Chat QOL] Erreur lors de la migration:");
            e.printStackTrace();
        }
    }

    private static boolean checkForNewFields(JsonObject existingJson, ModConfig defaultConfig) {
        try {
            for (Field field : ModConfig.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                String fieldName = field.getName();
                
                if (!existingJson.has(fieldName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static ModConfig mergeConfigs(JsonObject existingJson, ModConfig defaultConfig) {
        ModConfig mergedConfig = new ModConfig();
        
        try {
            for (Field field : ModConfig.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                String fieldName = field.getName();
                
                if (existingJson.has(fieldName)) {
                    try {
                        Object value = GSON.fromJson(existingJson.get(fieldName), field.getType());
                        if (value != null) {
                            field.set(mergedConfig, value);
                        } else {
                            Object defaultValue = field.get(defaultConfig);
                            field.set(mergedConfig, defaultValue);
                        }
                    } catch (Exception e) {
                        Object defaultValue = field.get(defaultConfig);
                        field.set(mergedConfig, defaultValue);
                    }
                } else {
                    Object defaultValue = field.get(defaultConfig);
                    field.set(mergedConfig, defaultValue);
                }
            }
        } catch (Exception e) {
            System.err.println("[Academy Chat QOL] Erreur lors de la fusion des configs:");
            e.printStackTrace();
            return defaultConfig;
        }
        
        return mergedConfig;
    }
    
    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            
            String jsonContent = GSON.toJson(config);
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                writer.write(jsonContent);
            }
            
            System.out.println("[Academy Chat QOL] Configuration sauvegardée avec succès!");
            
        } catch (IOException e) {
            System.err.println("[Academy Chat QOL] Erreur lors de la sauvegarde de la config:");
            e.printStackTrace();
        }
    }
    
    private static String removeJson5Comments(String json5) {
        json5 = json5.replaceAll("/\\*.*?\\*/", "");
        json5 = json5.replaceAll("//.*?\\n", "\n");
        return json5;
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
    
    public static void reload() {
        config = null;
        load();
    }
}