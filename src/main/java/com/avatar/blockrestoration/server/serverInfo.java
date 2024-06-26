package com.avatar.blockrestoration.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.avatar.blockrestoration.main;
import com.avatar.blockrestoration.function.BlockRestorer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.level.ExplosionEvent;
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
    private static List<Monster> currentMonsters = new ArrayList<Monster>();
    private static List<Animal> currentAnimals = new ArrayList<Animal>();
    private static List<AmbientCreature> currentAmbientCreatures = new ArrayList<AmbientCreature>();

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

                List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();
                if (players == null)
                    return;
                currentMobs.clear();
                currentMonsters.clear();
                currentAnimals.clear();
                currentAmbientCreatures.clear();
                for (ServerPlayer player : players) {
                    double px = player.getX();
                    double py = player.getY();
                    double pz = player.getZ();

                    AABB boundingBox = new AABB(
                            px - 100, py - 100, pz - 100,
                            px + 100, py + 100, pz + 100);

                    List<Mob> mobs = world.getEntitiesOfClass(Mob.class, boundingBox);
                    List<Monster> monsters = world.getEntitiesOfClass(Monster.class, boundingBox);
                    List<Animal> animals = world.getEntitiesOfClass(Animal.class, boundingBox);
                    List<AmbientCreature> ambientCreatures = world.getEntitiesOfClass(AmbientCreature.class,
                            boundingBox);
                    currentAmbientCreatures.addAll(ambientCreatures);
                    currentAnimals.addAll(animals);
                    currentMonsters.addAll(monsters);
                    currentMobs.addAll(mobs);

                }

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
            if (time % 20 * 1 / 10 == 0 && !isNight && currentPlayers != null) {
                BlockRestorer.restoreBlocks();
            }
            if (time % 20 * 8 == 0 && currentPlayers != null) {
                BlockRestorer.setWorld(currentWorld);
                BlockRestorer.animateBlockDestroyed();
            }
            if (time % 20 * 1 == 0 && currentPlayers != null) {
                /*
                 * for (ServerPlayer player : currentPlayers) {
                 * player.sendSystemMessage(Component.literal("Day: " + currentDay), false);
                 * player.sendSystemMessage(Component.literal("Time: " + currentHours + ":" +
                 * currentMinutes),
                 * false);
                 * player.sendSystemMessage(Component.literal("IsNight: " + isNight), false);
                 * player.sendSystemMessage(Component.literal("Mobs qnt: " + currentMobs.size()
                 * + ", types: "
                 * + getAllNameType(currentMobs)), false);
                 * player.sendSystemMessage(Component.literal("Monsters qnt: " +
                 * currentMonsters.size() + ", types: "
                 * + getAllNameType(currentMonsters)),
                 * false);
                 * player.sendSystemMessage(Component.literal("Animals qnt: " +
                 * currentAnimals.size() + ", types: "
                 * + getAllNameType(currentAnimals)), false);
                 * player.sendSystemMessage(Component.literal("AmbientCreatures qnt: " +
                 * currentAmbientCreatures.size()
                 * + ", types: " + getAllNameType(currentAmbientCreatures)), false);
                 * player.sendSystemMessage(Component.literal("Players: " +
                 * currentPlayers.size()), false);
                 * }
                 */
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

    public static <T extends Entity> Set<String> getAllNameType(List<T> entities) {
        Set<String> mobTypes = new HashSet<>();
        Set<Set<String>> mobTags = new HashSet<>();
        for (T mob : entities) {
            String mobImpulse = mob.getSoundSource().toString();
            String mobClass = mob.getClass().toString();
            System.out.println(mobClass);

            if (mobImpulse == "HOSTILE") {
                String mobName = mob.getType().toString().replaceAll("entity.minecraft.", "");
                System.out.println(mobName);
            }
            String type = mob.getType().toString().replaceAll("entity.minecraft.", "");
            Set<String> tags = mob.getTags();
            mobTypes.add(type);
            mobTags.add(tags);
        }
        return mobTypes;
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
