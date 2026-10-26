package net.lozangocomz.unabled;

import net.lozangocomz.unabled.config.NeoForgeConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class UnabledMod {
    public UnabledMod(IEventBus modEventBus, ModContainer modContainer) {
        CommonClass.init();
        NeoForgeConfig.register(modContainer, modEventBus);
    }
}