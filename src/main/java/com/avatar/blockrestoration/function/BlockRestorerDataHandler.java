package com.avatar.blockrestoration.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BlockRestorerDataHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    // Define your config values here
    public static ServerLevel currentWorld;
    public static ForgeConfigSpec.ConfigValue<List<String>> BROKEN_BLOCKS;
    public static ForgeConfigSpec.ConfigValue<List<String>> AROUND_BLOCKS_TABLE;
    public static ForgeConfigSpec.ConfigValue<List<String>> PLAYER_BROKEN_BLOCKS;

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

        BUILDER.comment("Player Broken Blocks Data").push("playerBrokenBlocks");
        PLAYER_BROKEN_BLOCKS = BUILDER
                .comment("Default player broken blocks data")
                .define("default", new ArrayList<String>());
        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void setWorld(ServerLevel world) {
        currentWorld = world;
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
    }

    // Method to save data
    public static void save(BlockRestorerDataDTO data) {

        List<String> brokenBlocks = data.getBrokenBlocksListBlockId();
        List<String> aroundBlocksTable = data.getAroundBlocksTableListBlockId();
        List<String> playerBrokenBlocks = data.getPlayerBrokenBlocksListBlockId();

      
        BROKEN_BLOCKS.set(brokenBlocks);
        AROUND_BLOCKS_TABLE.set(aroundBlocksTable);
        PLAYER_BROKEN_BLOCKS.set(playerBrokenBlocks);
        CONFIG.save();
    }

    private static Map<BlockPos, BlockState> deserializeBlockMap(List<String> MapBlockPos) {
        Map<BlockPos, BlockState> map = new HashMap<>();
        for (String entry : MapBlockPos) {
            String[] split = entry.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            BlockPos blockPos = new BlockPos(x, y, z);
            ServerLevel world = BlockRestorerDataHandler.currentWorld;
            BlockState blockState = world.getBlockState(blockPos);
            map.put(blockPos, blockState);
        }
        return map;
    }

    private static List<BlockPos> deserializeBlockList(List<String> ListBlockPos) {
        List<BlockPos> list = new ArrayList<>();
        for (String entry : ListBlockPos) {
            String[] split = entry.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            BlockPos blockPos = new BlockPos(x, y, z);
            list.add(blockPos);
        }
        return list;
    }

    // Method to load data
    public static BlockRestorerDataDTO load() {
        // Load the config if not already loaded
        Map<BlockPos, BlockState> brokenBlocksGet = new HashMap<>();
        Map<BlockPos, BlockState> aroundBlocksTableGet = new HashMap<>();
        List<BlockPos> playerBrokenBlocksGet = new ArrayList<>();

        if (CONFIG.isLoaded()) {
            // Retrieve data from config
            brokenBlocksGet = deserializeBlockMap(BROKEN_BLOCKS.get());
            aroundBlocksTableGet = deserializeBlockMap(AROUND_BLOCKS_TABLE.get());
            playerBrokenBlocksGet = deserializeBlockList(PLAYER_BROKEN_BLOCKS.get());
            System.out.println(aroundBlocksTableGet);
            System.out.println("Data loaded from config");
        }
        return new BlockRestorerDataDTO(
                brokenBlocksGet,
                aroundBlocksTableGet,
                playerBrokenBlocksGet);
    }
}
