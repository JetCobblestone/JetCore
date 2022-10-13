package me.jetcobblestone.game.map;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {
    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        return createChunkData(world);
    }
}
