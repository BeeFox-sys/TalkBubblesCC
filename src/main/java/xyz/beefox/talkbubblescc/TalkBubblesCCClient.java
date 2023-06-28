package xyz.beefox.talkbubblescc;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;

public class TalkBubblesCCClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("TalkBubblesSocketServer");

	private static class MessageServer
	{

		private final Thread thread;

		public MessageServer(int port)
		{
			thread = new Thread(() -> {
				LOGGER.info("Socket server starting");

				try (ServerSocket serverSocket = new ServerSocket(port))
				{
					LOGGER.info("Socket server listening on port {}", port);

					while (true)
					{
						Socket socket = serverSocket.accept();

						var session = true;
						while (session)
						{
							try (InputStream input = socket.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
							{
								while (true)
								{
									var message = reader.readLine();
									if (message == null)
										continue;

									LOGGER.info("Received: {}", message);

									if (message.equals("///quit"))
									{
										session = false;
										break;
									}

									if (MinecraftClient.getInstance().getNetworkHandler() == null)
									{
										LOGGER.info("Message not sent to server (not ingame)");
									}
									else
									{
										var passedData = new PacketByteBuf(Unpooled.buffer());
										passedData.writeString(message);
										ClientPlayNetworking.send(TalkBubblesCC.CREATE_MESSAGE_ID, passedData);
									}
								}
							}
							catch (EOFException ex)
							{
								LOGGER.warn("Caught EOF, restarting stream");
							}
							catch (IOException ex)
							{
								LOGGER.warn("Socket client exception", ex);
							}
						}
					}
				}
				catch (IOException ex)
				{
					LOGGER.error("Socket server exception", ex);
				}
			});
		}

		public void start()
		{
			thread.start();
		}

		public void stop()
		{
			thread.interrupt();
		}
	}

	private static MessageServer MESSAGE_SERVER;

	public static Identifier id(String path)
	{
		return new Identifier("talkbubblesnetworked", path);
	}

	@Override
	public void onInitializeClient() {

		LOGGER.info("Client Start");

		MESSAGE_SERVER = new MessageServer(25560);

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> MESSAGE_SERVER.start());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MESSAGE_SERVER.stop());

		
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}