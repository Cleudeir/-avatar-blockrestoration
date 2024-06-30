package com.avatar.avatar_blockrestoration.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRestorerDataDTO {
    Map<BlockPos, BlockState> brokenBlocks = new HashMap<>();
    Map<BlockPos, BlockState> aroundBlocksTable = new HashMap<>();
    Map<BlockPos, BlockState> perimeterBlocksTable = new HashMap<>();

    public BlockRestorerDataDTO(
            Map<BlockPos, BlockState> brokenBlocks,
            Map<BlockPos, BlockState> aroundBlocksTable,
            Map<BlockPos, BlockState> perimeterBlocksTable) {
        this.brokenBlocks = brokenBlocks;
        this.aroundBlocksTable = aroundBlocksTable;
        this.perimeterBlocksTable = perimeterBlocksTable;
    }

    public Map<BlockPos, BlockState> getBrokenBlocks() {
        return brokenBlocks;
    }

    public List<String> getIdListMap(Map<BlockPos, BlockState> list) {
        List<String> ListBlockPos = new ArrayList<>();
        for (Map.Entry<BlockPos, BlockState> entry : list.entrySet()) {
            BlockPos blockPos = entry.getKey();
            String stringBlockPos = blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
            ListBlockPos.add(stringBlockPos);
        }
        return ListBlockPos;
    }

    public List<String> getIdListList(List<BlockPos> list) {
        List<String> ListBlockPos = new ArrayList<>();
        for (BlockPos entry : list) {
            BlockPos blockPos = entry;
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

    public Map<BlockPos, BlockState> getPerimeterBlocksTable() {
        return perimeterBlocksTable;
    }

    public List<String> getPerimeterBlocksTableListBlockId() {
        return getIdListMap(perimeterBlocksTable);
    }

    public List<String> getAroundBlocksTableListBlockId() {
        return getIdListMap(aroundBlocksTable);
    }
}