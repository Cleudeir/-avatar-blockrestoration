package com.avatar.blockrestoration.server;

import java.util.ArrayList;
import java.util.List;

import com.avatar.blockrestoration.main;
import com.avatar.blockrestoration.function.BlockRestorer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = main.MODID)
public class serve {
    private static ServerLevel currentWorld;
    private static boolean isNight = false;
    private static long currentTime = 0;
    private static int currentDay = 0;
    private static int currentHours = 0;
    private static int currentMinutes = 0;
    private static List<ServerPlayer> currentPlayers = new ArrayList<ServerPlayer>();
    private static List<Mob> currentMobs = new ArrayList<Mob>();

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
            currentTime = time;
            if (checkPeriod(1)) {
                currentWorld = world;
            }
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
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getExploder() instanceof Creeper && currentWorld != null) {
            List<BlockPos> affectedBlocks = event.getAffectedBlocks();
            for (BlockPos pos : affectedBlocks) {
                BlockState state = currentWorld.getBlockState(pos);
                if (state.getBlock() != Blocks.AIR) {
                    // BlockRestorer.addBrokenBlock(pos, state);
                }
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
                System.out.println(state);
                BlockRestorer.addPlayerBrokenBlock(position);
            }
        }
        if (event.getState().getBlock() == Blocks.CRAFTING_TABLE) {
            System.out.println("A table was removed from the world!");
            BlockRestorer.removeBlockStatesTable();
        }
    }

    public static ServerLevel getCurrentWorld() {
        return currentWorld;
    }

    public static long getCurrentTime() {
        return currentTime;
    }

    public static boolean getIsNight() {
        return isNight;
    }

    public static int getCurrentDay() {
        return currentDay;
    }

    public static int getCurrentHours() {
        return currentHours;
    }

    public static int getCurrentMinutes() {
        return currentMinutes;
    }

    public static List<ServerPlayer> getCurrentPlayers() {
        return currentPlayers;
    }

    public static List<Mob> getCurrentMobs() {
        return currentMobs;
    }

}
