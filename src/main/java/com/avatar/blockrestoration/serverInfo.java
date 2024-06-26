package com.avatar.blockrestoration;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public class serverInfo {
    private static ServerLevel currentWorld;
    private static long currentTime;
    private static boolean isNight;
    private static int currentDay;
    private static int currentHours;
    private static int currentMinutes;
    private static List<ServerPlayer> currentPlayers;
    private static List<Mob> currentMobs;

    public static void ticksServer(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
            if (world != null) {
                long time = world.getDayTime();
                if (time % 20 == 0) {
                    currentWorld = world;
                    List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();

                    AABB boundingBox = new AABB(
                            world.getMinBuildHeight(),
                            0,
                            world.getMinBuildHeight(),
                            world.getMaxBuildHeight(),
                            world.getMaxBuildHeight(),
                            world.getMaxBuildHeight());

                    List<Mob> mobs = world.getEntitiesOfClass(Mob.class, boundingBox);

                    currentMobs = mobs;
                    currentPlayers = players;
                    currentTime = time;

                    int day = (int) (time / 24000);
                    currentDay = day;

                    int timeDay = (int) (time % 24000);
                    boolean verifyIsNightTime = timeDay >= 13000 && timeDay <= 23000;
                    isNight = verifyIsNightTime;

                    int percentDay = (int) (time % 24000);
                    int hours = ((int) (percentDay / 1000) + 6) % 24;
                    currentHours = hours;

                    int minutes = (int) ((percentDay % 1000) / 16.6667);
                    currentMinutes = minutes;
                }
                if (time % 20 * 30 == 0 && currentPlayers != null) {
                    for (ServerPlayer player : currentPlayers) {
                        // send message for all players with all infos
                        player.sendSystemMessage(Component.literal("Day: " + currentDay), false);
                        player.sendSystemMessage(Component.literal("Time: " + currentHours + ":" + currentMinutes),
                                false);
                        player.sendSystemMessage(Component.literal("IsNight: " + isNight), false);
                        player.sendSystemMessage(Component.literal("Mobs: " + currentMobs.size()), false);
                        player.sendSystemMessage(Component.literal("Players: " + currentPlayers.size()), false);
                    }
                }
            }
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
