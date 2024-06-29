package com.avatar.avatar_blockrestoration.server;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class globalConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> MAIN_BLOCK;
    static {
        setupConfig();
    }

    private static void setupConfig() {
        BUILDER.comment("Broken Blocks Data").push("brokenBlocks");
        MAIN_BLOCK = BUILDER
                .comment("Default broken blocks data")
                .define("default", "minecraft:crafting_table");
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
        String mainBlock = "minecraft:crafting_table";
        if (CONFIG.isLoaded()) {
            mainBlock = MAIN_BLOCK.get();
        }
        return mainBlock;
    }
}
