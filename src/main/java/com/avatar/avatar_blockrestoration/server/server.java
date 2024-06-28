package com.avatar.avatar_blockrestoration.server;

import com.avatar.avatar_blockrestoration.main;
import com.avatar.avatar_blockrestoration.function.BlockRestorer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = main.MODID)
public class server {
    private static ServerLevel currentWorld;
    private static long currentTime = 0;

    public static boolean checkPeriod(double seconds) {
        double divisor = (double) (seconds * 20);
        return currentTime % divisor == 0;
    }

    public static Block setDynamicBlock(String blockName) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        return block;
    }

    @SubscribeEvent
    public static void ticksServer(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);

            if (world != null) {
                long time = world.getDayTime();
                int timeDay = (int) (time % 24000);
                boolean isNight = timeDay >= 13000 && timeDay <= 23000;
                currentTime = time;
                if (currentWorld == null && checkPeriod(1)) {
                    System.out.println("server started!!!!!!!");
                    BlockRestorer.start(world);
                }
                if (checkPeriod(1)) {
                    currentWorld = world;
                }

                if (checkPeriod(1) && !isNight) {
                    BlockRestorer.restoreBlocksFirst(world);
                }
                if (checkPeriod(4)) {
                    BlockRestorer.animateBlockDestroyed(world);
                }
                if (checkPeriod(5)) {
                    BlockRestorer.checkBlockStatesAroundTable(world);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPutTable(BlockEvent.EntityPlaceEvent event) {
        BlockState getPlacedBlock = event.getPlacedBlock();
        if (getPlacedBlock.getBlock() == setDynamicBlock("minecraft:crafting_table")) {
            System.out.println("A table was placed in the world!");
            BlockPos tablePos = event.getPos();
            BlockRestorer.setBlockStatesTable(currentWorld, tablePos);
        } else {
            System.out.println("put block" + getPlacedBlock.toString());
            BlockPos blockPos = event.getPos();
            if (currentWorld != null) {
                BlockRestorer.updateBlock(currentWorld, blockPos);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerBreak(BreakEvent event) {
        BlockState state = event.getState();
        Player player = event.getPlayer();
        BlockPos position = event.getPos();

        if (player != null) {
            if (!state.is(Blocks.FIRE) || !state.isAir()) {
                if (currentWorld != null) {
                    BlockRestorer.updateBreakBlock(position);
                }
            }
        }
        if (event.getState().getBlock() == setDynamicBlock("minecraft:crafting_table")) {
            System.out.println("A table was removed from the world!");
            BlockRestorer.removeBlockStatesTable();
        }
    }

}
