package com.jotech.academy_chat_qol.config.gui;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    
    private static KeyBinding configKeyBinding;
    
    public static void register() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.academy_chat_qol.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.academy_chat_qol"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeyBinding.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }
        });
    }
}
