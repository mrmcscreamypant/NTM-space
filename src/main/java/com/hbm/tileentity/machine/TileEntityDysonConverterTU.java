package com.hbm.tileentity.machine;

import com.hbm.tileentity.IDysonConverter;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.tile.IHeatSource;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityDysonConverterTU extends TileEntityMachineBase implements IDysonConverter, IHeatSource {

	public long heatEnergy;

    public TileEntityDysonConverterTU() {
        super(0);
    }

    @Override
    public String getName() {
		return "container.machineDysonConverterTU";
    }

    @Override
    public void updateEntity() {
        
    }

    @Override
    public void provideEnergy(int x, int y, int z, long energy) {
        heatEnergy += energy;
        if(heatEnergy < 0) heatEnergy = Long.MAX_VALUE; // prevent overflow
    }

	@Override
	public long maximumEnergy() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getHeatStored() {
        if(heatEnergy > Integer.MAX_VALUE) return Integer.MAX_VALUE;
		return (int)heatEnergy;
	}

	@Override
	public void useUpHeat(int heat) {
		heatEnergy = Math.max(0, heatEnergy - heat);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.heatEnergy = nbt.getLong("heatEnergy");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("heatEnergy", heatEnergy);
	}
    
}
