package com.avatar.blockrestoration.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRestorerDataDTO {
    Map<BlockPos, BlockState> brokenBlocks = new HashMap<>();
    Map<BlockPos, BlockState> aroundBlocksTable = new HashMap<>();
    List<BlockPos> playerBrokenBlocks = new ArrayList<>();

    public BlockRestorerDataDTO(Map<BlockPos, BlockState> brokenBlocks, Map<BlockPos, BlockState> aroundBlocksTable,
            List<BlockPos> playerBrokenBlocks) {
        this.brokenBlocks = brokenBlocks;
        this.aroundBlocksTable = aroundBlocksTable;
        this.playerBrokenBlocks = playerBrokenBlocks;
    }

    public Map<BlockPos, BlockState> getBrokenBlocks() {
        return brokenBlocks;
    }

    public List<String> getBrokenBlocksListString() {
        return brokenBlocks.keySet().stream().map(BlockPos::toString).toList();
    }

    public Map<BlockPos, BlockState> getAroundBlocksTable() {
        return aroundBlocksTable;
    }

    public List<String> getAroundBlocksTableListString() {
        return aroundBlocksTable.keySet().stream().map(BlockPos::toString).toList();
    }

    public List<BlockPos> getPlayerBrokenBlocks() {
        return playerBrokenBlocks;
    }

    public List<String> getPlayerBrokenBlocksListString() {
        return playerBrokenBlocks.stream().map(BlockPos::toString).toList();
    }

}