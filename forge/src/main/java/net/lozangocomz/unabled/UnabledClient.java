package net.lozangocomz.unabled;

import net.lozangocomz.unabled.config.ForgeConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class UnabledClient {
    private UnabledClient() {
    }

    public static void register() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> new ForgeConfigScreen(parent)
                )
        );
    }
}
