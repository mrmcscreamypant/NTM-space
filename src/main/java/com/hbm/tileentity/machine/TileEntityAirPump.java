package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.gas.BlockGasAir;
import com.hbm.config.GeneralConfig;
import com.hbm.entity.mob.EntityCyberCrab;
import com.hbm.entity.mob.EntityTaintCrab;
import com.hbm.entity.mob.EntityTeslaCrab;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.INBTPacketReceiver;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.ArmorUtil;
import com.hbm.util.fauxpointtwelve.BlockPos;

import api.hbm.fluid.IFluidStandardReceiver;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

public class TileEntityAirPump extends TileEntityMachineBase implements IFluidStandardReceiver, INBTPacketReceiver {
	 private World customWorld;
	public static int flucue = 100;
	public int onTicks = 0;
	public static int range = 20;
	public static double offset = 1.75;
	public List<double[]> targets = new ArrayList();
	private boolean needsRevalidate = false;
	AxisAlignedBB sealedRoomAABB; 
	public FluidTank tank;

	public TileEntityAirPump() {
		super(1);
		tank = new FluidTank(Fluids.OXYGEN, 16000);
	}
	
	
	public void spawnParticles() {

			if(worldObj.getTotalWorldTime() % 2 == 0) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("type", "tower");
			data.setFloat("lift", 0.1F);
			data.setFloat("base", 0.3F);
			data.setFloat("max", 1F);
			data.setInteger("life", 20 + worldObj.rand.nextInt(20));
			data.setInteger("color",0x98bdf9);

			data.setDouble("posX", xCoord + 0.5 + worldObj.rand.nextDouble() - 0.5);
			data.setDouble("posZ", zCoord + 0.5 + worldObj.rand.nextDouble() -0.5);
			data.setDouble("posY", yCoord + 1);
			
			MainRegistry.proxy.effectNT(data);

		}
	}

	
	@Override
	public void updateEntity() {
		
		if(!worldObj.isRemote) {
			if(worldObj.getBlock(xCoord, yCoord+1, zCoord).isAir(worldObj, xCoord, yCoord+1, zCoord)) {
			this.updateConnections();
			if(onTicks > 0) onTicks--;
			this.targets.clear();
						
			if(tank.getFill() > 0) {
				onTicks = 20;


				tank.setFill(tank.getFill() - 10);

				double dx = xCoord + 0.5;
				double dy = yCoord + offset;
				double dz = zCoord + 0.5;

				revalidateRoom();  
				
			}else {
	        	worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
	        	findRoomSections(worldObj, xCoord, yCoord, zCoord);
				}
	        }

			NBTTagCompound data = new NBTTagCompound();
			data.setShort("length", (short)targets.size());
			data.setInteger("onTicks", onTicks);
			tank.writeToNBT(data, "at");
			int i = 0;
			for(double[] d : this.targets) {
				data.setDouble("x" + i, d[0]);
				data.setDouble("y" + i, d[1]);
				data.setDouble("z" + i, d[2]);
				i++;
			}

			this.networkPack(data, 100);
			
		}
		else{
			if(onTicks > 0) {
				this.spawnParticles();
			}
		}
	}
	
		
		
		
	public AxisAlignedBB getSealedRoomAABB() {
	    return sealedRoomAABB;
	}

	public void setNeedsRevalidate(boolean flag) {
	    needsRevalidate = flag;
	    System.out.print("SEVENT RETURNED " + needsRevalidate);
	}

	public void revalidateRoom() {
	    findRoomSections(worldObj, xCoord, yCoord, zCoord);
	}
	

	private void updateConnections() {
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			this.trySubscribe(Fluids.OXYGEN, worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
	}
	
	public static List<double[]> zap(World worldObj, double x, double y, double z, double radius, Entity source) {

		List<double[]> ret = new ArrayList();
		
		List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));
		
		for(EntityLivingBase e : targets) {
			
			Vec3 vec = Vec3.createVectorHelper(e.posX - x, e.posY + e.height / 2 - y, e.posZ - z);
			
			if(vec.lengthVector() > range)
				continue;
			
			HbmLivingProps.SsetOxy(e, 20);
			ret.add(new double[] {e.posX, e.posY + e.height / 2 - offset, e.posZ});
		}
		
		return ret;
	}
	private void reset(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
	    // First, convert all mod air blocks back to normal air
	    Iterator<BlockPos> it = globalAirBlocks.iterator();
	    while (it.hasNext()) {
	        BlockPos pos = it.next();
	        if (world.getBlock(pos.getX(), pos.getY(), pos.getZ()) == ModBlocks.air_block) {
	            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.air);
	        }
	    }
	    globalAirBlocks.clear(); // Clear the global air blocks set


	}
	private void revalidateTheRoom(World world, Set<BlockPos> air) {
	    // Iterate through all positions that need to be validated as room air blocks
	    for (BlockPos pos : air) {
	        if (world.getBlock(pos.getX(), pos.getY(), pos.getZ()).isAir(world, pos.getX(), pos.getY(), pos.getZ())) {
	            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), ModBlocks.air_block);
	            globalAirBlocks.add(pos); // Track the position globally
	        }
	    }
	}
	private void updateAirBlocks(World world, Set<BlockPos> air, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		reset(world, minX, maxX, minY, maxY, minZ, maxZ);
	    revalidateTheRoom(world, air); // Ensure that the air set is properly validated and updated
	}
	//TODO: Rewrite this fucking mess of a class
	private Set<BlockPos> globalAirBlocks = new HashSet<>();

	private void findRoomSections(World world, int startX, int startY, int startZ) {
	    Set<BlockPos> visited = new HashSet<>();
	    Set<BlockPos> air = new HashSet<>();
	    Set<BlockPos> poweredTileEntities = new HashSet<>();

	    Stack<BlockPos> stack = new Stack<>();

	    stack.push(new BlockPos(startX, startY, startZ));

	    int minX = startX, minY = startY, minZ = startZ;
	    int maxX = startX, maxY = startY, maxZ = startZ;

	    while (!stack.isEmpty()) {
	        BlockPos current = stack.pop();

	        if (Math.abs(maxX - minX) > MAX_RANGE_X || Math.abs(maxY - minY) > MAX_RANGE_Y || Math.abs(maxZ - minZ) > MAX_RANGE_Z) {
	            continue;
	        }

	        if (!visited.contains(current)) {
	            visited.add(current);

	            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
	                BlockPos neighbor = current.offset(dir);
	                Block block = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());

	                if (block.isAir(world, neighbor.getX(), neighbor.getY(), neighbor.getZ()) || block == ModBlocks.vacuum) {
	                    air.add(neighbor);
	                    stack.push(neighbor);
	                    minX = Math.min(minX, neighbor.getX());
	                    minY = Math.min(minY, neighbor.getY());
	                    minZ = Math.min(minZ, neighbor.getZ());
	                    maxX = Math.max(maxX, current.getX() + 1);
	                    maxY = Math.max(maxY, current.getY() + 1);
	                    maxZ = Math.max(maxZ, current.getZ() + 1);
	                }
	                
	            }
	        }

	        if (stack.isEmpty() && !visited.isEmpty()) {

	            updateAirBlocks(world, air, minX, maxX, minY, maxY, minZ, maxZ);
	            minX = maxX = current.getX();
	            minY = maxY = current.getY();
	            minZ = maxZ = current.getZ();
	            
	        }
	    }

	    if (tank.getFill() <= 0) {
	        Iterator<BlockPos> it = globalAirBlocks.iterator();
	        while (it.hasNext()) {
	            BlockPos pos = it.next();
	            if (world.getBlock(pos.getX(), pos.getY(), pos.getZ()) == ModBlocks.air_block) {
	                world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.air);
	                it.remove(); // Remove from global set once reset to air
	            }
	        }
	    }
	}
	/*
	private void mergeTileEntityRanges(Set<BlockPos> visited, BlockPos current, World world, Set<BlockPos> air, Set<BlockPos> poweredTileEntities) {
	    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
	        BlockPos neighbor = current.offset(dir);
	        TileEntity neighborTileEntity = world.getTileEntity(neighbor.getX(), neighbor.getY(), neighbor.getZ());
			TileEntity tile = world.getTileEntity(neighbor.getX(), neighbor.getY(), neighbor.getZ());
	        if (neighborTileEntity != null && !visited.contains(neighbor) && world.getTileEntity(neighbor.getX(), neighbor.getY(), neighbor.getZ()) instanceof TileEntityAirPump) {
				TileEntityAirPump tEntityAirPump = (TileEntityAirPump) tile;
				
	            visited.add(neighbor);
	            if (tEntityAirPump.tank.getFill() > 0) {
	                poweredTileEntities.add(neighbor); // Add powered tile entity to set
	            } else {
	                air.add(neighbor); // Add neighboring tile entity to air set if not powered
	            }
	            mergeTileEntityRanges(visited, neighbor, world, air, poweredTileEntities); // Recursively merge ranges
	        }
	    }
	}
	*/


	@Override
	public void networkUnpack(NBTTagCompound nbt) {
		this.tank.readFromNBT(nbt, "at");
		this.onTicks = nbt.getInteger("onTicks");

	}
	private static final int MAX_RANGE_X = 50;
	private static final int MAX_RANGE_Y = 30; 
	private static final int MAX_RANGE_Z = 50;

	
	/*
	private boolean isValidRoomBlock(World world, BlockPos pos) {
	    Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
	    if (!block.isAir(world, pos.getX(), pos.getY(), pos.getZ())) {
	        return false;
	    }

	    int solidNeighbors = 0;
	    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
	        BlockPos neighbor = pos.offset(dir);
	        Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());
	        if (!neighborBlock.isAir(world, neighbor.getX(), neighbor.getY(), neighbor.getZ())) {
	            solidNeighbors++;
	        }
	    }

	    return solidNeighbors >= 6;
	}
	*/
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.tank.readFromNBT(nbt, "at");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt, "at");

	}

	@Override
	public FluidTank[] getAllTanks() {
		return new FluidTank[] {tank};
	}

	@Override
	public FluidTank[] getReceivingTanks() {
		return new FluidTank[] {tank};
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}




