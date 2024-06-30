package com.avatar.avatar_blockrestoration.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.avatar.avatar_blockrestoration.GlobalConfig;
import com.avatar.avatar_blockrestoration.animation.Animate;
import com.avatar.avatar_blockrestoration.server.ServerConfig;

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
    private static BlockPos mainBlockPos;

    public static void start(ServerLevel world) {
        aroundBlocksTable = ServerConfig.loadAroundMainBlock(world);
        brokenBlocks = ServerConfig.loadBrokenBlocks(world);
        perimeterBlocksTable = ServerConfig.loadPerimeterMainBlock(world);
        mainBlockPos = ServerConfig.loadMainBlockPos(world);
    }

    public static void saveData() {
        ServerConfig
                .save(brokenBlocks, aroundBlocksTable, perimeterBlocksTable, mainBlockPos);
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

    public static void updatePutBlockAroundBlocks(ServerLevel world, BlockPos pos) {
        if (aroundBlocksTable.containsKey(pos)) {
            System.out.println("Player put blocks in safe area");
            BlockState state = world.getBlockState(pos);
            brokenBlocks.remove(pos);
            aroundBlocksTable.put(pos, state);
        }
    }

    public static void updatePlayerBreakBlockAroundBlocks(BlockPos pos) {
        if (aroundBlocksTable.containsKey(pos)) {
            System.out.println("Player broken blocks");
            aroundBlocksTable.remove(pos);
            brokenBlocks.remove(pos);
        }
    }

    public static void setBlockStatesAroundTable(ServerLevel world, BlockPos mainPos) {
        removeBlockAroundTable(world);
        mainBlockPos = mainPos;
        int radius = GlobalConfig.loadRadiusBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = mainPos.offset(x, y, z);
                    BlockState blockState = world.getBlockState(currentPos);
                    if (!blockState.is(Blocks.FIRE) && !blockState.is(Blocks.AIR)
                            && !aroundBlocksTable.containsKey(currentPos)) {
                        aroundBlocksTable.put(currentPos, blockState);
                    }
                }
            }
        }
        getPerimeterBlocks(world, mainPos, radius);
    }

    public static void getPerimeterBlocks(ServerLevel world, BlockPos block, int radius) {
        System.out.println("Listing all blocks in perimeter:");
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + i;
            int z = block.getZ() + radius;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 2;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + i;
            int z = block.getZ() - radius;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 2;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() + radius;
            int z = block.getZ() + i;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 2;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
        for (int i = -radius; i <= radius; i++) {
            int x = block.getX() - radius;
            int z = block.getZ() + i;
            int y = (int) world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z) + 2;
            BlockPos currentPos = new BlockPos(x, y, z);
            BlockState blockState = world.getBlockState(currentPos);
            perimeterBlocksTable.put(currentPos, blockState);
        }
    }

    public static void removeBlockAroundTable(ServerLevel world) {
        world.destroyBlock(mainBlockPos, false);
        aroundBlocksTable.clear();
        perimeterBlocksTable.clear();
    }

    public static void getRestoreBlocks(ServerLevel world) {
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

    public static void getAnimate(ServerLevel world) {
        if (brokenBlocks.size() > 0 && world != null) {
            for (Map.Entry<BlockPos, BlockState> entry : brokenBlocks.entrySet()) {
                BlockPos pos = entry.getKey();
                Animate.destroyedBlocks(world, pos);
            }
        }
        if (perimeterBlocksTable.size() > 0 && world != null) {
            for (Map.Entry<BlockPos, BlockState> entry : perimeterBlocksTable.entrySet()) {
                BlockPos pos = entry.getKey();
                Animate.perimeter(world, pos);
            }
        }
    }
}
