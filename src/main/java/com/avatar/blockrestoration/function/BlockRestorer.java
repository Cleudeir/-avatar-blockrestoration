package com.avatar.blockrestoration.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRestorer {
    private static final List<BlockEntry> brokenBlocks = new ArrayList<>();
    private static int index = 0;
    private static ServerLevel currentWorld;
    private static Set<BlockEntry> burningBlocks = new HashSet<>();

    public static void setWorld(ServerLevel world) {
        currentWorld = world;
    }

    public static void addBrokenBlock(BlockPos pos, BlockState state) {
        brokenBlocks.add(new BlockEntry(pos, state));
    }

    public static void addBlockBurning(BlockPos pos, BlockState state) {
        burningBlocks.add(new BlockEntry(pos, state));
    }

    public static void BlockBurningCheck() {
        if (currentWorld != null && burningBlocks.size() > 0) {
            Iterator<BlockEntry> iterator = burningBlocks.iterator();
            while (iterator.hasNext()) {
                BlockEntry item = iterator.next();
                BlockPos pos = item.getPos();
                BlockState state = item.state;
                BlockState newState = currentWorld.getBlockState(pos);
                if (newState.isAir()) {
                    addBrokenBlock(pos, state);
                    iterator.remove();
                } else if (!newState.is(Blocks.FIRE)) {
                    iterator.remove();
                }
            }
        }
    }

    public static void restoreBlocks() {
        if (brokenBlocks.size() > 0 && currentWorld != null) {
            BlockEntry item = brokenBlocks.get(index);
            BlockState state = item.getState();
            BlockPos pos = item.getPos();
            boolean entityExists = currentWorld.getEntities(null, state.getShape(currentWorld, pos).bounds().move(pos))
                    .size() > 0;
            System.out.println("brokenBlocks: " + brokenBlocks.size());
            if (state != null && state != Blocks.AIR.defaultBlockState() && currentWorld != null && !entityExists) {
                currentWorld.setBlockAndUpdate(pos, state);
                brokenBlocks.remove(index);
                index = 0;
            } else {
                if (index + 1 == brokenBlocks.size()) {
                    index = 0;
                } else {
                    index++;
                }
            }
        }
    }

    public static void animateBlockDestroyed() {
        if (brokenBlocks.size() > 0 && currentWorld != null) {
            for (int i = 0; i < brokenBlocks.size(); i++) {
                BlockEntry item = brokenBlocks.get(i);
                BlockPos pos = item.getPos();
                BlockAnimationHandler.animateBlock(currentWorld, pos);
            }
        }
    }

    private static class BlockEntry {
        private final BlockPos pos;
        private final BlockState state;

        public BlockEntry(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockState getState() {
            return state;
        }
    }
}
