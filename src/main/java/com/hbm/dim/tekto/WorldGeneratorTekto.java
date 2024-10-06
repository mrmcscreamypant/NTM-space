package com.hbm.dim.tekto;

import java.util.Iterator;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.SpaceConfig;
import com.hbm.config.WorldConfig;
import com.hbm.dim.CelestialBody;
import com.hbm.dim.eve.GenLayerEve.WorldGenElectricVolcano;
import com.hbm.dim.eve.GenLayerEve.WorldGenEveSpike;
import com.hbm.dim.eve.biome.BiomeGenBaseEve;
import com.hbm.dim.tekto.biome.BiomeGenBaseTekto;
import com.hbm.world.feature.OilBubble;
import com.hbm.world.generator.DungeonToolbox;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGeneratorTekto implements IWorldGenerator {


	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.dimensionId == SpaceConfig.tektoDimension) {
			generateTekto(world, random, chunkX * 16, chunkZ * 16);
			
		}
	}

	private void generateTekto(World world, Random rand, int cx, int cz) {
		int meta = CelestialBody.getMeta(world);

        int x = cx + rand.nextInt(16);  
        int z = cz + rand.nextInt(16);
        int y = world.getHeightValue(x, z);  
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

	    for (int i = 0; i < 2; i++) {  
			if(biome == BiomeGenBaseTekto.polyvinylPlains) {

	        if (rand.nextInt(10) == 0) {
	            WorldGenAbstractTree customTreeGen = new TTree(false, 4, 2, 10, 2, 4);
	            customTreeGen.generate(world, rand, x, y, z); 
	        	}
			}
	    }
		if(biome == BiomeGenBaseTekto.halogenHills) {
	        	if (rand.nextInt(12) == 0) {
	        		for (int i = 0; i < 4; i++) {  
	        			WorldGenAbstractTree customTreeGen = new TTree(false, 3, 2, 14, 3, 3);
	        			customTreeGen.generate(world, rand, x, y, z); 
	        		}
            }
		}
		if(biome == BiomeGenBaseTekto.forest) {

	    for (int i = 0; i < 8; i++) {
	    int xe = cx + rand.nextInt(16);  
	    int ze = cz + rand.nextInt(16);
	    int ye = world.getHeightValue(xe, ze);  
	        if (rand.nextInt(2) == 0) {
        			WorldGenAbstractTree customTreeGen = new TTree(false, 3, 2, 20, 3, 5);
        			customTreeGen.generate(world, rand, xe, ye, ze); 
           	        	
        			} 
        		else {
        	        	WorldGenAbstractTree tustomTreeGen = new TTree(false, 3, 1, 1, 3, 5);
        	        	tustomTreeGen.generate(world, rand, xe, ye, ze); 
        		}
	    }
		}
	}
}
