package com.hbm.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.hbm.handler.ImpactWorldHandler;
import com.hbm.handler.RogueWorldHandler;
import com.hbm.saveddata.RogueWorldSaveData;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionData;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.potion.HbmPotion;
import com.hbm.saveddata.TomSaveData;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * Utility for permanently synchronizing values every tick with a player in the given context of a world.
 * Uses the Byte Buffer directly instead of NBT to cut back on unnecessary data.
 * @author hbm
 */
public class PermaSyncHandler {
	
	public static HashSet<Integer> boykissers = new HashSet();
	public static float[] pollution = new float[PollutionType.values().length];

	public static void writePacket(ByteBuf buf, World world, EntityPlayerMP player) {
		
		/// TOM IMPACT DATA ///
		TomSaveData impact = TomSaveData.forWorld(world);
		buf.writeFloat(impact.fire);
		buf.writeFloat(impact.dust);
		buf.writeBoolean(impact.impact);
		buf.writeLong(impact.time);
		/// TOM IMPACT DATA ///
		
		/// ROGUE PLANET DATA ///
		RogueWorldSaveData rogue = RogueWorldSaveData.forWorld(world);
		buf.writeFloat(rogue.distance);
		buf.writeFloat(rogue.distance); //dont ask me how, or why, it just works okay :(
		buf.writeFloat(rogue.distance); //and it for some REASON IT SPECIFICALLY **NEEDS** 3!??!?? like it can work with more but 3 is the minimum
		buf.writeFloat(rogue.atmosphere);
		buf.writeBoolean(rogue.star);
		buf.writeBoolean(rogue.rogue);
		/// ROGUE PLANET DATA ///

		/// SHITTY MEMES ///
		List<Integer> ids = new ArrayList();
		for(Object o : world.playerEntities) {
			EntityPlayer p = (EntityPlayer) o;
			if(p.isPotionActive(HbmPotion.death.id)) {
				ids.add(p.getEntityId());
			}
		}
		buf.writeShort((short) ids.size());
		for(Integer i : ids) buf.writeInt(i);
		/// SHITTY MEMES ///

		/// POLLUTION ///
		PollutionData pollution = PollutionHandler.getPollutionData(world, (int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
		if(pollution == null) pollution = new PollutionData();
		for(int i = 0; i < PollutionType.values().length; i++) {
			buf.writeFloat(pollution.pollution[i]);
		}
		/// POLLUTION ///
	}
	
	public static void readPacket(ByteBuf buf, World world, EntityPlayer player) {

		/// TOM IMPACT DATA ///
		ImpactWorldHandler.lastSyncWorld = player.worldObj;
		ImpactWorldHandler.fire = buf.readFloat();
		ImpactWorldHandler.dust = buf.readFloat();
		ImpactWorldHandler.impact = buf.readBoolean();
		/// TOM IMPACT DATA ///
		
		/// ROGUE PLANET DATA ///
		RogueWorldHandler.lastSyncWorld = player.worldObj;
		RogueWorldHandler.distance = buf.readFloat();// SOMEONE PLEASE explain to me why my code is acting like a 5 year old
		RogueWorldHandler.distance = buf.readFloat();//mommy i want three! and only three! otherwise im not gonna work at all waahhh!!!!
		RogueWorldHandler.distance = buf.readFloat();
		RogueWorldHandler.atmosphere = buf.readFloat();
		RogueWorldHandler.star = buf.readBoolean();
		RogueWorldHandler.rogue = buf.readBoolean();
		/// ROGUE PLANET DATA ///

		/// SHITTY MEMES ///
		boykissers.clear();
		int ids = buf.readShort();
		for(int i = 0; i < ids; i++) boykissers.add(buf.readInt());
		/// SHITTY MEMES ///

		/// POLLUTION ///
		for(int i = 0; i < PollutionType.values().length; i++) {
			pollution[i] = buf.readFloat();
		}
		/// POLLUTION ///
	}
}
