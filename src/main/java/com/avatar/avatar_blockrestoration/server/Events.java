package com.avatar.avatar_blockrestoration.server;

import com.avatar.avatar_blockrestoration.GlobalConfig;
import com.avatar.avatar_blockrestoration.Main;
import com.avatar.avatar_blockrestoration.function.BlockRestorer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class Events {
    private static ServerLevel currentWorld;
    private static long currentTime = 0;

    public static boolean checkPeriod(double seconds) {
        double divisor = (double) (seconds * 20);
        return currentTime % divisor == 0;
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
                if (checkPeriod(10) && !isNight) {
                    BlockRestorer.getRestoreBlocks(world);
                }
                if (checkPeriod(3)) {
                    BlockRestorer.getAnimate(world);
                }
                if (checkPeriod(15)) {
                    BlockRestorer.checkBlockStatesAroundMainBlock(world);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPutMainBlock(BlockEvent.EntityPlaceEvent event) {
        BlockState getPlacedBlock = event.getPlacedBlock();
        if (getPlacedBlock.getBlock() == GlobalConfig.loadMainBlock()) {
            System.out.println("A table was placed in the world!");
            BlockPos mainPos = event.getPos();
            BlockRestorer.setBlockStatesAroundMainBlock(currentWorld, mainPos);
            BlockRestorer.getAnimate(currentWorld);
        } else {
            System.out.println("Player put block" + getPlacedBlock.toString());
            BlockPos blockPos = event.getPos();
            if (currentWorld != null) {
                BlockRestorer.updatePutBlockAroundBlocks(currentWorld, blockPos);
            }
        }
    }

    @SubscribeEvent
    public static void onServerShutdown(ServerStoppingEvent event) {
        System.out.println("Server is shutting down!");
        BlockRestorer.saveData();
    }

    @SubscribeEvent
    public void onPlayerBreak(BreakEvent event) {
        BlockState state = event.getState();
        Player player = event.getPlayer();
        BlockPos position = event.getPos();

        if (player != null) {
            if (!state.is(Blocks.FIRE) || !state.isAir()) {
                if (currentWorld != null) {
                    BlockRestorer.updatePlayerBreakBlockAroundBlocks(position);
                }
            }
        }
        if (event.getState().getBlock() == GlobalConfig.loadMainBlock() && currentWorld != null) {
            System.out.println("A table was removed from the world!");
            BlockRestorer.removeBlockAroundMainBlock(currentWorld);
        }
    }

}
