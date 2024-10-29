package com.hbm.entity.mob;

import com.hbm.entity.mob.ai.EntityAIBehemothGun;
import com.hbm.entity.mob.ai.EntityAIMaskmanMinigun;
import com.hbm.entity.mob.ai.EntityAIStepTowardsTarget;
import com.hbm.entity.mob.ai.EntityAITest;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

public class EntityTankbot extends EntityMob implements IRangedAttackMob {
    private int stepTimer = 0;

    private static final IEntitySelector selector = new IEntitySelector() {
		public boolean isEntityApplicable(Entity p_82704_1_) {
			return !(p_82704_1_ instanceof EntityTankbot);
		}
	};

    public EntityTankbot(World p_i1733_1_)
    {
        super(p_i1733_1_);
        this.setSize(0.75F, 1.35F);
        this.getNavigator().setAvoidsWater(true);
                
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.tasks.addTask(5, new EntityAIWander(this, 0.2D));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, true, selector));
		this.targetTasks.addTask(5, new EntityAIHurtByTarget(this, false));
        this.tasks.addTask(3, new EntityAIStepTowardsTarget(this, 20, 0.2D, 20, 20, 0.6));


    }
	@Override
	protected Entity findPlayerToAttack() {
		if(this.isPotionActive(Potion.blindness)) return null;

		return this.worldObj.getClosestVulnerablePlayerToEntity(this, 128D);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if(source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect) source).getSourceOfDamage() instanceof EntityEgg && rand.nextInt(10) == 0) {
			this.experienceValue = 0;
			this.setHealth(0);
			return true;
		}

		if(source.isFireDamage())
			amount = 0;
		if(source.isMagicDamage())
			amount = 0;
		if(source.isProjectile())
			amount *= 0.25F;
		if(source.isExplosion())
			amount *= 0.5F;

		if(amount > 50) {
			amount = 50 + (amount - 50) * 0.25F;
		}

		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(300.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(140.0D);
	}

    @Override
	protected String getHurtSound()
    {
        return "hbm:entity.cybercrab";
    }
	@Override
	public boolean isAIEnabled() {
		return true;
	}
    @Override
    public void onUpdate() {
        super.onUpdate();
    }
    public double getMaxTargetRange() {
        return 64;
    }
}
