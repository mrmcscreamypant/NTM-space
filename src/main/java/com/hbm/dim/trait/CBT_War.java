package com.hbm.dim.trait;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class CBT_War extends CelestialBodyTrait {
	
	//when you engage with one planet or the other, they are at war.
	//both planets will have this file to dictate the engagment.
	//that way they can share generic values to dictate who has what. 
	//grins
	
	//effects are all client side. these variables dictate shit like health and shield or whatevs
	
	public int health;
	public int shield;
	
	public List<Projectile> projectiles;
	
	public CBT_War() {
		this.health = 100;
		this.shield = 0;
		this.projectiles = new ArrayList<>();
	}

	public CBT_War(int health, int shield) {
		this.health = health;
		this.shield = shield;
		this.projectiles = new ArrayList<>();
	}
	
	public void launchProjectile(Projectile proj) {
		projectiles.add(proj);
	}
	public void launchProjectile(float traveltime, int size, int damage) {
		Projectile projectile = new Projectile(traveltime, size, damage);
		
		projectiles.add(projectile);
	}
	
	public void destroyProjectile(Projectile proj) {
		projectiles.remove(proj);
	}

    public List<Projectile> getProjectiles() {
        return projectiles;
    }
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("health", health);
		nbt.setInteger("shield", shield);
		
        NBTTagCompound projectilesTag = new NBTTagCompound();
        for (int i = 0; i < projectiles.size(); i++) {
        	NBTTagCompound projTag = new NBTTagCompound();
            projectiles.get(i).writeToNBT(projTag);
            projectilesTag.setTag("projectile" + i, projTag);
        }
        nbt.setTag("projectiles", projectilesTag);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		shield = nbt.getInteger("shield");
		health = nbt.getInteger("health");
	    NBTTagCompound projectilesTag = nbt.getCompoundTag("projectiles");
	    projectiles = new ArrayList<>();
	    for (int i = 0; projectilesTag.hasKey("projectile" + i); i++) {
	        NBTTagCompound projTag = projectilesTag.getCompoundTag("projectile" + i);
	        Projectile projectile = new Projectile();
	        projectile.readFromNBT(projTag);
	        projectiles.add(projectile);
	    }	
	}

	@Override
	public void writeToBytes(ByteBuf buf) {
		buf.writeInt(health);
		buf.writeInt(shield);
        buf.writeInt(projectiles.size());
        for (Projectile projectile : projectiles) {
            projectile.writeToBytes(buf);
        }
	}

	@Override
	public void readFromBytes(ByteBuf buf) {
		shield = buf.readInt();
		health = buf.readInt();
        int numProjectiles = buf.readInt();
        projectiles = new ArrayList<>(numProjectiles);
        for (int i = 0; i < numProjectiles; i++) {
            Projectile projectile = new Projectile();
            projectile.readFromBytes(buf);
            projectiles.add(projectile);
        }

	}
	public static class Projectile {
		public static float traveltime;
		public static int size;
		public static int damage;
		public static int animtime;
		public static float flashtime;
		public Projectile() {
			
		}
		public Projectile(float traveltime, int size, int damage) {
			this.damage = damage;
			this.size = size;
			this.traveltime = traveltime;
			
		}
		
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setInteger("damage", damage);
            nbt.setFloat("traveltime", traveltime);
            nbt.setInteger("size", size);
        }

        public void readFromNBT(NBTTagCompound nbt) {
            damage = nbt.getInteger("damage");
            traveltime = nbt.getFloat("traveltime");
            size = nbt.getInteger("size");
        }

        public void writeToBytes(ByteBuf buf) {
            buf.writeInt(damage);
            buf.writeFloat(traveltime);
            buf.writeInt(size);
        }

        public void readFromBytes(ByteBuf buf) {
            damage = buf.readInt();
            traveltime = buf.readFloat();
            size = buf.readInt();
        }
        
        public void impact() {
        	if (animtime >= 150) {
				animtime = 0;
        		flashtime = 0;

			} 
        	else if (animtime <= 0 || animtime <= 150) {
        			flashtime += 0.1f;
    				animtime += 1;
    	        	flashtime = Math.min(100.0f, flashtime + 0.1f * (100.0f - flashtime) * 0.15f);
    			}
        	

            System.out.println(flashtime);
        	System.out.println(animtime);
        }
	}

}
