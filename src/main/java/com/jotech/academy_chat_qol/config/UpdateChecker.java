package com.jotech.academy_chat_qol.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jotech.academy_chat_qol.AcademyChatQOL;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    
    private static final String GITHUB_API_URL = "https://api.github.com/repos/JotechAH/Academy-Chat-QOL/releases/latest";
    private static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/academy-chat-qol";
    private static final String CURRENT_VERSION = "1.0.0";
    
    private static boolean hasChecked = false;
    private static boolean updateAvailable = false;
    private static String latestVersion = null;
    
    public static void checkForUpdates() {
        if (hasChecked) {
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(GITHUB_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Academy-Chat-QOL-Mod");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    String jsonResponse = response.toString();
                    
                    if (jsonResponse.trim().startsWith("{")) {
                        JsonObject releaseData = JsonParser.parseString(jsonResponse).getAsJsonObject();
                        
                        if (releaseData.has("tag_name")) {
                            latestVersion = releaseData.get("tag_name").getAsString();
                            
                            if (latestVersion.startsWith("v")) {
                                latestVersion = latestVersion.substring(1);
                            }

                            if (isNewerVersion(latestVersion, CURRENT_VERSION)) {
                                updateAvailable = true;
                                AcademyChatQOL.LOGGER.info("[Academy Chat QOL] Update available! Latest version: " + latestVersion + " (Current: " + CURRENT_VERSION + ")");
                            } else {
                                AcademyChatQOL.LOGGER.info("[Academy Chat QOL] You are using the latest version (" + CURRENT_VERSION + ")");
                            }
                        } else {
                            AcademyChatQOL.LOGGER.warn("[Academy Chat QOL] No tag_name found in GitHub response");
                        }
                    } else {
                        AcademyChatQOL.LOGGER.warn("[Academy Chat QOL] GitHub API returned unexpected format (possibly no releases published yet)");
                    }
                } else if (responseCode == 404) {
                    AcademyChatQOL.LOGGER.info("[Academy Chat QOL] No releases found on GitHub yet");
                } else {
                    AcademyChatQOL.LOGGER.warn("[Academy Chat QOL] Failed to check for updates. Response code: " + responseCode);
                }
                
                connection.disconnect();
                hasChecked = true;
                
            } catch (Exception e) {
                AcademyChatQOL.LOGGER.warn("[Academy Chat QOL] Could not check for updates: " + e.getMessage());
                hasChecked = true;
            }
        });
    }
    
    public static void notifyPlayer() {
        if (!updateAvailable) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        MutableText message = Text.literal("")
            .append(Text.literal("═══════════════════════════════════════\n").formatted(Formatting.GRAY))
            .append(Text.literal("Academy Chat QOL").formatted(Formatting.GOLD, Formatting.BOLD))
            .append(Text.literal(" - Update Available!\n").formatted(Formatting.YELLOW))
            .append(Text.literal("\n"))
            .append(Text.literal("Current version: ").formatted(Formatting.GRAY))
            .append(Text.literal(CURRENT_VERSION).formatted(Formatting.RED))
            .append(Text.literal("\n"))
            .append(Text.literal("Latest version: ").formatted(Formatting.GRAY))
            .append(Text.literal(latestVersion).formatted(Formatting.GREEN))
            .append(Text.literal("\n\n"));
        
        MutableText curseforgeLink = Text.literal("[Update via CurseForge]")
            .formatted(Formatting.AQUA, Formatting.BOLD, Formatting.UNDERLINE)
            .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CURSEFORGE_URL))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    Text.literal("Click to open CurseForge page\n")
                        .formatted(Formatting.YELLOW)
                        .append(Text.literal("Use the 'Update' button in your modpack").formatted(Formatting.GRAY))
                ))
            );
        
        message.append(curseforgeLink);
        message.append(Text.literal("\n"));
        message.append(Text.literal("═══════════════════════════════════════").formatted(Formatting.GRAY));
        
        client.player.sendMessage(message, false);
    }
    
    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        String[] newParts = newVersion.split("\\.");
        String[] currentParts = currentVersion.split("\\.");
        
        int length = Math.max(newParts.length, currentParts.length);
        
        for (int i = 0; i < length; i++) {
            int newPart = i < newParts.length ? parseVersionPart(newParts[i]) : 0;
            int currentPart = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;
            
            if (newPart > currentPart) {
                return true;
            } else if (newPart < currentPart) {
                return false;
            }
        }
        
        return false;
    }
    
    private static int parseVersionPart(String part) {
        try {
            String numericPart = part.split("-")[0];
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    public static String getLatestVersion() {
        return latestVersion;
    }
    
    public static String getCurrentVersion() {
        return CURRENT_VERSION;
    }
}