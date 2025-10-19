package me.ancientri.rimelib.util.event

import me.ancientri.rimelib.util.EventUtil
import net.fabricmc.fabric.api.event.Event
import net.minecraft.text.Text
import java.util.function.Consumer

/**
 * Events for reading messages received by the client, as an alternative to FAPI's events.
 */
object ClientReceiveMessageEvents {
	/**
	 * Fired upon receiving a game message from the server.
	 *
	 * This will be fired even if the message is canceled by a mod.
	 */
	val ON_GAME_TEXT: Event<Consumer<Text>> = EventUtil.createArrayBacked { listeners ->
		Consumer { text ->
			for (listener in listeners) {
				listener.accept(text)
			}
		}
	}

	/**
	 * Fired upon receiving a game message from the server, with the message as the string content of the text and formatting stripped.
	 *
	 * * This will be fired even if the message is canceled by a mod.
	 */
	val ON_GAME_STRING: Event<Consumer<String>> = EventUtil.createArrayBacked { listeners ->
		Consumer { text ->
			for (listener in listeners) {
				listener.accept(text)
			}
		}
	}

	/**
	 * Fired upon receiving an overlay message from the server.
	 *
	 * This will be fired even if the message is canceled by a mod.
	 */
	val ON_OVERLAY_TEXT: Event<Consumer<Text>> = EventUtil.createArrayBacked { listeners ->
		Consumer { text ->
			for (listener in listeners) {
				listener.accept(text)
			}
		}
	}

	/**
	 * Fired upon receiving an overlay message from the server, with the message as the string content of the text and formatting stripped.
	 *
	 * This will be fired even if the message is canceled by a mod.
	 */
	val ON_OVERLAY_STRING: Event<Consumer<String>> = EventUtil.createArrayBacked { listeners ->
		Consumer { text ->
			for (listener in listeners) {
				listener.accept(text)
			}
		}
	}
}