package com.hbm.tileentity.machine;

import com.hbm.tileentity.IDysonConverter;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.energymk2.IEnergyProviderMK2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityDysonConverterHE extends TileEntityMachineBase implements IDysonConverter, IEnergyProviderMK2 {

	public long power;

	public TileEntityDysonConverterHE() {
		super(0);
	}

	@Override
	public String getName() {
		return "container.machineDysonConverterHE";
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				tryProvide(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
			}

			// To prevent this machine acting like an endgame battery, but still be able to transmit every drop of power
			// this machine will clear its buffers immediately after transmitting power
			power = 0;
		}
	}

	@Override
	public void provideEnergy(int x, int y, int z, long energy) {
		power += energy;
		if(power < 0) power = Long.MAX_VALUE; // prevent overflow
	}

	@Override
	public long maximumEnergy() {
		return Long.MAX_VALUE;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public long getMaxPower() {
		return Long.MAX_VALUE;
	}
	
}
