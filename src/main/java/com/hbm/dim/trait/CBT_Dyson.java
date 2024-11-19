package com.hbm.dim.trait;

import java.util.HashMap;
import java.util.Map.Entry;

import com.hbm.dim.CelestialBody;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class CBT_Dyson extends CelestialBodyTrait {

	// Correlates an ID with a count of swarm members
	private HashMap<Integer, Integer> swarm = new HashMap<>();

	public static void launch(World world, int id) {
		CelestialBody star = CelestialBody.getStar(world);
		CBT_Dyson dyson = star.getTrait(CBT_Dyson.class);
		if(dyson == null) dyson = new CBT_Dyson();

		dyson.swarm.put(id, dyson.swarm.getOrDefault(id, 0) + 1);

		star.modifyTraits(dyson);
	}

	public static int count(World world, int id) {
		CelestialBody star = CelestialBody.getStar(world);
		CBT_Dyson dyson = star.getTrait(CBT_Dyson.class);
		if(dyson == null) return 0;

		return dyson.swarm.getOrDefault(id, 0);
	}

	public int size() {
		int size = 0;
		for(int count : swarm.values()) {
			size += count;
		}
		return size;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		int[] swarmData = new int[swarm.size() * 2];
		int i = 0;
		for(Entry<Integer, Integer> entry : swarm.entrySet()) {
			swarmData[i] = entry.getKey();
			swarmData[i+1] = entry.getValue();
			i += 2;
		}

		nbt.setIntArray("swarm", swarmData);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		int[] swarmData = nbt.getIntArray("swarm");
		swarm = new HashMap<>();
		for(int i = 0; i < swarmData.length; i += 2) {
			swarm.put(swarmData[i], swarmData[i+1]);
		}
	}

	@Override
	public void writeToBytes(ByteBuf buf) {
		buf.writeInt(swarm.size() * 2);
		for(Entry<Integer, Integer> entry : swarm.entrySet()) {
			buf.writeShort(entry.getKey());
			buf.writeInt(entry.getValue());
		}
	}

	@Override
	public void readFromBytes(ByteBuf buf) {
		int count = buf.readInt();
		swarm = new HashMap<>();
		for(int i = 0; i < count; i += 2) {
			int id = buf.readShort();
			int number = buf.readInt();
			swarm.put(id, number);
		}
	}
	
}
