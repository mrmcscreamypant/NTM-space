package com.hbm.dim.thatmo;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.hbm.dim.SkyProviderCelestial;
import com.hbm.lib.RefStrings;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class SkyProviderThatmo extends SkyProviderCelestial {
	private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/particle/shockwave.png");
	private static final ResourceLocation ThatmoShield = new ResourceLocation("hbm:textures/particle/cens.png");
	@Override
	public void renderSpecialEffects(float partialTicks, WorldClient world, Minecraft mc) {
		
		GL11.glPushMatrix();
        float alpha = (WorldProviderThatmo.flashd <= 0) ? 0.0F : 1.0F - Math.min(1.0F, WorldProviderThatmo.flashd / 100);

		//god awful and shouldnt even be here. ill figure out something later im so sorry
		//another thing, this is a thatmo exclusive, when the war update rolls out there will be a better way to render a beam from a sattelite
		//help me i beg
		//:(
		GL11.glTranslated(-35, 2.5, 100); 
		GL11.glScaled(10, 10, 10);
		GL11.glRotated(-63.5, 0.0, 0.0, 1.0);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, WorldProviderThatmo.flashd * 0.5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x202060, 0x202060, 0, 1, 0F, 6, (float)0.2 * 0.2F, alpha );
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, WorldProviderThatmo.flashd* 0.5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x202060, 0x202060, 0, 1, 0F, 6, (float)0.2 * 0.6F, alpha );
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, WorldProviderThatmo.flashd* 0.5, 0), EnumWaveType.RANDOM, EnumBeamType.SOLID, 0x202060, 0x202060, (int)(world.getTotalWorldTime() / 5) % 1000, 25, 0.2F, 6, (float)0.2 * 0.1F, alpha );

		GL11.glRotated(27, 00, 80, 0);
		
		GL11.glColor4f(1, 1, 1, alpha);

		GL11.glPopMatrix();
		GL11.glPushMatrix();
		float var14 = WorldProviderThatmo.flashd * 2;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glRotated(180.0, 0.0, 5.0, 0.0);
		GL11.glRotated(90.0, -12.0, 7.3F, -4.0);

		mc.renderEngine.bindTexture(this.texture);

		GL11.glColor4f(1, 1, 1, alpha);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-var14, 100.0D, -var14, 0.0D, 0.0D);
		tessellator.addVertexWithUV(var14, 100.0D, -var14, 1.0D, 0.0D);
		tessellator.addVertexWithUV(var14, 100.0D, var14, 1.0D, 1.0D);
		tessellator.addVertexWithUV(-var14, 100.0D, var14, 0.0D, 1.0D);
		tessellator.draw();

		GL11.glPopMatrix();
		
		Random random = new Random(42);
		float alt = WorldProviderThatmo.altitude;
		float rnd = WorldProviderThatmo.randPos;
		float fl = WorldProviderThatmo.scale;
		float nl = WorldProviderThatmo.shield;
		float el = WorldProviderThatmo.nmass;
		float xcl = WorldProviderThatmo.shielde;
		float xcvl = WorldProviderThatmo.csyw;

		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		float alphad = 1.0F - Math.min(1.0F, el / 100);
		float alpd = 1.0F - Math.min(1.0F, xcvl / 100);
		

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPushMatrix();
		GL11.glTranslated(145, 75.5, -15); 
		GL11.glRotated(90.0, -10.0, -1.0, 50.0);
		GL11.glRotated(20.0, -0.0, -1.0, 1.0);

		//GL11.glRotated(90.0, -0.0, 5.0, -2.0);
		GL11.glScaled(6, 6, 6);

		GL11.glColor4d(1, 1, 1, 1);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, fl, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 2, 0F, 2, (float) alphad * 0.1F, 0.5F);



		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glRotated(25.0 * rnd, -12.0, 5.0 + rnd, 2.0 + rnd);
		GL11.glRotated(15.0 * rnd, -11.0, 20.0 + rnd, 1.0 + rnd);

		GL11.glTranslated(50 - rnd, alt - rnd * rnd, -10 * rnd);
		for (int i = 0; i < 17; i++) {
		GL11.glTranslated( - rnd, 0 - random.nextInt(20) , rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}

		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(45.0 * rnd, -12.0, 5.0, 0.0);

		GL11.glTranslated(-40 + rnd,alt - 50 * rnd, -90 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-90.0 * rnd, -12.0, 5.0, -20 + -45);

		GL11.glTranslated(-10 * rnd, rnd + alt - 110, -80 + rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-90.0 * 0.5961033, -11.0, 5.0, -20 + -45);
		GL11.glTranslated(80 * rnd, rnd + alt - 110, 55 + rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( -rnd, 0 - random.nextInt(20) ,- 1 - rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}
		GL11.glPopMatrix();
		
		
		GL11.glPushMatrix();
		GL11.glRotated(-25.0 - rnd, -16.0, 5.0, 0.0);
		GL11.glTranslated(-60 - rnd, alt - 150 * rnd, 20 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( - rnd, 0 - random.nextInt(15) , rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID,0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glRotated(-65.0 - rnd, -12.0, 5.0, 0.0);

		GL11.glTranslated(50 + rnd, alt - 150 * rnd, -80 * rnd);

		for (int i = 0; i < 17; i++) {
			GL11.glTranslated( rnd, 0 - random.nextInt(15) ,- rnd);
		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 5, 0), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0xFF9000, 0xFF9000, 0, 1, 0F, 6, (float)0.2 * 0.2F, 0.5F);
		}
		GL11.glPopMatrix();
	}

}
