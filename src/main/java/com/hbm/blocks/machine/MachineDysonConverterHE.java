package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityDysonConverterHE;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MachineDysonConverterHE extends BlockDummyable {

	public MachineDysonConverterHE(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if(meta >= 12) return new TileEntityDysonConverterHE();
		if(meta >= 6) return new TileEntityProxyCombo(false, false, false);
		return null;
	}

	@Override
	public int[] getDimensions() {
		return new int[] {0, 0, 0, 0, 0, 0};
	}

	@Override
	public int getOffset() {
		return 0;
	}

	//. TEMP .//
	@Override
	public int getRenderType() {
		return 0;
	}
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
	//. TEMP .//
	
}
