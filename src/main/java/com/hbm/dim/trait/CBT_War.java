package com.hbm.dim.trait;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import scala.inline;

public class CBT_War extends CelestialBodyTrait {
	
	//when you engage with one planet or the other, they are at war.
	//both planets will have this file to dictate the engagment.
	//that way they can share generic values to dictate who has what. 
	//grins
	
	//effects are all client side. these variables dictate shit like health and shield or whatevs
	
	public int health;
	public int shield;

	
	public CBT_War() {
		this.health = 100;
		this.shield = 0;
	}

	public CBT_War(int health, int shield) {
		this.health = health;
		this.shield = shield;
	}
	

	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("health", health);
		nbt.setInteger("shield", shield);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		shield = nbt.getInteger("shield");
		health = nbt.getInteger("health");

	}

	@Override
	public void writeToBytes(ByteBuf buf) {
		buf.writeInt(health);
		buf.writeInt(shield);

	}

	@Override
	public void readFromBytes(ByteBuf buf) {
		shield = buf.readInt();
		health = buf.readInt();

	}

}
