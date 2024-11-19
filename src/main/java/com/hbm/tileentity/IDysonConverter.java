package com.hbm.tileentity;

public interface IDysonConverter {
	
	// Attempt to provide power at a given position on the converter
	public void provideEnergy(int x, int y, int z, long energy);

}
