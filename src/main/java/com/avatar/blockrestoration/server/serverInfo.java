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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = main.MODID)
public class serverInfo {
    private static ServerLevel currentWorld;
    private static long currentTime;
    private static boolean isNight;
    private static int currentDay;
    private static int currentHours;
    private static int currentMinutes;
    private static List<ServerPlayer> currentPlayers = new ArrayList<ServerPlayer>();
    private static List<Mob> currentMobs = new ArrayList<Mob>();

    @SubscribeEvent
    public static void ticksServer(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            if (currentWorld == null) {
                System.out.println(Monster.class.arrayType());
            }
            ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
            if (world == null)
                return;
            long time = world.getDayTime();
            if (time % 20 == 0) {
                currentWorld = world;
            }
            if (time % 20 * 1 / 10 == 0 && !isNight && currentPlayers != null) {
                BlockRestorer.restoreBlocks();
            }
            if (time % 20 * 5 == 0 && currentPlayers != null) {
                BlockRestorer.BlockBurningCheck();
                BlockRestorer.setWorld(currentWorld);
                BlockRestorer.animateBlockDestroyed();
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getExploder() instanceof Creeper && currentWorld != null) {
            List<BlockPos> affectedBlocks = event.getAffectedBlocks();
            for (BlockPos pos : affectedBlocks) {
                BlockState state = currentWorld.getBlockState(pos);
                if (state.getBlock() != Blocks.AIR) {
                    BlockRestorer.addBrokenBlock(pos, state);
                }
            }
        }
    }

    @SubscribeEvent
    public void onNeighborNotify(BreakEvent event) {
        BlockState state = event.getState();
        System.out.println(state);
        if (!state.is(Blocks.FIRE) && !state.isAir()) {
            // BlockRestorer.addBlockBurning(event.getPos(), state);
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
