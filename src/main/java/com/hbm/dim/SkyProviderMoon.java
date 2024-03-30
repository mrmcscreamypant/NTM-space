package com.hbm.dim;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.ImpactWorldHandler;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.main.ModEventHandler;
import com.hbm.main.ModEventHandlerClient;
import com.hbm.main.ResourceManager;
import com.hbm.main.ModEventHandlerClient.Meteor;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.hbm.tileentity.machine.TileEntityAtmoExtractor;
import com.hbm.util.AstronomyUtil;
import com.hbm.util.PlanetaryTraitUtil;
import com.hbm.util.PlanetaryTraitUtil.Hospitality;

import java.util.Random;

import javax.swing.text.Position;

public class SkyProviderMoon extends IRenderHandler {
	
	private static final ResourceLocation sunTexture = new ResourceLocation("textures/environment/sun.png");
	private static final ResourceLocation kerbin = new ResourceLocation("hbm:textures/misc/space/kerbin.png");
	private static final ResourceLocation planet = new ResourceLocation("hbm:textures/misc/space/planet.png");
	private static final ResourceLocation flash = new ResourceLocation("hbm:textures/misc/space/flare.png");
	private static final ResourceLocation flash2 = new ResourceLocation("hbm:textures/misc/space/sunspike.png");
	private static final ResourceLocation night = new ResourceLocation("hbm:textures/misc/space/night.png");
	private static final ResourceLocation digammaStar = new ResourceLocation("hbm:textures/misc/space/star_digamma.png");
	private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/particle/shockwave.png");
	private static final ResourceLocation ntex = new ResourceLocation("hbm:textures/misc/line.png");
	private static final ResourceLocation ntexe = new ResourceLocation("hbm:textures/particle/cens.png");

	Random random = new Random(42);

	public static boolean displayListsInitialized = false;
	public static int starGLCallList;
	public static int glSkyList;
	public static int glSkyList2;

	protected double x;
	protected double y;
	protected double z;

	public SkyProviderMoon() {
	    if (!displayListsInitialized) {
	        initializeDisplayLists();
	    }
	}

	private void initializeDisplayLists() {
	    starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		final Tessellator tessellator = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		final byte byte2 = 64;
		final int i = 256 / byte2 + 2;
		float f = 16F;

		for(int j = -byte2 * i; j <= byte2 * i; j += byte2) {
			for(int l = -byte2 * i; l <= byte2 * i; l += byte2) {
				tessellator.startDrawingQuads();
				tessellator.addVertex(j + 0, f, l + 0);
				tessellator.addVertex(j + byte2, f, l + 0);
				tessellator.addVertex(j + byte2, f, l + byte2);
				tessellator.addVertex(j + 0, f, l + byte2);
				tessellator.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f = -16F;
		tessellator.startDrawingQuads();

		for(int k = -byte2 * i; k <= byte2 * i; k += byte2) {
			for(int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
				tessellator.addVertex(k + byte2, f, i1 + 0);
				tessellator.addVertex(k + 0, f, i1 + 0);
				tessellator.addVertex(k + 0, f, i1 + byte2);
				tessellator.addVertex(k + byte2, f, i1 + byte2);
			}
		}

		tessellator.draw();
		GL11.glEndList();
		displayListsInitialized = true;
	}
	

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
        float solar = (AstronomyUtil.KerbolRadius*4/(AstronomyUtil.KerbinAU*AstronomyUtil.AUToKm))*360;
        //this is beyond overengineered
        double MohoKerbin = AstronomyUtil.getInterplanetaryDistance(world, AstronomyUtil.MohoAU, AstronomyUtil.MohoP, AstronomyUtil.KerbinAU, AstronomyUtil.KerbinP);
        double EveKerbin = AstronomyUtil.getInterplanetaryDistance(world, AstronomyUtil.EveAU, AstronomyUtil.EveP, AstronomyUtil.KerbinAU, AstronomyUtil.KerbinP);
        double KerbinDuna = AstronomyUtil.getInterplanetaryDistance(world, AstronomyUtil.KerbinAU, AstronomyUtil.KerbinP, AstronomyUtil.DunaAU, AstronomyUtil.DunaP);
        double KerbinJool = AstronomyUtil.getInterplanetaryDistance(world, AstronomyUtil.KerbinAU, AstronomyUtil.KerbinP, AstronomyUtil.JoolAU, AstronomyUtil.JoolP);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 vec3 = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float f1 = (float) vec3.xCoord;
		float f2 = (float) vec3.yCoord;
		float f3 = (float) vec3.zCoord;
		float f6;

		if(mc.gameSettings.anaglyph) {
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}
		



		GL11.glColor3f(f1, f2, f3);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(f1, f2, f3);
		GL11.glCallList(this.glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float f7;
		float f8;
		float f9;
		float f10;
		float f18 = world.getStarBrightness(partialTicks);

		if(f18 > 0.0F) {
			GL11.glPushMatrix();
	        mc.renderEngine.bindTexture(this.night);
	        GL11.glEnable(3553);
	        GL11.glBlendFunc(770, 1);
	        float starBrightness = world.getStarBrightness(partialTicks) *0.6f;
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, starBrightness);
	        float x = 0.0F;
	        float y = 0.0F;
	        float z = 0.0F;
	        GL11.glTranslatef(x, y, z);
	        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
	        
	       // 

	        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
	        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, starBrightness);
	        
	        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
	        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
	        renderSkyboxSide(tessellator, 4);
	        
	        GL11.glPushMatrix();
	        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
	        renderSkyboxSide(tessellator, 1);
	        GL11.glPopMatrix();
	        
	        GL11.glPushMatrix();
	        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
	        renderSkyboxSide(tessellator, 0);
	        GL11.glPopMatrix();
	        
	        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
	        renderSkyboxSide(tessellator, 5);
	        
	        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
	        renderSkyboxSide(tessellator, 2);
	        
	        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
	        renderSkyboxSide(tessellator, 3);
	        GL11.glDisable(3553);
	        GL11.glPopMatrix();	        
			/*GL11.glPushMatrix();
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-19.0F, 0, 1.0F, 0);
			GL11.glColor4f(f18, f18, f18, f18);
			GL11.glCallList(this.starGLCallList);
			GL11.glPopMatrix();*/
		}

		float fl = ModEventHandlerClient.scale;
		float nl = ModEventHandlerClient.shield;
		float el = ModEventHandlerClient.nmass;
		float xcl = ModEventHandlerClient.shielde;
		float xcvl = ModEventHandlerClient.csyw;

		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();

		float alphad = 1.0F - Math.min(1.0F, el / 100);
		float alpd = 1.0F - Math.min(1.0F, xcvl / 100);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(21.5, 33, -28); 
		GL11.glScaled(0 + nl, 0 + nl, 0 + nl);
		GL11.glRotated(90.0, -10.0, -1.0, 50.0);
		GL11.glRotated(20.0, -0.0, -1.0, 1.0);

		GL11.glColor4d(1, 1, 1,  alphad);
		GL11.glEnable(GL11.GL_BLEND);


		//GL11.glDepthMask(false);
		
	    FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.texture);
		ResourceManager.plane.renderAll();

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPopMatrix();
		GL11.glPushMatrix();


		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(21.5, 33, -28); 

		GL11.glScaled(0 + xcl, 0 + xcl, 0 + xcl);
		GL11.glRotated(90.0, -10.0, -1.0, 50.0);
		GL11.glRotated(20.0, -0.0, -1.0, 1.0);

		GL11.glColor4d(1, 1, 1, alpd);
		GL11.glEnable(GL11.GL_BLEND);


		//GL11.glDepthMask(false);
		
	    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ntexe);
		ResourceManager.plane.renderAll();

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPopMatrix();
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
		float flash = ModEventHandlerClient.flashd;
		{	
			GL11.glPushMatrix();
			float asize = 14;
			float alpha = 1.0F - Math.min(1.0F, flash / 100);
			GL11.glRotated(180.0, 0.0, 5.0, 0.0);
			GL11.glRotated(90.0, -12.0, 7.3F, -4.0);
			mc.renderEngine.bindTexture(this.flash);

			GL11.glColor4f(1, 1, 1, alpha);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-flash, 100.0D, -flash, 0.0D, 0.0D);
			tessellator.addVertexWithUV(flash, 100.0D, -flash, 1.0D, 0.0D);
			tessellator.addVertexWithUV(flash, 100.0D, flash, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-flash, 100.0D, flash, 0.0D, 1.0D);
			tessellator.draw();
			GL11.glPopMatrix();

		}
		{	
			GL11.glPushMatrix();

			float var14 = flash * 2;
			float var15 = Math.min(70, var14 * 2 );
			float alpha = 1.0F - Math.min(1.0F, flash /100 );
			
			GL11.glRotated(180.0, 0.0, 5.0, 0.0);
			GL11.glRotated(90.0, -12.0, 7.3F, -4.0);

			mc.renderEngine.bindTexture(this.texture);

			GL11.glColor4f(1, 1, 1, alpha);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-var14, 100.0D, -var14, 0.0D, 0.0D);
			tessellator.addVertexWithUV(var14, 100.0D, -var14, 1.0D, 0.0D);
			tessellator.addVertexWithUV(var14, 100.0D, var14, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-var14, 100.0D, var14, 0.0D, 1.0D);
			tessellator.draw();
			GL11.glPopMatrix();

		}
		{	
			GL11.glPushMatrix();
			
			float var14 = 1;
			float var15 = Math.min(70, var14 * 2 );
            float alpha = (flash <= 0) ? 0.0F : 1.0F - Math.min(1.0F, flash / 100);
			GL11.glRotated(79, 90, 0, 0);
			GL11.glTranslated(-0.6, 0, 0);
			mc.renderEngine.bindTexture(this.ntex);
			GL11.glRotated(27, 00, 80, 0);
			
			GL11.glColor4f(1, 1, 1, alpha);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-var14, 100.0D, -var14, 0.0D, 0.0D);
			tessellator.addVertexWithUV(flash * 8, 100.0D, -var14, 1.0D, 0.0D);
			tessellator.addVertexWithUV(flash * 8, 100.0D, var14, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-var14, 100.0D, var14, 0.0D, 1.0D);
			tessellator.draw();
			

			GL11.glPopMatrix();

		}
		
		GL11.glPushMatrix();
		f7 = 0.0F;
		f8 = 0.0F;
		f9 = 0.0F;
		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

		// Render sun
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		// Some blanking to conceal the stars
		f10 = (AstronomyUtil.KerbolRadius*4/(AstronomyUtil.KerbinAU*AstronomyUtil.AUToKm))*360;
		float f11 = f10*3;
		tessellator.startDrawingQuads();
		tessellator.addVertex(-f10, 99.9D, -f10);
		tessellator.addVertex(f10, 99.9D, -f10);
		tessellator.addVertex(f10, 99.9D, f10);
		tessellator.addVertex(-f10, 99.9D, f10);
		tessellator.draw();
		
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1);
			mc.renderEngine.bindTexture(this.sunTexture);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-f10, 100.0D, -f10, 0.0D, 0.0D);
			tessellator.addVertexWithUV(f10, 100.0D, -f10, 1.0D, 0.0D);
			tessellator.addVertexWithUV(f10, 100.0D, f10, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-f10, 100.0D, f10, 0.0D, 1.0D);
			tessellator.draw();
		}

		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1f);
			if(PlanetaryTraitUtil.isDimensionWithTraitNT(world, Hospitality.BREATHEABLE)){
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5f);

			}
			mc.renderEngine.bindTexture(this.flash2);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-f11, 100.0D, -f11, 0.0D, 0.0D);
			tessellator.addVertexWithUV(f11, 100.0D, -f11, 1.0D, 0.0D);
			tessellator.addVertexWithUV(f11, 100.0D, f11, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-f11, 100.0D, f11, 0.0D, 1.0D);
			tessellator.draw();
		}

		{
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glRotatef(world.getCelestialAngle(partialTicks) * -360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-60.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			f10 = (float) ((AstronomyUtil.KerbinRadius/AstronomyUtil.MunKerbinKm)*180);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.kerbin);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-f10, 100.0D, -f10, 0.0D, 0.0D);
			tessellator.addVertexWithUV(f10, 100.0D, -f10, 1.0D, 0.0D);
			tessellator.addVertexWithUV(f10, 100.0D, f10, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-f10, 100.0D, f10, 0.0D, 1.0D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_BLEND);
        	GL11.glPopMatrix();

		}

		{
			OpenGlHelper.glBlendFunc(770, 1, 1, 0);

			float brightness = (float) Math.sin(world.getCelestialAngle(partialTicks) * Math.PI);
			brightness *= brightness;

			GL11.glPushMatrix();
			GL11.glColor4f(brightness, brightness, brightness, brightness);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(140.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-40.0F, 0.0F, 0.0F, 1.0F);

			FMLClientHandler.instance().getClient().renderEngine.bindTexture(digammaStar);

			float digamma = HbmLivingProps.getDigamma(Minecraft.getMinecraft().thePlayer);
			float var12 = 1F * (1 + digamma * 0.25F);
			double dist = 100D - digamma * 2.5;

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-var12, dist, -var12, 0.0D, 0.0D);
			tessellator.addVertexWithUV(var12, dist, -var12, 0.0D, 1.0D);
			tessellator.addVertexWithUV(var12, dist, var12, 1.0D, 1.0D);
			tessellator.addVertexWithUV(-var12, dist, var12, 1.0D, 0.0D);
			tessellator.draw();
			GL11.glPopMatrix();

		}
		{
			
		}

		
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		for(Meteor meteor : ModEventHandlerClient.meteors) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * partialTicks;
			double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * partialTicks;
			double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * partialTicks;
			Vec3 vec = Vec3.createVectorHelper(meteor.posX - dx, meteor.posY - dy, meteor.posZ - dz);
			Vec3 vec2 = Vec3.createVectorHelper(meteor.posX - dx, meteor.posY - dy, meteor.posZ - dz);
			double l = Math.min(Minecraft.getMinecraft().gameSettings.renderDistanceChunks*16, vec.lengthVector());
			double sf = Math.max(0.2,(312.5/(vec2.lengthVector()/l)));
			vec = vec.normalize();
			Vec3 vecd = Vec3.createVectorHelper(vec.xCoord*l, vec.yCoord*l, vec.zCoord*l);
			GL11.glTranslated( vecd.xCoord, vecd.yCoord , vecd.zCoord);
			double descent = 2017d-meteor.posY;
			double quadratic = (-1*Math.pow(descent, 2)+(1517*descent))/41;
			//float scalar = (float) (7000f/vec2.lengthVector()); 
			float scalar = (float) (quadratic/vec2.lengthVector()); 
			GL11.glScaled(scalar, scalar, scalar);
				//System.out.println("scalar "+scalar);
			renderGlow(new ResourceLocation(RefStrings.MODID + ":textures/particle/flare.png"), 1, 1, 1, partialTicks);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glPopMatrix();
	}
	for(Meteor fragment : ModEventHandlerClient.fragments) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * partialTicks;
		double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * partialTicks;
		double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * partialTicks;
		Vec3 vec = Vec3.createVectorHelper(fragment.posX - dx, fragment.posY - dy, fragment.posZ - dz);
		Vec3 vec2 = Vec3.createVectorHelper(fragment.posX - dx, fragment.posY - dy, fragment.posZ - dz);
		double l = Math.min(Minecraft.getMinecraft().gameSettings.renderDistanceChunks*16, vec.lengthVector());
		double sf = Math.max(0.2,(312.5/(vec2.lengthVector()/l)));
		vec = vec.normalize();
		Vec3 vecd = Vec3.createVectorHelper(vec.xCoord*l, vec.yCoord*l, vec.zCoord*l);
		GL11.glTranslated( vecd.xCoord, vecd.yCoord , vecd.zCoord);
		double descent = 2017d-fragment.posY;
		double quadratic = (-1*Math.pow(descent, 2)+(1517*descent))/82;
		//float scalar = (float) (7000f/vec2.lengthVector()); 
		float scalar = (float) (quadratic/vec2.lengthVector()); 
			GL11.glScaled(scalar, scalar, scalar);
			//System.out.println("scalar "+scalar);
			renderGlow(new ResourceLocation(RefStrings.MODID + ":textures/particle/flare.png"), 1, 1, 1, partialTicks);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
}
	for(Meteor smoke : ModEventHandlerClient.smoke) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * partialTicks;
		double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * partialTicks;
		double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * partialTicks;
		Vec3 vec = Vec3.createVectorHelper(smoke.posX - dx, smoke.posY - dy, smoke.posZ - dz);
		Vec3 vec2 = Vec3.createVectorHelper(smoke.posX - dx, smoke.posY - dy, smoke.posZ - dz);
		double l = Math.min(Minecraft.getMinecraft().gameSettings.renderDistanceChunks*16, vec.lengthVector());
		double sf = Math.max(0.2,(312.5/(vec2.lengthVector()/l)));
		vec = vec.normalize();
		Vec3 vecd = Vec3.createVectorHelper(vec.xCoord*l, vec.yCoord*l, vec.zCoord*l);
		GL11.glTranslated( vecd.xCoord, vecd.yCoord , vecd.zCoord);
		double descent = 2017d-smoke.posY;
		double quadratic = (-1*Math.pow(descent, 2)+(1517*descent))/82;
		//float scalar = (float) (14000f/vec2.lengthVector()); 
		float scalar = (float) (quadratic/vec2.lengthVector()); 
			//scalar = 3500;
		GL11.glColor4d(1, 0, 0, 1);
			GL11.glScaled(scalar, scalar, scalar);
			renderSmoke(new ResourceLocation(RefStrings.MODID + ":textures/particle/particle_base.png"), smoke.age);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

		if(d0 < 0.0D) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			f8 = 1.0F;
			f9 = -((float) (d0 + 65.0D));
			f10 = -f8;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertex(-f8, f9, f8);
			tessellator.addVertex(f8, f9, f8);
			tessellator.addVertex(f8, f10, f8);
			tessellator.addVertex(-f8, f10, f8);
			tessellator.addVertex(-f8, f10, -f8);
			tessellator.addVertex(f8, f10, -f8);
			tessellator.addVertex(f8, f9, -f8);
			tessellator.addVertex(-f8, f9, -f8);
			tessellator.addVertex(f8, f10, -f8);
			tessellator.addVertex(f8, f10, f8);
			tessellator.addVertex(f8, f9, f8);
			tessellator.addVertex(f8, f9, -f8);
			tessellator.addVertex(-f8, f9, -f8);
			tessellator.addVertex(-f8, f9, f8);
			tessellator.addVertex(-f8, f10, f8);
			tessellator.addVertex(-f8, f10, -f8);
			tessellator.addVertex(-f8, f10, -f8);
			tessellator.addVertex(-f8, f10, f8);
			tessellator.addVertex(f8, f10, f8);
			tessellator.addVertex(f8, f10, -f8);
			tessellator.draw();
		}

		if(world.provider.isSkyColored()) {
			GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
		} else {
			GL11.glColor3f(f1, f2, f3);
		}

		float alt = ModEventHandlerClient.altitude;
		float rnd = ModEventHandlerClient.toy;

		

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
		GL11.glCallList(this.glSkyList2);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPushMatrix();
		GL11.glTranslated(145, 75.5, -15); 
		GL11.glRotated(90.0, -10.0, -1.0, 50.0);
		GL11.glRotated(20.0, -0.0, -1.0, 1.0);

		//GL11.glRotated(90.0, -0.0, 5.0, -2.0);
		GL11.glScaled(6, 6, 6);

		GL11.glColor4d(1, 1, 1, 1);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, fl, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 2, 0F, 2, (float) alphad * 0.1F);



		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glRotated(25.0 * rnd, -12.0, 5.0 + rnd, 2.0 + rnd);
		GL11.glRotated(15.0 * rnd, -11.0, 20.0 + rnd, 1.0 + rnd);

		GL11.glTranslated(50 - rnd, alt - rnd * rnd, -10 * rnd);
		for (int i = 0; i < 17; i++) {
		GL11.glTranslated( - rnd, 0 - random.nextInt(20) , rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}

		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(45.0 * rnd, -12.0, 5.0, 0.0);

		GL11.glTranslated(-40 + rnd,alt - 50 * rnd, -90 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-90.0 * rnd, -12.0, 5.0, -20 + -45);

		GL11.glTranslated(-10 * rnd, rnd + alt - 110, -80 + rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-90.0 * 0.5961033, -12.0, 5.0, -20 + -45);
		GL11.glTranslated(80 * rnd, rnd + alt - 110, 55 + rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}
		GL11.glPopMatrix();
		
		
		GL11.glPushMatrix();
		GL11.glRotated(-25.0 - rnd, -16.0, 5.0, 0.0);
		GL11.glTranslated(-60 - rnd, alt - 150 * rnd, 20 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( - rnd, 0 - random.nextInt(15) , rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID,0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-65.0 - rnd, -12.0, 5.0, 0.0);

		GL11.glTranslated(50 + rnd, alt - 150 * rnd, -80 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( rnd, 0 - random.nextInt(15) ,- rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F);
		}
		
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST); 

		GL11.glTranslated(-35, 4.5, 100); 
		GL11.glScaled(10, 10, 10);
		GL11.glRotated(180.0, 0.0, 5.0, 0.0);
		GL11.glRotated(90.0, -12.0, 5.0, 0.0);

		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_FOG);

		GL11.glColor4f(0, 0, 0, 1);
		GL11.glDepthRange(0.0, 1.0);

		//GL11.glDepthMask(false);
		
		mc.renderEngine.bindTexture(ResourceManager.sat_rail_tex);
		ResourceManager.sat_rail.renderAll();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPopMatrix();

		
		
		GL11.glDepthMask(true);

	}

	private void renderStars() {
		Random random = new Random(10842L);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		for(int i = 0; i < 1500; ++i) {
			double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double) (0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if(d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for(int j = 0; j < 4; ++j) {
					double d17 = 0.0D;
					double d18 = (double) ((j & 2) - 1) * d3;
					double d19 = (double) ((j + 1 & 2) - 1) * d3;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
				}
			}
		}
		tessellator.draw();
	}
	//with all due respect i have zero idea how people manage to write this without any help
	  private void renderSkyboxSide(Tessellator tessellator, int side)
	  {
	    double u = side % 3 / 3.0D;
	    double v = side / 3 / 2.0D;
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV(-100.0D, -100.0D, -100.0D, u, v);
	    tessellator.addVertexWithUV(-100.0D, -100.0D, 100.0D, u, v + 0.5D);
	    tessellator.addVertexWithUV(100.0D, -100.0D, 100.0D, u + 0.3333333333333333D, v + 0.5D);
	    tessellator.addVertexWithUV(100.0D, -100.0D, -100.0D, u + 0.3333333333333333D, v);
	    tessellator.draw();
	  }
		//ASTEROID ENTRY GLOW
		public void renderGlow(ResourceLocation loc1, double x, double y, double z, float partialTicks) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			float f4 = 1.0F;
			float f5 = 0.5F;
			float f6 = 0.25F;
	        GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
	       // double distant = 1d-(Math.min(6300000f, Math.max(0d, y-40000f))/6300000f);
	      //  double sf = 1d-(Math.min(400000f, Math.max(0d, y-350000f))/400000f);
	       //
	        //double near = distant*(Math.min(40000f, Math.max(0d, y-35000f))/40000f)*Math.min(1d,Minecraft.getMinecraft().thePlayer.worldObj.getStarBrightness(partialTicks)+sf);
	        double near = 0.51d*(Math.min(40000f, Math.max(0d, y-35000d))/40000d);
	      //  System.out.println((1d-(Math.min(200d, Math.max(0d, y-2017d))/200f)));
	        double entry = near*(1d-Minecraft.getMinecraft().thePlayer.worldObj.getRainStrength(partialTicks))+(1d-(Math.min(200d, Math.max(0d, x-2017d))/200f));
			GL11.glColor4d(entry, entry, entry, entry);
			Tessellator tess = Tessellator.instance;
			TextureManager tex = Minecraft.getMinecraft().getTextureManager();
			tess.startDrawingQuads();
			tess.setNormal(0.0F, 1.0F, 0.0F);
			tess.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, 1, 0);
			tess.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, 0, 0);
			tess.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, 0, 1);
			tess.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, 1, 1);
			tex.bindTexture(loc1);
			tess.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

		}
		
		public void renderSmoke(ResourceLocation loc1, long age) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			float f4 = 1.0F;
			float f5 = 0.5F;
			float f6 = 0.25F;
			float dark = 1f - Math.min(((float)(age) / (float)(100f * 0.35F)), 1f);
	        GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glColor4d(0.6*dark+0.0, 0.6*dark+0.0, 1*dark+0.0, 1);
			Tessellator tess = Tessellator.instance;
			TextureManager tex = Minecraft.getMinecraft().getTextureManager();
			tess.startDrawingQuads();
			tess.setNormal(0.0F, 1.0F, 0.0F);
			tess.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, 1, 0);
			tess.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, 0, 0);
			tess.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, 0, 1);
			tess.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, 1, 1);
			tex.bindTexture(loc1);
			tess.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

		}
}