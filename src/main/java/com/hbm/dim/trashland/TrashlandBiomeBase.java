
package com.hbm.dim.trashland;

import java.util.Random;

import com.hbm.config.SpaceConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class TrashlandBiomeBase extends BiomeGenBase
{
    public static final BiomeGenBase dunaPolar = new BiomeGenTrashHills(127).setTemperatureRainfall(-1.0F, 0.0F);
    public static final BiomeGenBase dunaHills = new BiomeGenTrashianLowlands(128).setTemperatureRainfall(-1.0F, 0.0F);
    public static final BiomeGenBase dunaPolarHills = new BiomeGenBaseTrash(98).setTemperatureRainfall(-1.0F, 0.0F);
    
    public TrashlandBiomeBase(int id)
    {
        super(id);
    }
}