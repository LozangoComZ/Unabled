package net.lozangocomz.unabled.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.lozangocomz.unabled.Config;

public final class FabricServerNetworking {
    private FabricServerNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(handler.getPlayer(), ConfigSyncPayload.TYPE)) {
                ServerPlayNetworking.send(handler.getPlayer(), ConfigSyncPayload.from(Config.get()));
            }
        });
    }
}