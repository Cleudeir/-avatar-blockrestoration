package com.avatar.blockrestoration.old;

import com.avatar.blockrestoration.main;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AirBlock;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = main.MODID)
public class BlockAnimationHandler {

    public static void animateBlock(ServerLevel world, BlockPos animationPos) {
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

}
