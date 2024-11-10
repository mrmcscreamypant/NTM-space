package com.hbm.entity.mob;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.World;

public class EntityMoonCow extends EntityCow {

	public EntityMoonCow(World world) {
		super(world);
	}

    public EntityMoonCow createChild(EntityAgeable entity) {
        return new EntityMoonCow(this.worldObj);
    }
	
}
