package com.jotech.academy_chat_qol;

import com.jotech.academy_chat_qol.commands.IgnoreCommand;
import com.jotech.academy_chat_qol.commands.ReloadCommand;
import com.jotech.academy_chat_qol.commands.StatusCommand;
import com.jotech.academy_chat_qol.commands.UnignoreCommand;
import com.jotech.academy_chat_qol.config.ConfigManager;
import com.jotech.academy_chat_qol.config.SoundListGenerator;
import com.jotech.academy_chat_qol.config.UpdateChecker;
import com.jotech.academy_chat_qol.chat.IgnoreManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcademyChatQOL implements ClientModInitializer {
    public static final String MOD_ID = "academy-chat-qol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static boolean hasNotified = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Academy Chat QOL");
        
        ConfigManager.load();
        IgnoreManager.load();
        
        SoundListGenerator.generateSoundList();
        
        UpdateChecker.checkForUpdates();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            IgnoreCommand.register(dispatcher);
            UnignoreCommand.register(dispatcher);
            ReloadCommand.register(dispatcher);
            StatusCommand.register(dispatcher);
        });
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!hasNotified && client.player != null && client.world != null) {
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        if (UpdateChecker.isUpdateAvailable()) {
                            client.execute(UpdateChecker::notifyPlayer);
                        }
                        hasNotified = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        
        LOGGER.info("Academy Chat QOL initialized successfully!");
    }
}