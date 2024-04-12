package com.hbm.entity.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import com.hbm.entity.effect.EntityNukeTorex2.Cloudlet;
//import com.hbm.entity.effect.EntityNukeTorex2.TorexType;
//import com.hbm.entity.effect.EntityNukeTorex.TorexType;
//import com.hbm.entity.effect.EntityNukeTorex.Cloudlet;
//import com.hbm.entity.effect.EntityNukeTorex.TorexType;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.items.ModItems;
import com.hbm.lib.ModDamageSource;
import com.hbm.util.TrackerUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBlackHole extends Entity {
	
	Random rand = new Random();
	public double coreHeight = 3;
	public double convectionHeight = 3;
	public double torusWidth = 3;
	public double rollerSize = 1;
	public double heat = 1;
	public double lastSpawnY = - 1;
	public ArrayList<Cloudlet> cloudlets = new ArrayList();
	public EntityBlackHole(World p_i1582_1_) {
		super(p_i1582_1_);
		this.ignoreFrustumCheck = true;
		this.isImmuneToFire = true;
		this.noClip = true;
	}

	public EntityBlackHole(World world, float size) {
		this(world);
		this.dataWatcher.updateObject(16, size);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		double cs = 1.5;
		float size = this.dataWatcher.getWatchableObjectFloat(16);
		
		if(worldObj.isRemote) {
			
			if(lastSpawnY == -1) {
				lastSpawnY = posY - 3;
			}
			
			int spawnTarget = Math.max(worldObj.getHeightValue((int) Math.floor(posX), (int) Math.floor(posZ)) - 3, 1);
			double moveSpeed = 0.5D;
			
			if(Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
				lastSpawnY = spawnTarget;
			} else {
				lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
			}
			
			// spawn mush clouds
			double range = (torusWidth - rollerSize) * 0.25;
			//double simSpeed = getSimulationSpeed();
			//int toSpawn = (int) Math.ceil(10 * simSpeed * simSpeed);
			//int lifetime = Math.min((ticksExisted * ticksExisted) + 200, maxAge - ticksExisted + 200);
			// spawn ring clouds
			//if(ticksExisted < 200) {
				for(int i = 0; i < 20; i++) {
					double x = posX+(rand.nextDouble()*60D)-30D;
					double z = posZ+(rand.nextDouble()*60D)-30D;
					double distSq = Math.pow((x-posX), 2)+Math.pow((z-posZ), 2);
					//distSq /= 2;
					double dist = Math.sqrt(distSq);
					if(dist>30)
					{
						i=i-1;
					}
					if(dist<=30)
					{
						Cloudlet cloud = new Cloudlet(x, posY, z, (float)(rand.nextDouble() * 2D * Math.PI), 0, 200, TorexType.RING);
						cloud.setScale(1F, 1f);
						cloudlets.add(cloud);
					}
					//System.out.println("dist: "+dist);
				}
			//}
			
			for(Cloudlet cloud : cloudlets) {
				cloud.update();
			}
			coreHeight += 0.15/* * s*/;
			torusWidth += 0.05/* * s*/;
			rollerSize = torusWidth * 0.35;
			convectionHeight = coreHeight + rollerSize;
			
			//int maxHeat = (int) (50 * s);
			//heat = maxHeat - Math.pow((maxHeat * this.ticksExisted) / maxAge, 1);
			
			cloudlets.removeIf(x -> x.isDead);
		}
		
		/*if(!worldObj.isRemote) {
			for(int k = 0; k < size * 2; k++) {
				double phi = rand.nextDouble() * (Math.PI * 2);
				double costheta = rand.nextDouble() * 2 - 1;
				double theta = Math.acos(costheta);
				double x = Math.sin( theta) * Math.cos( phi );
				double y = Math.sin( theta) * Math.sin( phi );
				double z = Math.cos( theta );
				
				Vec3 vec = Vec3.createVectorHelper(x, y, z);
				int length = (int)Math.ceil(size * 15);
				
				for(int i = 0; i < length; i ++) {
					int x0 = (int)(this.posX + (vec.xCoord * i));
					int y0 = (int)(this.posY + (vec.yCoord * i));
					int z0 = (int)(this.posZ + (vec.zCoord * i));
					
					if(worldObj.getBlock(x0, y0, z0).getMaterial().isLiquid()) {
						worldObj.setBlock(x0, y0, z0, Blocks.air);
					}
					
					if(worldObj.getBlock(x0, y0, z0) != Blocks.air) {
						EntityRubble rubble = new EntityRubble(worldObj);
						rubble.posX = x0 + 0.5F;
						rubble.posY = y0;
						rubble.posZ = z0 + 0.5F;
						rubble.setMetaBasedOnBlock(worldObj.getBlock(x0, y0, z0), worldObj.getBlockMetadata(x0, y0, z0));
						
						worldObj.spawnEntityInWorld(rubble);
					
						worldObj.setBlock(x0, y0, z0, Blocks.air);
						break;
					}
				}
			}
		}*/
		
		double range = size * 25;
		
		List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(
				posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range));
		
		for(Entity e : entities) {
			
			if(e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode)
				continue;
			
			if(e instanceof EntityFallingBlock && !worldObj.isRemote && e.ticksExisted > 1) {
				
				double x = e.posX;
				double y = e.posY;
				double z = e.posZ;
				Block b = ((EntityFallingBlock)e).func_145805_f();
				int meta = ((EntityFallingBlock)e).field_145814_a;
				
				e.setDead();
				
				EntityRubble rubble = new EntityRubble(worldObj);
				rubble.setMetaBasedOnBlock(b, meta);
				rubble.setPositionAndRotation(x, y, z, 0, 0);
				rubble.motionX = e.motionX;
				rubble.motionY = e.motionY;
				rubble.motionZ = e.motionZ;
				worldObj.spawnEntityInWorld(rubble);
			}
			
			Vec3 vec = Vec3.createVectorHelper(posX - e.posX, posY - e.posY, posZ - e.posZ);
			
			double dist = vec.lengthVector();
			
			if(dist > range)
				continue;
			
			vec = vec.normalize();
			
			if(!(e instanceof EntityItem))
				vec.rotateAroundY((float)Math.toRadians(15));
			
			double speed = 0.1D;
			e.motionX += vec.xCoord * speed;
			e.motionY += vec.yCoord * speed * 2;
			e.motionZ += vec.zCoord * speed;
			
			if(e instanceof EntityBlackHole)
				continue;
			
			if(dist < size * 1.5) {
				e.attackEntityFrom(ModDamageSource.blackhole, 1000);
				
				if(!(e instanceof EntityLivingBase))
					e.setDead();
				
				if(!worldObj.isRemote && e instanceof EntityItem) {
					EntityItem item = (EntityItem) e;
					ItemStack stack = item.getEntityItem();
					
					if(stack.getItem() == ModItems.pellet_antimatter || stack.getItem() == ModItems.flame_pony) {
						this.setDead();
						worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 5.0F, true);
						return;
					}
				}
			}
		}
		
		this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		
		this.motionX *= 0.99D;
		this.motionY *= 0.99D;
		this.motionZ *= 0.99D;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(16, 0.5F);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.dataWatcher.updateObject(16, nbt.getFloat("size"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("size", this.dataWatcher.getWatchableObjectFloat(16));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 25000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}

	@Override
	public float getBrightness(float p_70013_1_) {
		return 1.0F;
	}
	
	public class Cloudlet {

		public double posX;
		public double posY;
		public double posZ;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;
		public double motionX;
		public double motionY;
		public double motionZ;
		public int age;
		public int cloudletLife;
		public float angle;
		public boolean isDead = false;
		float rangeMod = 1.0F;
		public float colorMod = 1.0F;
		public Vec3 color;
		public Vec3 prevColor;
		public TorexType type;
		public Vec3 swirl;
		
		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
			this(posX, posY, posZ, angle, age, maxAge, TorexType.RING);
		}

		public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.age = age;
			this.cloudletLife = maxAge;
			this.angle = angle;
			this.rangeMod = 0.3F + rand.nextFloat() * 0.7F;
			this.colorMod = 0.8F + rand.nextFloat() * 0.2F;
			this.type = type;
			
			this.updateColor();
		}
		
		private void update() {
			
			age++;
			
			/*if(age > cloudletLife) {
				this.isDead = true;
			}*/

			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			
			Vec3 simPos = Vec3.createVectorHelper(EntityBlackHole.this.posX - this.posX, 0, EntityBlackHole.this.posZ - this.posZ);
			double simPosX = EntityBlackHole.this.posX + simPos.lengthVector();
			double simPosZ = EntityBlackHole.this.posZ + 0D;
			
			if(simPos.lengthVector()<2f)
			{
				this.isDead = true;
			}
			
			if(this.type == TorexType.STANDARD) {
				Vec3 convection = getConvectionMotion(simPosX, simPosZ);
				Vec3 lift = getLiftMotion(simPosX, simPosZ);
				
				double factor = MathHelper.clamp_double((this.posY - EntityBlackHole.this.posY) / EntityBlackHole.this.coreHeight, 0, 1);
				this.motionX = convection.xCoord * factor + lift.xCoord * (1D - factor);
				this.motionY = convection.yCoord * factor + lift.yCoord * (1D - factor);
				this.motionZ = convection.zCoord * factor + lift.zCoord * (1D - factor);
			} else if(this.type == TorexType.RING) {
				double x2 = (this.posX - EntityBlackHole.this.posX);
				double z2 = (this.posZ - EntityBlackHole.this.posZ);
				Vec3 suck = Vec3.createVectorHelper((x2)/5, 0, (z2)/5);
				Vec3 motion = getRingMotion(simPosX, simPosZ);
				suck.rotateAroundY(90);
				this.swirl = suck;
				//suck.normalize();
				//suck.crossProduct(Vec3.createVectorHelper(0.01f, 0.1f, 0.01f));
				this.motionX = suck.xCoord/suck.lengthVector();
				this.motionY = suck.yCoord/suck.lengthVector();
				this.motionZ = suck.zCoord/suck.lengthVector();
			}
			
			double mult = this.motionMult;
			
			this.posX += this.motionX * mult;
			this.posY += this.motionY * mult;
			this.posZ += this.motionZ * mult;
			
			this.updateColor();
		}
		
		private Vec3 getRingMotion(double simPosX, double simPosZ) {
			
			/*Vec3 targetPos = Vec3.createVectorHelper(
					(EntityNukeTorex.this.posX + torusWidth * 1),
					(EntityNukeTorex.this.posY + coreHeight * 0.5),
					EntityNukeTorex.this.posZ);
			
			Vec3 delta = Vec3.createVectorHelper(targetPos.xCoord - simPosX, targetPos.yCoord - this.posY, targetPos.zCoord - simPosZ);
			
			double speed = 0.125D;
			delta.xCoord *= speed;
			delta.yCoord *= speed;
			delta.zCoord *= speed;
			
			delta.rotateAroundY(this.angle);
			return delta;*/
			
			if(simPosX > EntityBlackHole.this.posX + torusWidth * 2)
				return Vec3.createVectorHelper(0, 0, 0);
			
			/* the position of the torus' outer ring center */
			Vec3 torusPos = Vec3.createVectorHelper(
					(EntityBlackHole.this.posX + torusWidth),
					(EntityBlackHole.this.posY),
					EntityBlackHole.this.posZ);
			
			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = Vec3.createVectorHelper(torusPos.xCoord - simPosX, torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = EntityBlackHole.this.rollerSize * this.rangeMod * 0.25;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = Vec3.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist, -delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundY(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = Vec3.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			double speed = 0.001D;
			motion.xCoord *= speed;
			motion.yCoord *= speed;
			motion.zCoord *= speed;
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);
			
			return motion;
		}
		
		/* simulated on a 2D-plane along the X/Y axis */
		private Vec3 getConvectionMotion(double simPosX, double simPosZ) {
			
			if(simPosX > EntityBlackHole.this.posX + torusWidth * 2)
				return Vec3.createVectorHelper(0, 0, 0);
			
			/* the position of the torus' outer ring center */
			Vec3 torusPos = Vec3.createVectorHelper(
					(EntityBlackHole.this.posX + torusWidth),
					(EntityBlackHole.this.posY + coreHeight),
					EntityBlackHole.this.posZ);
			
			/* the difference between the cloudlet and the torus' ring center */
			Vec3 delta = Vec3.createVectorHelper(torusPos.xCoord - simPosX, torusPos.yCoord - this.posY, torusPos.zCoord - simPosZ);
			
			/* the distance this cloudlet wants to achieve to the torus' ring center */
			double roller = EntityBlackHole.this.rollerSize * this.rangeMod;
			/* the distance between this cloudlet and the torus' outer ring perimeter */
			double dist = delta.lengthVector() / roller - 1D;
			
			/* euler function based on how far the cloudlet is away from the perimeter */
			double func = 1D - Math.pow(Math.E, -dist); // [0;1]
			/* just an approximation, but it's good enough */
			float angle = (float) (func * Math.PI * 0.5D); // [0;90°]
			
			/* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
			Vec3 rot = Vec3.createVectorHelper(-delta.xCoord / dist, -delta.yCoord / dist, -delta.zCoord / dist);
			/* rotate by the approximate angle */
			rot.rotateAroundZ(angle);
			
			/* the direction from the cloudlet to the target position on the perimeter */
			Vec3 motion = Vec3.createVectorHelper(
					torusPos.xCoord + rot.xCoord - simPosX,
					torusPos.yCoord + rot.yCoord - this.posY,
					torusPos.zCoord + rot.zCoord - simPosZ);
			
			motion = motion.normalize();
			motion.rotateAroundY(this.angle);
			
			return motion;
		}
		
		private Vec3 getLiftMotion(double simPosX, double simPosZ) {
			double scale = MathHelper.clamp_double(1D - (simPosX - (EntityBlackHole.this.posX + torusWidth)), 0, 1);
			
			Vec3 motion = Vec3.createVectorHelper(EntityBlackHole.this.posX - this.posX, (EntityBlackHole.this.posY + convectionHeight) - this.posY, EntityBlackHole.this.posZ - this.posZ);
			
			motion = motion.normalize();
			motion.xCoord *= scale;
			motion.yCoord *= scale;
			motion.zCoord *= scale;
			
			return motion;
		}
		
		private void updateColor() {
			this.prevColor = this.color;

			double exX = EntityBlackHole.this.posX;
			double exY = EntityBlackHole.this.posY;
			double exZ = EntityBlackHole.this.posZ;

			double distX = exX - posX;
			double distY = exY - posY;
			double distZ = exZ - posZ;
			
			double distSq = distX * distX + distY * distY + distZ * distZ;
			distSq /= 4;
			double dist = Math.sqrt(distSq);
			
			dist = Math.max(dist, 1);
			double col = 2D / dist;
			
		//	int type = EntityBlackHole.this.dataWatcher.getWatchableObjectInt(11);
			
			//System.out.println(rel);
			/*if(type == 1) {
					this.color = Vec3.createVectorHelper(
							Math.max(col * 2, 0.25),
							Math.max(col * 0.5, 0.25),
							Math.max(col * 1.5, 0.25)
							);
			} 
			if(type == 2) {
				this.color = Vec3.createVectorHelper(
						Math.max(col * 2, 0.25),
						Math.max(col * 0.5, 0.25),
						Math.max(col * 1.5, 0.25)
						);
			}else {*/
				this.color = Vec3.createVectorHelper(
						Math.max(col * 2, (144F/255F)),
						Math.max(col * 1.5, (14F/255F)),
						Math.max(col * 0.5, 0)
						);
				//System.out.println(rel);
			//}
		}
		
		public Vec3 getInterpPos(float interp) {
			return Vec3.createVectorHelper(
					prevPosX + (posX - prevPosX) * interp,
					prevPosY + (posY - prevPosY) * interp,
					prevPosZ + (posZ - prevPosZ) * interp);
		}
		
		public Vec3 getInterpColor(float interp) {
			//double greying = EntityNukeTorex2.this.getGreying();
			
			/*if(this.type == TorexType.RING) {
				greying += 1;
			}*/
			
			return Vec3.createVectorHelper(
					(prevColor.xCoord + (color.xCoord - prevColor.xCoord) * interp) * 1,
					(prevColor.yCoord + (color.yCoord - prevColor.yCoord) * interp) * 1,
					(prevColor.zCoord + (color.zCoord - prevColor.zCoord) * interp) * 1);
		}
		
		public float getAlpha() {
			return 1;//(1F - ((float)age / (float)cloudletLife)) * EntityNukeTorex2.this.getAlpha();
		}
		
		private float startingScale = 1;
		private float growingScale = 0;
		
		public float getScale() {
			return startingScale;
		}
		
		public Cloudlet setScale(float start, float grow) {
			this.startingScale = start;
			this.growingScale = grow;
			return this;
		}
		
		private double motionMult = 1F;
		
		public Cloudlet setMotion(double mult) {
			this.motionMult = mult;
			return this;
		}
	}
	
	public static enum TorexType {
		STANDARD,
		RING
	}
}
