package com.avatar.avatar_blockrestoration.server;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class GlobalConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> MAIN_BLOCK;
    static {
        setupConfig();
    }

    private static void setupConfig() {
        BUILDER.comment("Broken Blocks Data").push("brokenBlocks");
        MAIN_BLOCK = BUILDER
                .comment("Default block get effect restore")
                .define("default", "minecraft:black_banner");
        BUILDER.pop();
        CONFIG = BUILDER.build();
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
    }

    // Method to save data
    public static void save(String data) {
        CONFIG.save();
    }

    // Method to load data
    public static String loadMainBlock() {
        String mainBlock = "minecraft:black_banner";
        if (CONFIG.isLoaded()) {
            mainBlock = MAIN_BLOCK.get();
        }
        return mainBlock;
    }
}
