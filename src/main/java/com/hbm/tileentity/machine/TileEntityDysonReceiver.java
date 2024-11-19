package com.hbm.tileentity.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.dim.trait.CBT_Dyson;
import com.hbm.tileentity.IDysonConverter;
import com.hbm.tileentity.TileEntityMachineBase;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityDysonReceiver extends TileEntityMachineBase {

	// Connects to a dyson swarm via ID, receiving energy during the day
	// also receives energy at night if a satellite relay is in orbit around the planet

	// The energy received is fired as a violently powerful beam,
	// converters can collect this beam and turn it into HE/TU or used for analysis, crafting, etc.

	public TileEntityDysonReceiver() {
		super(0);
	}

	@Override
	public String getName() {
		return "container.machineDysonReceiver";
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);

			int swarmId = 12345;

			int swarmCount = CBT_Dyson.count(worldObj, swarmId);

			if(swarmCount > 0) {
				for(int i = 3; i < 24; i++) {
					int x = xCoord + dir.offsetX * i;
					int y = yCoord;
					int z = zCoord + dir.offsetZ * i;

					Block block = worldObj.getBlock(x, y, z);
					
					TileEntity te;
					if(block instanceof BlockDummyable) {
						int[] pos = ((BlockDummyable) block).findCore(worldObj, x, y, z);
						te = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
					} else {
						te = worldObj.getTileEntity(x, y, z);
					}

					if(te instanceof IDysonConverter) {
						((IDysonConverter) te).provideEnergy(x, y, z, swarmCount);
						break;
					}
				}
			}
		}
	}
	
}
