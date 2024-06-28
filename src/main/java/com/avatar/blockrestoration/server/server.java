package com.avatar.blockrestoration.server;

import com.avatar.blockrestoration.main;
import com.avatar.blockrestoration.function.BlockRestorer;
import com.avatar.blockrestoration.function.BlockRestorerDataHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = main.MODID)
public class server {
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
            if (world == null)
                return;
            long time = world.getDayTime();
            int timeDay = (int) (time % 24000);
            boolean isNight = timeDay >= 13000 && timeDay <= 23000;
            currentTime = time;
            if (checkPeriod(1)) {
                currentWorld = world;
            }
            BlockRestorerDataHandler.setWorld(world);
            if (checkPeriod(1) && !isNight) {
                BlockRestorer.restoreBlocks(world);
            }

            if (checkPeriod(4)) {
                BlockRestorer.animateBlockDestroyed(world);
            }
            if (checkPeriod(5)) {
                BlockRestorer.checkBlockStatesAroundTable(world);
            }
        }
    }

    @SubscribeEvent
    public static void onPutTable(BlockEvent.EntityPlaceEvent event) {
        BlockState getPlacedBlock = event.getPlacedBlock();
        if (getPlacedBlock.getBlock() == Blocks.CRAFTING_TABLE) {
            System.out.println("A table was placed in the world!");
            BlockPos tablePos = event.getPos();
            BlockRestorer.setBlockStatesTable(currentWorld, tablePos);
        }
    }

    @SubscribeEvent
    public void onPlayerBreak(BreakEvent event) {
        BlockState state = event.getState();
        Player player = event.getPlayer();
        BlockPos position = event.getPos();

        if (player != null) {
            if (!state.is(Blocks.FIRE) || !state.isAir()) {             
                BlockRestorer.addPlayerBrokenBlock(position);
            }
        }
        if (event.getState().getBlock() == Blocks.CRAFTING_TABLE) {
            System.out.println("A table was removed from the world!");
            BlockRestorer.removeBlockStatesTable();
        }
    }
}
