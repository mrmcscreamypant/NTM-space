package com.hbm.dim.tekto;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.block.Block;
import java.util.Random;

import com.hbm.blocks.ModBlocks;

public class TTree extends WorldGenAbstractTree {

	int offset;
	int smallest;
	int tallest;
	int xz;
	int y;
	
    public TTree(boolean notify, int offset, int smallest, int tallest, int xz, int y) {
        super(notify);
        this.offset = offset;
        this.smallest = smallest;
        this.tallest = tallest;
        this.xz = xz;
        this.y = y;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(smallest) + tallest;  

        if (y < 1 || y + height + 1 > 256) {
            return false;
        }

        Block blockBelow = world.getBlock(x, y - 1, z);
        if (blockBelow != ModBlocks.rubber_grass && blockBelow != ModBlocks.rubber_silt) {
            return false;
        }

        // Generate taller trunk
        for (int i = 0; i < height; i++) {
            world.setBlock(x, y + i, z, ModBlocks.pvc_log, 0, 2); 
        }

       
        int bulbStartY = y + height - offset; 
        int bulbRadiusXz = xz;  
        int bulbRadiusY = this.y;  
        
        
        for (int dy = -bulbRadiusY; dy <= bulbRadiusY; dy++) {
            for (int dx = -bulbRadiusXz; dx <= bulbRadiusXz; dx++) {
                for (int dz = -bulbRadiusXz; dz <= bulbRadiusXz; dz++) {
                   
                    if (Math.pow(dx / (double)bulbRadiusXz, 2) + Math.pow(dy / (double)bulbRadiusY, 2) + Math.pow(dz / (double)bulbRadiusXz, 2) <= 1) {
                        Block block = world.getBlock(x + dx, bulbStartY + dy, z + dz);
                        if (block.isAir(world, x + dx, bulbStartY + dy, z + dz)) {
                            world.setBlock(x + dx, bulbStartY + dy, z + dz, ModBlocks.rubber_leaves, 0, 2);  
                        }
                    }
                }
            }
        }

        return true;
    }

}