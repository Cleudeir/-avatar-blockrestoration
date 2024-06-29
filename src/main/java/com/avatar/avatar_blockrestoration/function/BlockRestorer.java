package com.avatar.avatar_blockrestoration.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.avatar.avatar_blockrestoration.server.Server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockRestorer {
    private static Map<BlockPos, BlockState> brokenBlocks = new HashMap<>();
    private static Map<BlockPos, BlockState> aroundBlocksTable = new HashMap<>();
    private static Map<BlockPos, BlockState> perimeterBlocksTable = new HashMap<>();

    public static void start(ServerLevel world) {
        BlockRestorerDataDTO data = BlockRestorerDataHandler.load(world);
        aroundBlocksTable = data.getAroundBlocksTable();
        brokenBlocks = data.getBrokenBlocks();
        perimeterBlocksTable = data.getPerimeterBlocksTable();
    }

    public static void saveData() {
        BlockRestorerDataHandler
                .save(new BlockRestorerDataDTO(brokenBlocks, aroundBlocksTable, perimeterBlocksTable));
    }

    public static void checkBlockStatesAroundTable(ServerLevel world) {
        System.out.println("brokenBlocks" + brokenBlocks.size());
        System.out.println("aroundBlocksTable " + aroundBlocksTable.size());
        System.out.println("perimeterBlocksTable " + perimeterBlocksTable.size());
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
        System.out.println("Player put blocks");
        brokenBlocks.remove(pos);
        BlockState state = world.getBlockState(pos);
        System.out.println(state);
        aroundBlocksTable.put(pos, state);

    }

    public static void updateBreakBlock(BlockPos pos) {
        System.out.println("Player broken blocks");
        aroundBlocksTable.remove(pos);
        brokenBlocks.remove(pos);

    }

    public static void setBlockStatesTable(ServerLevel world, BlockPos tablePos) {
        removeBlockStatesTable(world);
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
        listAllBlocksInPerimeter(world, tablePos, radius);
    }

    public static void listAllBlocksInPerimeter(ServerLevel world, BlockPos block, int radius) {
        System.out.println("Listing all blocks in perimeter:");
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + i;
            int z = block.getZ() + radius;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 1;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + i;
            int z = block.getZ() - radius;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 1;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + radius;
            int z = block.getZ() + i;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 1;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() - radius;
            int z = block.getZ() + i;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 1;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
    }

    public static void removeBlockStatesTable(ServerLevel world) {
        for (BlockPos pos : aroundBlocksTable.keySet()) {
            BlockState blockState = aroundBlocksTable.get(pos);
            if (blockState.getBlock() == Server.setDynamicBlock()) {
                world.destroyBlock(pos, false);
                break;
            }
        }
        aroundBlocksTable.clear();
        perimeterBlocksTable.clear();
    }

    public static void restoreBlocksFirst(ServerLevel world) {
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
                } else {
                    continue;
                }
            }
        }
    }

    public static void animates(ServerLevel world) {
        if (brokenBlocks.size() > 0 && world != null) {
            for (Map.Entry<BlockPos, BlockState> entry : brokenBlocks.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockAnimationHandler.animateBlock(world, pos);
            }
        }
        if (perimeterBlocksTable.size() > 0 && world != null) {
            for (Map.Entry<BlockPos, BlockState> entry : perimeterBlocksTable.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockAnimationHandler.animatePerimeter(world, pos);
            }
        }
    }
}
