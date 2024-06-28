package com.avatar.blockrestoration.function;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRestorer {
    private static Map<BlockPos, BlockState> brokenBlocks = new HashMap<>();
    private static Map<BlockPos, BlockState> aroundBlocksTable = new HashMap<>();
    private static List<BlockPos> playerBrokenBlocks = new ArrayList<>();

    static {

        BlockRestorerDataDTO data = BlockRestorerDataHandler.load();
        aroundBlocksTable = data.getAroundBlocksTable();
        brokenBlocks = data.getBrokenBlocks();
        playerBrokenBlocks = data.getPlayerBrokenBlocks();
    }

    private static void saveData() {
        BlockRestorerDataHandler
                .save(new BlockRestorerDataDTO(brokenBlocks, aroundBlocksTable, playerBrokenBlocks));
    }

    public static void addBrokenBlock(BlockPos pos, BlockState state) {
        brokenBlocks.put(pos, state);
        saveData();
    }

    public static void checkBlockStatesAroundTable(ServerLevel world) {
        if (world != null && !aroundBlocksTable.isEmpty()) {
            Iterator<Map.Entry<BlockPos, BlockState>> iterator = aroundBlocksTable.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, BlockState> entry = iterator.next();
                BlockPos pos = entry.getKey();
                BlockState state = entry.getValue();
                boolean isPlayerBroken = playerBrokenBlocks.contains(pos);
                if (isPlayerBroken) {
                    System.out.println("isPlayerBroken");
                    System.out.println(isPlayerBroken);
                }
                boolean isBrokenBlock = brokenBlocks.containsKey(pos);
                BlockState newState = world.getBlockState(pos);
                if (newState != null && newState.getBlock() == Blocks.AIR
                        && !isPlayerBroken
                        && !isBrokenBlock) {
                    brokenBlocks.put(pos, state);
                    iterator.remove();
                    saveData();
                }
            }
        }
    }

    public static void addPlayerBrokenBlock(BlockPos pos) {
        playerBrokenBlocks.add(pos);
        saveData();
    }

    public static void setBlockStatesTable(ServerLevel world, BlockPos tablePos) {
        int radius = 30;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = tablePos.offset(x, y, z);
                    BlockState blockState = world.getBlockState(currentPos);
                    if (!blockState.is(Blocks.FIRE) && !blockState.is(Blocks.AIR)
                            && !aroundBlocksTable.containsKey(currentPos)) {
                        aroundBlocksTable.put(currentPos, blockState);
                    }
                }
            }
        }
        saveData();
    }

    public static void removeBlockStatesTable() {
        aroundBlocksTable.clear();
        saveData();
    }

    public static void restoreBlocks(ServerLevel world) {
        System.out.println("restoreBlocks: " + brokenBlocks.size());
        if (world != null && !brokenBlocks.isEmpty() && world != null) {
            Iterator<Map.Entry<BlockPos, BlockState>> iterator = brokenBlocks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, BlockState> entry = iterator.next();
                BlockPos pos = entry.getKey();
                BlockState state = entry.getValue();
                boolean entityExists = world
                        .getEntities(null, state.getShape(world, pos).bounds().move(pos))
                        .size() > 0;

                if (state != null && state.getBlock() != Blocks.AIR && !entityExists) {
                    world.setBlockAndUpdate(pos, state);
                    iterator.remove();
                    saveData();
                }
            }
        }
    }

    public static void animateBlockDestroyed(ServerLevel world) {
        if (brokenBlocks.size() > 0 && world != null) {
            for (Map.Entry<BlockPos, BlockState> entry : brokenBlocks.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockAnimationHandler.animateBlock(world, pos);
            }
        }
    }
}
