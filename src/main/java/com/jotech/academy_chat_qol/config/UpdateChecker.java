package com.jotech.academy_chat_qol.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jotech.academy_chat_qol.AcademyChatQOL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    
    private static final String GITHUB_API_URL = "https://api.github.com/repos/JotechAH/Academy-Chat-QOL/releases/latest";
    private static final String CURRENT_VERSION = "0.1.0";
    
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