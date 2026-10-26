package net.lozangocomz.unabled;

import net.fabricmc.api.ModInitializer;
import net.lozangocomz.unabled.config.FabricConfig;
import net.lozangocomz.unabled.network.FabricServerNetworking;

public class UnabledMod implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricConfig.load();
        FabricServerNetworking.register();
        CommonClass.init();
    }
}