package com.ilmusu.ilmusuenchantments.networking.messages;

import com.ilmusu.ilmusuenchantments.Resources;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.chunk.WorldChunk;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public abstract class _Message
{
	public final Identifier IDENTIFIER;

	public _Message(String name)
	{
		IDENTIFIER = Resources.identifier(name);
	}

	public void tryToDecodeAndHandle(ReentrantThreadExecutor<? extends Runnable> executor, Supplier<PlayerEntity> playerGetter, PacketByteBuf buf)
	{
		try
		{
			// Trying to instantiate a new message on the client
			_Message message = this.getClass().getDeclaredConstructor().newInstance();
			// Decoding the message contained in the buffer
			message.decode(buf);
			// Executing the handler
			executor.execute(() ->
			{
				PlayerEntity player = playerGetter.get();

				// Default handler
				message.handle(player);
			});
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException |NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	public abstract PacketByteBuf encode(PacketByteBuf buf);
	public abstract void decode(PacketByteBuf buf);
	public abstract void handle(PlayerEntity player);

	public void sendToClient(ServerPlayerEntity player)
	{
		ServerPlayNetworking.send(player, IDENTIFIER, this.encode(PacketByteBufs.create()));
	}

	public void sendToClientsTracking(WorldChunk chunk)
	{
		ServerChunkManager manager = ((ServerChunkManager)chunk.getWorld().getChunkManager());
		manager.threadedAnvilChunkStorage.getPlayersWatchingChunk(chunk.getPos(), false).forEach(this::sendToClient);
	}

	public void sendToClientsTracking(Entity entity)
	{
		ServerChunkManager manager = ((ServerChunkManager)entity.getEntityWorld().getChunkManager());
		Packet<?> packet = ServerPlayNetworking.createS2CPacket(IDENTIFIER, this.encode(PacketByteBufs.create()));
		manager.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(entity, packet);
	}

	public void sendToClientsTrackingAndSelf(ServerPlayerEntity player)
	{
		this.sendToClientsTracking(player);
		this.sendToClient(player);
	}

	public void sendToClientsTrackingAndSelf(Entity entity)
	{
		this.sendToClientsTracking(entity);
		if(entity instanceof ServerPlayerEntity player)
			this.sendToClient(player);
	}

	@Environment(EnvType.CLIENT)
	public void sendToServer()
	{
		ClientPlayNetworking.send(IDENTIFIER, this.encode(PacketByteBufs.create()));
	}
}
