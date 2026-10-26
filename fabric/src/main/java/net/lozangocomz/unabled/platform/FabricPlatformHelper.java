package net.lozangocomz.unabled.platform;

import net.lozangocomz.unabled.platform.services.IPlatformHelper;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return player.getClass().getName().contains("FakePlayer");
    }
}