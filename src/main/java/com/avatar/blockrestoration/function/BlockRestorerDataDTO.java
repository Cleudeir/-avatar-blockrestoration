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

    public List<String> getIdListMap(Map<BlockPos, BlockState> list) {
        List<String> ListBlockPos = new ArrayList<>();
        for (Map.Entry<BlockPos, BlockState> entry : list.entrySet()) {
            BlockPos blockPos = entry.getKey();
            // string x, y, z
            String stringBlockPos = blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
            ListBlockPos.add(stringBlockPos);
        }
        return ListBlockPos;
    }

    public List<String> getIdListList(List<BlockPos> list) {
        List<String> ListBlockPos = new ArrayList<>();
        for (BlockPos entry : list) {
            BlockPos blockPos = entry;
            // string x, y, z
            String stringBlockPos = blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
            ListBlockPos.add(stringBlockPos);
        }
        return ListBlockPos;
    }

    public List<String> getBrokenBlocksListBlockId() {
        return getIdListMap(brokenBlocks);
    }

    public Map<BlockPos, BlockState> getAroundBlocksTable() {
        return aroundBlocksTable;
    }

    public List<String> getAroundBlocksTableListBlockId() {
        return getIdListMap(aroundBlocksTable);
    }

    public List<BlockPos> getPlayerBrokenBlocks() {
        return playerBrokenBlocks;
    }

    public List<String> getPlayerBrokenBlocksListBlockId() {
        return getIdListList(playerBrokenBlocks);
    }

}