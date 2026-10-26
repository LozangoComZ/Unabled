package net.lozangocomz.unabled.platform.services;

import net.minecraft.world.entity.player.Player;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isFakePlayer(Player player);

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}