package xyz.beefox.talkbubblescc;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TalkBubblesCC implements ModInitializer 
{
	public static final Identifier CREATE_MESSAGE_ID = new Identifier("talkbubblescc", "create_message");
	private static final Logger LOGGER = LoggerFactory.getLogger("TalkBubblesSocketServer");

	public static List<CaptionElement> captions = new ArrayList<CaptionElement>();

	@Override
	public void onInitialize()
	{
		LOGGER.info("Server Start");
		ServerPlayNetworking.registerGlobalReceiver(CREATE_MESSAGE_ID, TalkBubblesCC::handleCreateMessage);
	}

	private static void handleCreateMessage(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var message = buf.readString();

		removeCaption(player.getUuid());

		CaptionElement caption = new CaptionElement(player, message);

		captions.add(caption);
	}

	public static void removeCaption(UUID player_id){

		// LOGGER.info("Deleting id {} Captions", player_id);
		Iterator<CaptionElement> captionIterator = captions.iterator();
		while (captionIterator.hasNext()) {
			CaptionElement captionEl = captionIterator.next();
			if(captionEl.playerID == player_id){
				captionEl.destroy();
				// LOGGER.info("Removing Caption For: {}", player_id);
				captionIterator.remove();
			}
		}
	}
}