package com.hbm.tileentity;

public interface IDysonConverter {
	
	// Attempt to provide power at a given position on the converter
	public void provideEnergy(int x, int y, int z, long energy);

	// Tell Dyson receivers our energy conversion limit
	public long maximumEnergy();

}
