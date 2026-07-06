package me.ancientri.rimelib.mixins;

import me.ancientri.rimelib.util.event.ClientReceiveMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatListener.class, priority = 1001) // Apply after FAPI to not need to care about its allow events
public class ChatListenerMixin {
	@Inject(method = "handleSystemMessage", at = @At("HEAD"))
	private void onGameMessage(Component message, boolean remote, CallbackInfo ci) {
		ClientReceiveMessageEvents.INSTANCE.getON_GAME_TEXT().invoker().accept(message);
		ClientReceiveMessageEvents.INSTANCE.getON_GAME_STRING().invoker().accept(ChatFormatting.stripFormatting(message.getString()));
	}

	@Inject(method = "handleOverlay", at = @At("HEAD"))
	private void onOverlayMessage(Component message, CallbackInfo ci) {
		ClientReceiveMessageEvents.INSTANCE.getON_OVERLAY_TEXT().invoker().accept(message);
		ClientReceiveMessageEvents.INSTANCE.getON_OVERLAY_STRING().invoker().accept(ChatFormatting.stripFormatting(message.getString()));
	}
}
