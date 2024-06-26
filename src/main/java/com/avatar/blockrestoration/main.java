package com.avatar.blockrestoration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(main.MODID)
public class main {
    public static final String MODID = "avatar-blockrestoration";

    public main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new serverInfo());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Some client setup code
    }
}
