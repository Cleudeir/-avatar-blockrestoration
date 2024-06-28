package com.avatar.avatar_blockrestoration.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockRestorer {
    private static Map<BlockPos, BlockState> brokenBlocks = new HashMap<>();
    private static Map<BlockPos, BlockState> aroundBlocksTable = new HashMap<>();

    public static void start(ServerLevel world) {
        BlockRestorerDataDTO data = BlockRestorerDataHandler.load(world);
        aroundBlocksTable = data.getAroundBlocksTable();
        brokenBlocks = data.getBrokenBlocks();
    }

    private static void saveData() {
        BlockRestorerDataHandler
                .save(new BlockRestorerDataDTO(brokenBlocks, aroundBlocksTable));
    }

    public static void checkBlockStatesAroundTable(ServerLevel world) {
        System.out.println(brokenBlocks.size());
        if (world != null && !aroundBlocksTable.isEmpty()) {
            Iterator<Map.Entry<BlockPos, BlockState>> iterator = aroundBlocksTable.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, BlockState> entry = iterator.next();
                BlockPos pos = entry.getKey();
                BlockState state = entry.getValue();
                boolean isBrokenBlock = brokenBlocks.containsKey(pos);
                BlockState newState = world.getBlockState(pos);
                if (newState != null && newState.getBlock() == Blocks.AIR
                        && !isBrokenBlock) {
                    brokenBlocks.put(pos, state);
                    iterator.remove();
                }
            }
        }
    }

    public static void updateBlock(ServerLevel world, BlockPos pos) {
        System.out.println("updateBlock");
        brokenBlocks.remove(pos);
        BlockState state = world.getBlockState(pos);
        System.out.println(state);
        aroundBlocksTable.put(pos, state);
        // saveData();
    }

    public static void updateBreakBlock(BlockPos pos) {
        System.out.println("updateBreakBlock");
        aroundBlocksTable.remove(pos);
        brokenBlocks.remove(pos);
        // saveData();
    }

    public static void setBlockStatesTable(ServerLevel world, BlockPos tablePos) {
        int radius = 20;
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

    public static void restoreBlocksFirst(ServerLevel world) {
        int index = 0;
        if (world != null && !brokenBlocks.isEmpty()) {
            Iterator<Map.Entry<BlockPos, BlockState>> iterator = brokenBlocks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, BlockState> entry = iterator.next();
                BlockPos pos = entry.getKey();
                BlockState state = entry.getValue();
                boolean entityExists = false;
                VoxelShape shape = state.getShape(world, pos);
                if (shape != null && !shape.isEmpty()) {
                    entityExists = world.getEntities(null, shape.bounds().move(pos)).size() > 0;
                }
                if (!entityExists) {
                    world.setBlockAndUpdate(pos, state);
                    System.out.println("restoreBlocks");
                    System.out.println(state);
                    iterator.remove();
                    index++;
                    break;
                } else {
                    continue;
                }
            }
        }
        if (index > 0) {
            saveData();
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
