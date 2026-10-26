package net.lozangocomz.unabled;

import net.lozangocomz.unabled.config.ForgeConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class UnabledMod {
    public UnabledMod(FMLJavaModLoadingContext context) {
        CommonClass.init();
        ForgeConfig.register(context);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> UnabledClient::register);
    }
}
