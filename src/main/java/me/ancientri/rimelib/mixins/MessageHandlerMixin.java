package me.ancientri.rimelib.mixins;

import me.ancientri.rimelib.util.event.ClientReceiveMessageEvents;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MessageHandler.class, priority = 1001) // Apply after FAPI to not need to care about its allow events
public class MessageHandlerMixin {
	@Inject(method = "onGameMessage", at = @At("HEAD"))
	private void onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
		if (overlay) {
			ClientReceiveMessageEvents.INSTANCE.getON_OVERLAY_TEXT().invoker().accept(message);
			ClientReceiveMessageEvents.INSTANCE.getON_OVERLAY_STRING().invoker().accept(Formatting.strip(message.getString()));
		} else {
			ClientReceiveMessageEvents.INSTANCE.getON_GAME_TEXT().invoker().accept(message);
			ClientReceiveMessageEvents.INSTANCE.getON_GAME_STRING().invoker().accept(Formatting.strip(message.getString()));
		}
	}
}
