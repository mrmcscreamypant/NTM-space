package com.hbm.items.tool;

import java.util.List;
import java.util.Random;

import com.hbm.dim.DebugTeleporter;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBedrockOreTE.TileEntityBedrockOre;
import com.hbm.config.WorldConfig;
import com.hbm.dim.DebugTeleporter;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.logic.EntityTomBlast;
import com.hbm.entity.mob.siege.EntitySiegeTunneler;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBookLore;
import com.hbm.items.special.ItemBookLore.BookLoreType;
import com.hbm.items.special.ItemKitCustom;
import com.hbm.items.special.ItemBedrockOre.EnumBedrockOre;
import com.hbm.lib.Library;
import com.hbm.saveddata.TomSaveData;
import com.hbm.world.feature.OilSpot;
import com.hbm.world.generator.DungeonToolbox;
import com.hbm.world.machine.FWatz;
import com.hbm.world.machine.NuclearReactor;
import com.hbm.world.machine.Watz;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemWandD extends Item {

	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		
		if(world.isRemote)
			return stack;
		
		MovingObjectPosition pos = Library.rayTrace(player, 500, 1, false, true, false);
		
		if(pos != null) {
	
			EntityPlayerMP thePlayer = (EntityPlayerMP) player;
				
			//if(!player.isSneaking())
			//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.ikeDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
			//else
			//System.out.println(player.dimension);
			
			if(stack.stackTagCompound == null)
			{
				stack.stackTagCompound = new NBTTagCompound();
				stack.stackTagCompound.setInteger("building", 0);
			}
			
			boolean up = player.rotationPitch <= 0.5F;
			
			if(!player.isSneaking())
			{
				Random rand = new Random();
				
				switch(stack.stackTagCompound.getInteger("dim"))
				{
				case 0:
					DebugTeleporter.teleport(player, WorldConfig.moonDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.moonDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 1:
					DebugTeleporter.teleport(player, WorldConfig.ikeDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.ikeDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 2:
					DebugTeleporter.teleport(player, WorldConfig.dunaDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.dunaDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 3:
					DebugTeleporter.teleport(player, 0, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.dunaDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 4:
					DebugTeleporter.teleport(player, WorldConfig.eveDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.dunaDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 5:
					DebugTeleporter.teleport(player, WorldConfig.dresDimension, player.posX, 300, player.posZ);
					//DebugTeleporter.teleport(player, WorldConfig.eveDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.dunaDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 6:
					DebugTeleporter.teleport(player, WorldConfig.mohoDimension, player.posX, 300, player.posZ);
					//DebugTeleporter.teleport(player, WorldConfig.eveDimension, player.posX, 300, player.posZ);
					//thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, WorldConfig.dunaDimension, new DebugTeleporter(thePlayer.getServerForPlayer()));
					break;
				case 7:
					DebugTeleporter.teleport(player, WorldConfig.minmusDimension, player.posX, 300, player.posZ);
				case 8:
					TomSaveData data = TomSaveData.forWorld(world);
					data.impact = false;
					data.fire = 0F;
					data.dust = 0F;
					data.dtime=(600-pos.blockY)*2;
					data.time=3600;
					data.x=pos.blockX;
					data.z=pos.blockZ;
					data.markDirty();
					break;
				}
				
				
				
			}
			if(player.isSneaking())
			{
				if(stack.stackTagCompound == null)
				{
					stack.stackTagCompound = new NBTTagCompound();
					stack.stackTagCompound.setInteger("dim", 0);
				} else {
					int i = stack.stackTagCompound.getInteger("dim");
					i++;
					stack.stackTagCompound.setInteger("dim", i);
					if(i >= 9) {
						stack.stackTagCompound.setInteger("dim", 0);
					}
					
					switch(i)
					{
						case 0:
							player.addChatMessage(new ChatComponentText("Dim: Moon"));
							break;
						case 1:
							player.addChatMessage(new ChatComponentText("Dim: Ike"));
							break;
						case 2:
							player.addChatMessage(new ChatComponentText("Dim: Duna"));
							break;
						case 3:
							player.addChatMessage(new ChatComponentText("Dim: Kerbin"));
							break;
						case 4:
							player.addChatMessage(new ChatComponentText("Dim: Eve"));
							break;
						case 5:
							player.addChatMessage(new ChatComponentText("Dim: Dres"));
							break;
						case 6:
							player.addChatMessage(new ChatComponentText("Dim: Moho"));
							break;
						case 7:
							player.addChatMessage(new ChatComponentText("Dim: Minmus"));
							break;
						case 8:
							player.addChatMessage(new ChatComponentText("Impact Event"));
							break;
						default:
							player.addChatMessage(new ChatComponentText("Dim: Moon"));
							break;
						}
					}
				}
			}
			
			/*
			return stack;
			
			/*TimeAnalyzer.startCount("setBlock");
			world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.dirt);
			TimeAnalyzer.startEndCount("getBlock");
			world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
			TimeAnalyzer.endCount();
			TimeAnalyzer.dump();*/
			
			/*TomSaveData data = TomSaveData.forWorld(world);
			data.impact = true;
			data.fire = 0F;
			data.dust = 0F;
			//data.dtime=(600-pos.blockY);
			//data.time=3600;
			//data.x=pos.blockX;
			//data.z=pos.blockZ;
			data.markDirty();*/
			
			/*EntityTomBlast tom = new EntityTomBlast(world);
			tom.posX = pos.blockX;
			tom.posY = pos.blockY;
			tom.posZ = pos.blockZ;
			tom.destructionRange = 600;
			world.spawnEntityInWorld(tom);
			
			ItemStack itemStack = new ItemStack(ModItems.book_lore);
			BookLoreType.setTypeForStack(itemStack, BookLoreType.TEST_LORE);
			
			player.inventory.addItemStackToInventory(itemStack);
			player.inventoryContainer.detectAndSendChanges();
			
			//use sparingly
			/*int k = ((pos.blockX >> 4) << 4) + 8;
			int l = ((pos.blockZ >> 4) << 4) + 8;
			
			MapGenBunker.Start start = new MapGenBunker.Start(world, world.rand, pos.blockX >> 4, pos.blockZ >> 4);
			start.generateStructure(world, world.rand, new StructureBoundingBox(k - 124, l - 124, k + 15 + 124, l + 15 + 124));*/
			//MapGenStronghold.Start startS = new MapGenStronghold.Start(world, world.rand, pos.blockX >> 4, pos.blockZ >> 4);
			//startS.generateStructure(world, world.rand, new StructureBoundingBox(k - 124, l - 124, k + 15 + 124, l + 15 + 124));
			
			//OilSpot.generateOilSpot(world, pos.blockX, pos.blockZ, 3, 50, true);
			
			/*EntityNukeTorex torex = new EntityNukeTorex(world);
			torex.setPositionAndRotation(pos.blockX, pos.blockY + 1, pos.blockZ, 0, 0);
			torex.getDataWatcher().updateObject(10, 1.5F);
			world.spawnEntityInWorld(torex);
			EntityTracker entitytracker = ((WorldServer) world).getEntityTracker();
			IntHashMap map = ReflectionHelper.getPrivateValue(EntityTracker.class, entitytracker, "trackedEntityIDs", "field_72794_c");
			EntityTrackerEntry entry = (EntityTrackerEntry) map.lookup(torex.getEntityId());
			entry.blocksDistanceThreshold = 1000;
			world.spawnEntityInWorld(EntityNukeExplosionMK5.statFacNoRad(world, 150, pos.blockX, pos.blockY + 1, pos.blockZ));*/
			
			//DungeonToolbox.generateBedrockOreWithChance(world, world.rand, pos.blockX, pos.blockZ, EnumBedrockOre.TITANIUM,	new FluidStack(Fluids.SULFURIC_ACID, 500), 2, 1);
			
			/*EntitySiegeTunneler tunneler = new EntitySiegeTunneler(world);
			tunneler.setPosition(pos.blockX, pos.blockY + 1, pos.blockZ);
			tunneler.onSpawnWithEgg(null);
			world.spawnEntityInWorld(tunneler);*/
			
			//CellularDungeonFactory.meteor.generate(world, x, y, z, world.rand);
			
			/*int r = 5;
			
			int x = pos.blockX;
			int y = pos.blockY;
			int z = pos.blockZ;
			for(int i = x - r; i <= x + r; i++) {
				for(int j = y - r; j <= y + r; j++) {
					for(int k = z - r; k <= z + r; k++) {
						if(world.getBlock(i, j, k) == ModBlocks.concrete_super)
							world.getBlock(i, j, k).updateTick(world, i, j, k, world.rand);
					}
				}
			}*/
			
			//new Bunker().generate(world, world.rand, x, y, z);
			
			/*EntityBlockSpider spider = new EntityBlockSpider(world);
			spider.setPosition(x + 0.5, y, z + 0.5);
			spider.makeBlock(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
			world.setBlockToAir(x, y, z);
			world.spawnEntityInWorld(spider);*/
			
			
    		/*NBTTagCompound data = new NBTTagCompound();
    		data.setString("type", "rift");
    		data.setDouble("posX", x);
    		data.setDouble("posY", y + 1);
    		data.setDouble("posZ", z);
    		
    		MainRegistry.proxy.effectNT(data);*/
			
			//new Spaceship().generate_r0(world, world.rand, x - 4, y, z - 8);

			//new Ruin001().generate_r0(world, world.rand, x, y - 8, z);

			//CellularDungeonFactory.jungle.generate(world, x, y, z, world.rand);
			//CellularDungeonFactory.jungle.generate(world, x, y + 4, z, world.rand);
			//CellularDungeonFactory.jungle.generate(world, x, y + 8, z, world.rand);
			
			//new AncientTomb().build(world, world.rand, x, y + 10, z);
			
			//new ArcticVault().trySpawn(world, x, y, z);
			
			/*for(int ix = x - 10; ix <= x + 10; ix++) {
				for(int iz = z - 10; iz <= z + 10; iz++) {

					if(ix % 2 == 0 && iz % 2 == 0) {
						for(int iy = y; iy < y + 4; iy++)
							world.setBlock(ix, iy, iz, ModBlocks.brick_dungeon_flat);
						world.setBlock(ix, y + 4, iz, ModBlocks.brick_dungeon_tile);
					} else if(ix % 2 == 1 && iz % 2 == 1) {
						world.setBlock(ix, y, iz, ModBlocks.reinforced_stone);
						world.setBlock(ix, y + 1, iz, ModBlocks.spikes);
					} else if(world.rand.nextInt(3) == 0) {
						for(int iy = y; iy < y + 4; iy++)
							world.setBlock(ix, iy, iz, ModBlocks.brick_dungeon_flat);
						world.setBlock(ix, y + 4, iz, ModBlocks.brick_dungeon_tile);
					} else {
						world.setBlock(ix, y, iz, ModBlocks.reinforced_stone);
						world.setBlock(ix, y + 1, iz, ModBlocks.spikes);
					}
				}
			}*/
		return stack;
		}
		

	
	



	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
	{
		list.add("Used for debugging purposes.");

		if(itemstack.stackTagCompound != null)
		{
			switch(itemstack.stackTagCompound.getInteger("dim"))
			{
			case 0:
				list.add("Dim: Moon");
				break;
			case 1:
				list.add("Dim: Ike");
				break;
			case 2:
				list.add("Dim: Duna");
				break;
			case 3:
				list.add("Dim: Kerbin");
				break;
			case 4:
				list.add("Dim: Eve");
				break;
			case 5:
				list.add("Dim: Dres");
				break;
			case 6:
				list.add("Dim: Moho");
				break;
			case 7:
				list.add("Dim: Minmus");
				break;
			case 8:
				list.add("Impact");
				break;
	}
}
	}
}