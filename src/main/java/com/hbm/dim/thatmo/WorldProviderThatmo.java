package com.hbm.dim.thatmo;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.SpaceConfig;
import com.hbm.dim.WorldProviderCelestial;
import com.hbm.dim.laythe.SkyProviderLaytheSunset;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class WorldProviderThatmo extends WorldProviderCelestial {

	@Override
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(new BiomeGenThatmo(89), dimensionId);
	}

	@Override
	public String getDimensionName() {
		return "Thatmo";
	}
	
	public static int chargetime;
	public static float flashd;
	public static float altitude;
	public static float randPos;
	public static float scale;
	public static float shield;
	public static float cooldown;
	public static float nmass;
	public static float shielde;
	public static float csyw;
	
	@Override
	public void updateWeather() {
		super.updateWeather();
		if(worldObj.isRemote) {
        if (chargetime <= 0 || chargetime <= 600) {
            chargetime += 1;
            flashd = 0;
        } else if (chargetime >= 100) {
            flashd += 0.3f;
            flashd = Math.min(100.0f, flashd + 0.3f * (100.0f - flashd) * 0.15f);

            if (flashd <= 5) {
                Minecraft.getMinecraft().thePlayer.playSound("hbm:misc.fireflash", 10F, 1F);
            }

            if (flashd >= 100) {
                chargetime = 0;
            }
        }
	    if(chargetime == 285) {
            Minecraft.getMinecraft().thePlayer.playSound("hbm:misc.cenimp", 10F, 1F);
    	}
        if ( chargetime >= 300 &&chargetime <= 430) {
    	if (scale <= 0 || scale <= 20) {
    	    scale += 1.5;
    		}
        

    	if (scale >= 20) {
    	    scale = 20; 
    	    shield += 0.4f; 
    	    shield = Math.min(25.0f, shield + 0.5f * (25.0f - shield) * 0.15f);	        	
    	    nmass = Math.min(180.0f, nmass + 0.2f * (180.0f - nmass) * 0.15f);
    	    shielde = Math.min(15.0f, shielde + 0.6f * (15.0f - shielde) * 0.10f);	   
    	    csyw += 0.2f;
    	    csyw = Math.min(100.0f, csyw + 0.2f * (100.0f - csyw) * 0.15f);

    	    }

    	if (shield > 0) {
    	    if (cooldown <= 0 || cooldown <= 60) {
    	        cooldown += 0.5;
    	    }

    	    if (cooldown >= 60) {
    	        cooldown = 0;
    	        shield = 0; 
    	        scale = 0;  
    	        nmass = 0;
    	        shielde = 0;
    	        csyw = 0;
    	    }
    	}
        }else {
	        cooldown = 0;
	        shield = 0; 
	        scale = 0;  
	        nmass = 0;
	        shielde = 0;
	        csyw = 0;
		}
        if (altitude <= 400) {
            altitude += 5;
        }
        if (altitude >= 400) {
            altitude = 0;
            randPos = Minecraft.getMinecraft().theWorld.rand.nextFloat();
        }
	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getSkyColor(Entity camera, float partialTicks) {
		Vec3 ohshit = super.getSkyColor(camera, partialTicks);
        float xalpha = (csyw <= 0) ? 0.0F : 0.5F - Math.min(0.5F, csyw / 100);   
        float alpha = (flashd <= 0) ? 0.0F : 1.0F - Math.min(1.0F, flashd / 100);

		return Vec3.createVectorHelper(ohshit.xCoord + alpha + xalpha , ohshit.yCoord + alpha + xalpha, ohshit.zCoord + alpha + xalpha);
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float x, float y) {
		Vec3 ohshit = super.getFogColor(x, y);
        float xalpha = (csyw <= 0) ? 0.0F : 0.5F - Math.min(0.5F, csyw / 100);   
        float alpha = (flashd <= 0) ? 0.0F : 1.0F - Math.min(1.0F, flashd / 100);

		return Vec3.createVectorHelper(ohshit.xCoord + alpha + xalpha , ohshit.yCoord + alpha + xalpha, ohshit.zCoord + alpha + xalpha);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		return new SkyProviderThatmo();
	}
	
	@Override
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderThatmo(this.worldObj, this.getSeed(), false);
	}

	@Override
	public Block getStone() {
		return ModBlocks.sellafield_slaked;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float par1) {
		float imsuper = super.getSunBrightness(par1);
        float xalpha = (csyw <= 0) ? 0.0F : 0.5F - Math.min(0.5F, csyw / 100);   
        float alpha = (flashd <= 0) ? 0.0F : 1.0F - Math.min(1.0F, flashd / 100);
		return imsuper * 0.1F + alpha + xalpha;
	}

}
