package com.hbm.tileentity.machine;

import com.hbm.dim.trait.CBT_Dyson;
import com.hbm.items.ModItems;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.energymk2.IEnergyReceiverMK2;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityDysonLauncher extends TileEntityMachineBase implements IEnergyReceiverMK2 {

	private long power;
	private long maxPower = 1_000_000;

	public TileEntityDysonLauncher() {
		super(2);
	}

	@Override
	public String getName() {
		return "container.machineDysonLauncher";
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) trySubscribe(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);

			if(power == maxPower && slots[0] != null && slots[0].getItem() == ModItems.swarm_member) {
				int swarmId = 12345;

				CBT_Dyson.launch(worldObj, swarmId);

				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "hbm:misc.spinshot", 4.0F, 0.9F + worldObj.rand.nextFloat() * 0.3F);
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "hbm:misc.spinshot", 4.0F, 1F + worldObj.rand.nextFloat() * 0.3F);

				slots[0].stackSize--;
				power = 0;
			
				if(slots[0].stackSize <= 0) slots[0] = null;
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(slot == 0) return itemStack.getItem() == ModItems.swarm_member;
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {0};
	}

	@Override public long getPower() { return power; }
	@Override public void setPower(long power) { this.power = power; }
	@Override public long getMaxPower() { return maxPower;}
	
}
