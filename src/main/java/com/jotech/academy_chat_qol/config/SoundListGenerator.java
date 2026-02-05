package com.jotech.academy_chat_qol.config;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundListGenerator {
    
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("academy-chat-qol");
    private static final Path SOUND_LIST_FILE = CONFIG_DIR.resolve("available_sounds.txt");
    
    public static void generateSoundList() {
        try {
            Files.createDirectories(CONFIG_DIR);
            
            List<String> sounds = new ArrayList<>();
            
            for (Identifier soundId : Registries.SOUND_EVENT.getIds()) {
                sounds.add(soundId.toString());
            }
            
            Collections.sort(sounds);
            
            try (FileWriter writer = new FileWriter(SOUND_LIST_FILE.toFile())) {
                writer.write("========================================\n");
                writer.write("  ACADEMY CHAT QOL - AVAILABLE SOUNDS\n");
                writer.write("  Total sounds: " + sounds.size() + "\n");
                writer.write("========================================\n");
                writer.write("\n");
                writer.write("Copy and paste any sound ID below into your config.json5\n");
                writer.write("for the \"mentionSound\" setting.\n");
                writer.write("\n");
                writer.write("POPULAR SOUNDS (Recommended):\n");
                writer.write("------------------------------\n");
                writer.write("minecraft:block.note_block.pling        - Notification sound (default)\n");
                writer.write("minecraft:entity.experience_orb.pickup  - XP pickup sound\n");
                writer.write("minecraft:block.note_block.bell         - Bell sound\n");
                writer.write("minecraft:entity.player.levelup         - Level up sound\n");
                writer.write("minecraft:block.amethyst_block.chime    - Amethyst chime\n");
                writer.write("minecraft:entity.arrow.hit_player       - Arrow hit sound\n");
                writer.write("minecraft:block.anvil.land              - Anvil land (loud)\n");
                writer.write("minecraft:ui.button.click               - Button click\n");
                writer.write("\n");
                writer.write("========================================\n");
                writer.write("  ALL AVAILABLE SOUNDS\n");
                writer.write("========================================\n");
                writer.write("\n");
                
                String currentNamespace = "";
                for (String sound : sounds) {
                    String namespace = sound.split(":")[0];
                    
                    if (!namespace.equals(currentNamespace)) {
                        currentNamespace = namespace;
                        writer.write("\n--- " + namespace.toUpperCase() + " ---\n");
                    }
                    
                    writer.write(sound + "\n");
                }
                
                writer.write("\n========================================\n");
                writer.write("  END OF LIST\n");
                writer.write("========================================\n");
            }
            
            System.out.println("[Academy Chat QOL] Generated sound list with " + sounds.size() + " sounds");
            
        } catch (IOException e) {
            System.err.println("[Academy Chat QOL] Error generating sound list: " + e.getMessage());
            e.printStackTrace();
        }
    }
}