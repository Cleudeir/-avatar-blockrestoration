package com.avatar.avatar_blockrestoration.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BlockRestorerDataHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    // Define your config values here
    public static ForgeConfigSpec.ConfigValue<List<String>> BROKEN_BLOCKS;
    public static ForgeConfigSpec.ConfigValue<List<String>> AROUND_BLOCKS_TABLE;
    public static ForgeConfigSpec.ConfigValue<List<String>> PERIMETER_BLOCKS_TABLE;
    // Initialize config values without static initializer block
    static {
        setupConfig();
    }

    private static void setupConfig() {
        BUILDER.comment("Broken Blocks Data").push("brokenBlocks");
        BROKEN_BLOCKS = BUILDER
                .comment("Default broken blocks data")
                .define("default", new ArrayList<String>());
        BUILDER.pop();

        BUILDER.comment("Around Blocks Table Data").push("aroundBlocksTable");
        AROUND_BLOCKS_TABLE = BUILDER
                .comment("Default around blocks table data")
                .define("default", new ArrayList<String>());
        BUILDER.pop();

        BUILDER.comment("Perimeter Blocks Table Data").push("perimeterBlocksTable");
        PERIMETER_BLOCKS_TABLE = BUILDER
                .comment("Default perimeter blocks table data")
                .define("default", new ArrayList<String>());
        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG);
    }

    // Method to save data
    public static void save(BlockRestorerDataDTO data) {
        List<String> brokenBlocks = data.getBrokenBlocksListBlockId();
        List<String> aroundBlocksTable = data.getAroundBlocksTableListBlockId();
        List<String> perimeterBlocksTable = data.getPerimeterBlocksTableListBlockId();
        BROKEN_BLOCKS.set(brokenBlocks);
        AROUND_BLOCKS_TABLE.set(aroundBlocksTable);
        PERIMETER_BLOCKS_TABLE.set(perimeterBlocksTable);
        CONFIG.save();
    }

    private static Map<BlockPos, BlockState> deserializeBlockMap(List<String> MapBlockPos, ServerLevel world) {
        Map<BlockPos, BlockState> map = new HashMap<>();
        for (String entry : MapBlockPos) {
            String[] split = entry.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            BlockPos blockPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(blockPos);
            map.put(blockPos, blockState);
        }
        return map;
    }

    // Method to load data
    public static BlockRestorerDataDTO load(ServerLevel world) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        // Load the config if not already loaded
        Map<BlockPos, BlockState> brokenBlocksGet = new HashMap<>();
        Map<BlockPos, BlockState> aroundBlocksTableGet = new HashMap<>();
        Map<BlockPos, BlockState> perimeterBlocksTable = new HashMap<>();
        if (CONFIG.isLoaded()) {
            // Retrieve data from config
            brokenBlocksGet = deserializeBlockMap(BROKEN_BLOCKS.get(), world);
            aroundBlocksTableGet = deserializeBlockMap(AROUND_BLOCKS_TABLE.get(), world);
            perimeterBlocksTable = deserializeBlockMap(PERIMETER_BLOCKS_TABLE.get(), world);
            System.out.println("Data loaded from config");
        }
        return new BlockRestorerDataDTO(
                brokenBlocksGet,
                aroundBlocksTableGet,
                perimeterBlocksTable);
    }
}
