package com.jotech.academy_chat_qol.mixin;

import com.jotech.academy_chat_qol.chat.ChatFormatter;
import com.jotech.academy_chat_qol.chat.IgnoreManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        String messageString = message.getString();
        
        if (IgnoreManager.shouldIgnoreMessage(messageString)) {
            ci.cancel();
            return;
        }
        
        Text formattedMessage = ChatFormatter.formatChatMessage(message);
        
        if (!formattedMessage.equals(message)) {
            ci.cancel();
            ((ChatHud)(Object)this).addMessage(formattedMessage, signature, indicator);
        }
    }
}