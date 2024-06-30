package com.avatar.avatar_blockrestoration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

public class GlobalConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> MAIN_BLOCK;
    public static ForgeConfigSpec.ConfigValue<String> RADIUS_SCAN;
    static {
        setupConfig();
    }

    private static void setupConfig() {
        BUILDER.comment("Broken Blocks Data").push("mainBlock");
        MAIN_BLOCK = BUILDER
                .comment("Default block get effect restore")
                .define("default", "minecraft:black_banner");
        BUILDER.pop();

        BUILDER.comment("Around main block radius scan").push("radiusScan");
        RADIUS_SCAN = BUILDER
                .comment("Default radius scan")
                .define("default", "20");
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
    public static Block loadMainBlock() {
        Block block = Blocks.BLACK_BANNER;
        if (CONFIG.isLoaded()) {
            String mainBlock = MAIN_BLOCK.get();
            try {
                block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mainBlock));
            } catch (Exception e) {
                System.out.println("Block not found: " + mainBlock);
            }
        }
        return block;
    }

    // Method to load data
    public static int loadRadiusBlock() {
        int radius = 20;
        if (CONFIG.isLoaded()) {
            try {
                radius = Integer.valueOf(RADIUS_SCAN.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer input");
            }
        }
        return radius;
    }
}
