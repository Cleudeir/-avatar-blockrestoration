package com.avatar.blockrestoration;

import com.avatar.blockrestoration.function.BlockRestorer;
import com.avatar.blockrestoration.function.BlockRestorerDataHandler;
import com.avatar.blockrestoration.server.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(main.MODID)
public class main {
    public static final String MODID = "blockrestoration";

    public main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new server());
        System.out.println("Mod constructor called");
        BlockRestorerDataHandler.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
        System.out.println("Setup method called");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        System.out.println("Client setup method called");
        // Some client setup code
    }
}
