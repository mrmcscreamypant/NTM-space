package com.hbm.render.entity.effect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.effect.EntityBlackHole;
//import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.entity.effect.EntityBlackHole.Cloudlet;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderBlackHole extends Render {
	private static final ResourceLocation cloudlet = new ResourceLocation(RefStrings.MODID + ":textures/particle/particle_base.png");
	protected static final ResourceLocation objTesterModelRL = new ResourceLocation(RefStrings.MODID, "models/Sphere.obj");
	protected IModelCustom blastModel;
	protected ResourceLocation hole = new ResourceLocation(RefStrings.MODID, "textures/models/BlackHole.png");
	protected ResourceLocation swirl = new ResourceLocation(RefStrings.MODID, "textures/entity/bhole.png");
	protected ResourceLocation disc = new ResourceLocation(RefStrings.MODID, "textures/entity/bholeDisc.png");

	public RenderBlackHole() {
		blastModel = AdvancedModelLoader.loadModel(objTesterModelRL);
	}

	@Override
	public void doRender(Entity entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float interp) {
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);

		float size = entity.getDataWatcher().getWatchableObjectFloat(16);

		GL11.glScalef(size, size, size);

		bindTexture(hole);
		blastModel.renderAll();

		if(entity instanceof EntityVortex) {
			renderSwirl(entity, interp);
			
		} else if(entity instanceof EntityRagingVortex) {
			renderSwirl(entity, interp);
			renderJets(entity, interp);
			
		} else {
			cloudletWrapper((EntityBlackHole) entity, interp);
			//renderDisc(entity, interp);
			GL11.glScalef(size, size, size);
			renderJets(entity, interp);
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
	}
	
	private Comparator cloudSorter = new Comparator() {

		@Override
		public int compare(Object arg0, Object arg1) {
			Cloudlet first = (Cloudlet) arg0;
			Cloudlet second = (Cloudlet) arg1;
			EntityPlayer player = MainRegistry.proxy.me();
			double dist1 = player.getDistanceSq(first.posX, first.posY, first.posZ);
			double dist2 = player.getDistanceSq(second.posX, second.posY, second.posZ);
			
			return dist1 > dist2 ? -1 : dist1 == dist2 ? 0 : 1;
		}
	};
	
	private void cloudletWrapper(EntityBlackHole cloud, float interp) {

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		// To prevent particles cutting off before fully fading out
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		RenderHelper.disableStandardItemLighting();

		bindTexture(cloudlet);

		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		
		ArrayList<Cloudlet> cloudlets = new ArrayList(cloud.cloudlets);
		cloudlets.sort(cloudSorter);
		
		for(Cloudlet cloudlet : cloudlets) {
			Vec3 vec = cloudlet.getInterpPos(interp);
			double x2 = (cloud.posX - cloudlet.posX);
			double z2 = (cloud.posZ - cloudlet.posZ);
			Vec3 suck = Vec3.createVectorHelper((x2)/5, 0, (z2)/5);
			suck.rotateAroundY(90);
			if(cloudlet.motionX>=0d)
			{
				tessellateCloudlet(tess, vec.xCoord - cloud.posX, vec.yCoord - cloud.posY, vec.zCoord - cloud.posZ, cloudlet, interp, suck);
			}
		}

		tess.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	
	protected ResourceLocation discTex() {
		return this.disc;
	}
	
	protected void renderDisc(Entity entity, float interp) {
		
		float glow = 0.75F;
		
		bindTexture(discTex());

		GL11.glPushMatrix();
		GL11.glRotatef(entity.getEntityId() % 90 - 45, 1, 0, 0);
		GL11.glRotatef(entity.getEntityId() % 360, 0, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.0F);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		
		Tessellator tess = Tessellator.instance;
		
		int count = 16;

		Vec3 vec = Vec3.createVectorHelper(1, 0, 0);
		
		for(int k = 0; k < steps(); k++) {
			
			GL11.glPushMatrix();
			GL11.glRotatef((entity.ticksExisted + interp % 360) * -((float)Math.pow(k + 1, 1.25)), 0, 1, 0);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			double s = 3 - k * 0.175D;
			
			for(int j = 0; j < 2; j++) {
				
				tess.startDrawingQuads();
				for(int i = 0; i < count; i++) {
					
					if(j == 0)
						this.setColorFromIteration(tess, k, 1F);
					else
						tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
					
					tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
					this.setColorFromIteration(tess, k, 0F);
					tess.addVertexWithUV(vec.xCoord * s * 2, 0, vec.zCoord * s * 2, 0.5 + vec.xCoord * 0.5, 0.5 + vec.zCoord * 0.5);
					
					vec.rotateAroundY((float)(Math.PI * 2 / count));
					this.setColorFromIteration(tess, k, 0F);
					tess.addVertexWithUV(vec.xCoord * s * 2, 0, vec.zCoord * s * 2, 0.5 + vec.xCoord * 0.5, 0.5 + vec.zCoord * 0.5);
					
					if(j == 0)
						this.setColorFromIteration(tess, k, 1F);
					else
						tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
					
					tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
				}
				tess.draw();
				
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			}
			
			GL11.glPopMatrix();
		}
		

		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}
	
	protected int steps() {
		return 15;
	}
	
	protected void setColorFromIteration(Tessellator tess, int iteration, float alpha) {
		
		if(iteration < 5) {
			float g = 0.125F + iteration * (1F / 10F);
			tess.setColorRGBA_F(1.0F, g, 0.0F, alpha);
			return;
		}
		
		if(iteration == 5) {
			tess.setColorRGBA_F(1.0F, 1.0F, 0.0F, alpha);
			return;
		}
		
		if(iteration > 5) {
			
			int i = iteration - 6;
			float r = 1.0F - i * (1F / 9F);
			float g = 1F - i * (1F / 9F);
			float b = i * (1F / 5F);
			tess.setColorRGBA_F(r, g, b, alpha);
		}
	}
	
	protected void renderSwirl(Entity entity, float interp) {
		
		float glow = 0.75F;
		
		if(entity instanceof EntityRagingVortex)
			glow = 0.25F;
		
		bindTexture(swirl);

		GL11.glPushMatrix();
		GL11.glRotatef(entity.getEntityId() % 90 - 45, 1, 0, 0);
		GL11.glRotatef(entity.getEntityId() % 360, 0, 1, 0);
		GL11.glRotatef((entity.ticksExisted + interp % 360) * -5, 0, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.0F);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		Vec3 vec = Vec3.createVectorHelper(1, 0, 0);
		
		Tessellator tess = Tessellator.instance;
		
		double s = 3;
		int count = 16;
		
		//swirl, inner part (solid)
		for(int j = 0; j < 2; j++) {
			tess.startDrawingQuads();
			for(int i = 0; i < count; i++) {
	
				tess.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
				tess.addVertexWithUV(vec.xCoord * 0.9, 0, vec.zCoord * 0.9, 0.5 + vec.xCoord * 0.25 / s * 0.9, 0.5 + vec.zCoord * 0.25 / s * 0.9);
				
				if(j == 0)
					this.setColorFull(entity, tess);
				else
					tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
				
				tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
				
				vec.rotateAroundY((float)(Math.PI * 2 / count));
				
				if(j == 0)
					this.setColorFull(entity, tess);
				else
					tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
				
				tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
				tess.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
				tess.addVertexWithUV(vec.xCoord * 0.9, 0, vec.zCoord * 0.9, 0.5 + vec.xCoord * 0.25 / s * 0.9, 0.5 + vec.zCoord * 0.25 / s * 0.9);
			}
			
			tess.draw();
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		
		//swirl, outer part (fade)
		for(int j = 0; j < 2; j++) {
			
			tess.startDrawingQuads();
			for(int i = 0; i < count; i++) {
				
				if(j == 0)
					this.setColorFull(entity, tess);
				else
					tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
				
				tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
				this.setColorNone(entity, tess);
				tess.addVertexWithUV(vec.xCoord * s * 2, 0, vec.zCoord * s * 2, 0.5 + vec.xCoord * 0.5, 0.5 + vec.zCoord * 0.5);
				
				vec.rotateAroundY((float)(Math.PI * 2 / count));
				this.setColorNone(entity, tess);
				tess.addVertexWithUV(vec.xCoord * s * 2, 0, vec.zCoord * s * 2, 0.5 + vec.xCoord * 0.5, 0.5 + vec.zCoord * 0.5);
				
				if(j == 0)
					this.setColorFull(entity, tess);
				else
					tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, glow);
				
				tess.addVertexWithUV(vec.xCoord * s, 0, vec.zCoord * s, 0.5 + vec.xCoord * 0.25, 0.5 + vec.zCoord * 0.25);
			}
			tess.draw();
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		

		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		GL11.glPopMatrix();
	}
	
	protected void renderJets(Entity entity, float interp) {

		Tessellator tess = Tessellator.instance;

		GL11.glPushMatrix();
		//GL11.glRotatef(entity.getEntityId() % 90 - 45, 1, 0, 0);
		//GL11.glRotatef(entity.getEntityId() % 360, 0, 1, 0);
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDepthMask(false);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		for(int j = -1; j <= 1; j += 2) {
			tess.startDrawing(GL11.GL_TRIANGLE_FAN);

			tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.35F);
			tess.addVertex(0, 0, 0);
			tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.0F);
			
			Vec3 jet = Vec3.createVectorHelper(0.5, 0, 0);
			
			for(int i = 0; i <= 12; i++) {

				tess.addVertex(jet.xCoord, 10 * j, jet.zCoord);
				jet.rotateAroundY((float)(Math.PI / 6 * -j));
			}
			
			tess.draw();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}
	
	protected void renderFlare(Entity entity) {
		
		GL11.glPushMatrix();
		GL11.glScalef(0.2F, 0.2F, 0.2F);
		
		Tessellator tessellator = Tessellator.instance;
		RenderHelper.disableStandardItemLighting();
		int j = 75;
		float f1 = (j + 2.0F) / 200.0F;
		float f2 = 0.0F;
		int count = 250;

		count = j;

		if(f1 > 0.8F) {
			f2 = (f1 - 0.8F) / 0.2F;
		}

		Random random = new Random(432L);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		
		for(int i = 0; i < count; i++) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float f3 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			setColorFull(entity, tessellator);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			setColorNone(entity, tessellator);
			tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
			tessellator.addVertex(0.866D * f4, f3, -0.5F * f4);
			tessellator.addVertex(0.0D, f3, 1.0F * f4);
			tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
			tessellator.draw();
		}

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}

	private void tessellateCloudlet(Tessellator tess, double posX, double posY, double posZ, Cloudlet cloud, float interp, Vec3 cloudV) {

		float alpha = cloud.getAlpha();
		float scale = cloud.getScale();

		float f1 = ActiveRenderInfo.rotationX;
		float f2 = ActiveRenderInfo.rotationZ;
		float f3 = ActiveRenderInfo.rotationYZ;
		float f4 = ActiveRenderInfo.rotationXY;
		float f5 = ActiveRenderInfo.rotationXZ;
		Vec3 cloudV2 = Vec3.createVectorHelper(cloud.motionX, 0, cloud.motionZ);
		Vec3 cloudP = Vec3.createVectorHelper(cloud.posX-Minecraft.getMinecraft().thePlayer.posX, 0, cloud.posZ-Minecraft.getMinecraft().thePlayer.motionZ);
		//cloudP.normalize();
		Vec3 playerV = Vec3.createVectorHelper(Minecraft.getMinecraft().thePlayer.motionX, 0, Minecraft.getMinecraft().thePlayer.motionZ);
		Vec3 diff = cloudV2.subtract(playerV);
		
		//double pZ = Minecraft.getMinecraft().thePlayer.posZ-Minecraft.getMinecraft().thePlayer.lastTickPosZ;
		//double mZ = cZ+pZ;
		//double relX = mX/(1+pX*cX);
		//double relZ = mZ/(1+pZ*cZ);
		//Vec3 vel = Vec3.createVectorHelper(mX-cloud.motionX, mY-cloud.motionY, mZ-cloud.motionZ);
		/*
		if(Minecraft.getMinecraft().thePlayer.ticksExisted%20==0)
		{
			System.out.println("Displacement: "+playerV.lengthVector());
		}
		*/
		//double rel = vel.lengthVector();
		float brightness = 0;
		double rel =diff.lengthVector();//relX+relZ;// Math.sqrt((Minecraft.getMinecraft().thePlayer.motionX-cloud.motionX)-(Minecraft.getMinecraft().thePlayer.motionZ-cloud.motionZ));
		//if (rel>1f)
		//{
		//	brightness = 0.75F * cloud.colorMod;
		//}
		//if (rel<=1f)
		{
			brightness = (float) Math.max(0,(0.75F * cloud.colorMod));//*(rel/1));
		}
		//if(Minecraft.getMinecraft().thePlayer.ticksExisted%20==0)
		//{
		//	System.out.println("Displacement: "+(rel/10));
		//}
		/*if (mZ-cloud.motionZ<-1f)
		{
			brightness = 0.75F * cloud.colorMod;
		}
		if (mZ-cloud.motionZ>=-1f)
		{
			brightness = (float) Math.max(0,(0.75F * cloud.colorMod)*-(mZ-cloud.motionZ));
		}*/
		//System.out.println(rel);
		Vec3 color = cloud.getInterpColor(interp);
		tess.setColorRGBA_F((float)color.xCoord * brightness, (float)color.yCoord * brightness, (float)color.zCoord * brightness, alpha);

		tess.addVertexWithUV((double) (posX - f1 * scale - f3 * scale), (double) (posY - f5 * scale), (double) (posZ - f2 * scale - f4 * scale), 1, 1);
		tess.addVertexWithUV((double) (posX - f1 * scale + f3 * scale), (double) (posY + f5 * scale), (double) (posZ - f2 * scale + f4 * scale), 1, 0);
		tess.addVertexWithUV((double) (posX + f1 * scale + f3 * scale), (double) (posY + f5 * scale), (double) (posZ + f2 * scale + f4 * scale), 0, 0);
		tess.addVertexWithUV((double) (posX + f1 * scale - f3 * scale), (double) (posY - f5 * scale), (double) (posZ + f2 * scale - f4 * scale), 0, 1);

	}
	
	protected void setColorFull(Entity e, Tessellator tessellator) {

		if(e instanceof EntityVortex)
			tessellator.setColorRGBA_I(0x3898b3, (int) (255.0F * (1.0F)));

		else if(e instanceof EntityRagingVortex)
			tessellator.setColorRGBA_I(0xe8390d, (int) (255.0F * (1.0F)));

		else
			tessellator.setColorRGBA_I(0xFFB900, (int) (255.0F * (1.0F)));
	}

	protected void setColorNone(Entity e, Tessellator tessellator) {

		if(e instanceof EntityVortex)
			tessellator.setColorRGBA_I(0x3898b3, 0);

		else if(e instanceof EntityRagingVortex)
			tessellator.setColorRGBA_I(0xe8390d, 0);

		else
			tessellator.setColorRGBA_I(0xFFB900, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

}
