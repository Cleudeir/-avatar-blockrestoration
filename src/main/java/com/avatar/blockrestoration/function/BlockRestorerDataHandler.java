package com.avatar.blockrestoration.function;

import java.io.*;
import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRestorerDataHandler {
    private static final String DATA_FILE = "block_restorer_data.dat";

    public static void saveData(Map<BlockPos, BlockState> brokenBlocks,
            Map<BlockPos, BlockState> aroundBlocksTable,
            List<BlockPos> playerBrokenBlocks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(brokenBlocks);
            oos.writeObject(aroundBlocksTable);
            oos.writeObject(playerBrokenBlocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadData(Map<BlockPos, BlockState> brokenBlocks,
            Map<BlockPos, BlockState> aroundBlocksTable,
            List<BlockPos> playerBrokenBlocks) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            brokenBlocks.putAll((Map<BlockPos, BlockState>) ois.readObject());
            aroundBlocksTable.putAll((Map<BlockPos, BlockState>) ois.readObject());
            playerBrokenBlocks.addAll((List<BlockPos>) ois.readObject());
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
