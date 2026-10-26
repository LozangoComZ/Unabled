package net.lozangocomz.unabled;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lozangocomz.unabled.config.FabricConfig;
import net.lozangocomz.unabled.network.ConfigSyncPayload;

public class UnabledClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE,
                (payload, context) -> Config.set(payload.settings()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FabricConfig.load());
    }
}