package com.avatar.avatar_blockrestoration.animation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AirBlock;

public class Animate {

    public static void destroyedBlocks(ServerLevel world, BlockPos animationPos) {
        if (world.getBlockState(animationPos).getBlock() instanceof AirBlock && animationPos != null) {
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    animationPos.getX() + 0.5,
                    animationPos.getY() + 0.5,
                    animationPos.getZ() + 0.5,
                    3,
                    0.2,
                    0.2,
                    0.2,
                    0.05);
        }
    }

    public static void perimeter(ServerLevel world, BlockPos animationPos) {
        world.sendParticles(ParticleTypes.ENCHANT,
                animationPos.getX() + 0.5,
                animationPos.getY() + 0.5,
                animationPos.getZ() + 0.5,
                10,
                0.2,
                0.2,
                0.2,
                0.1);
    }

}
