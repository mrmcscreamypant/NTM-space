package com.hbm.render.entity.mob;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderTankbot extends RenderLiving {

	public static final ResourceLocation glyphid_infested_tex = new ResourceLocation(RefStrings.MODID, "textures/entity/glyphid_infestation.png");
	
	public RenderTankbot() {
		super(new ModelTankbot(), 1.0F);
		this.shadowOpaque = 0.0F;
		this.setRenderPassModel(this.mainModel);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ResourceManager.tankbot_tex;
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float interp) {
		if(pass != 0) {
			return -1;
		}
			return -1;
	}
	
	public static class ModelTankbot extends ModelBase {
		
		double bite = 0;

		@Override
		public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float interp) {
			bite = entity.getSwingProgress(interp);
		}

		@Override
		public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotationYaw, float rotationHeadYaw, float rotationPitch, float scale) {
			GL11.glPushMatrix();

			GL11.glRotatef(180, 1, 0, 0);
			GL11.glTranslatef(0, -1.5F, 0);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			this.renderModel(entity, limbSwing);
			
			GL11.glPopMatrix();
		}
		
		public void renderModel(Entity entity, float limbSwing) {
			
			GL11.glPushMatrix();
			
			double s = 2;
			GL11.glScaled(s, s, s);
			
			EntityLivingBase living = (EntityLivingBase) entity;
			
			double walkCycle = limbSwing;

			double cy0 = Math.sin(walkCycle % (Math.PI * 2) * 2);
			double cy1 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.62);
			double cy2 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI);
			double cy3 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.75);
			double cy4 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 1.25);

			double bite = MathHelper.clamp_double(Math.sin(this.bite * Math.PI * 2 - Math.PI * 0.5), 0, 1) * 20;
			double headTilt = Math.sin(this.bite * Math.PI) * 30;
			
			ResourceManager.tankbot.renderPart("body");
			
			/// LEFT ARM ///
			GL11.glPushMatrix();
			
			GL11.glPushMatrix();

			GL11.glTranslated(0.25, 0.625, 0.0625);
			GL11.glRotated(10, 0, 1, 0);
			GL11.glRotated(-cy3 * 10, 0, 1, 0);
			GL11.glRotated(cy2 * -2, 0, 0, 1);
			GL11.glTranslated(-0.25, -0.625, -0.0625);
			ResourceManager.tankbot.renderPart("front2foot");
			ResourceManager.tankbot.renderPart("front2leg");
			ResourceManager.tankbot.renderPart("front2knee");
			GL11.glPopMatrix();
			GL11.glPushMatrix();

			GL11.glTranslated(0.25, 0.625, 0.4375);
			GL11.glRotated(cy3 - cy2 * 10, 0, 1, 0);
			GL11.glRotated(cy2 - cy3 * -2, 0, 0, 1);
			GL11.glTranslated(-0.25, -0.625, -0.4375);
			ResourceManager.tankbot.renderPart("front1foot");
			ResourceManager.tankbot.renderPart("front1leg");
			ResourceManager.tankbot.renderPart("front1knee");
			GL11.glPopMatrix();

			GL11.glPushMatrix();

			GL11.glTranslated(0.25, 0.625, 0.9375);
			GL11.glRotated(cy3 - cy2 * 10, 0, 1, 0);
			GL11.glRotated(cy2 - cy3 * -2, 0, 0, 1);

			GL11.glTranslated(-0.25, -0.625, -0.9375);
			ResourceManager.tankbot.renderPart("back1foot");
			ResourceManager.tankbot.renderPart("back1leg");
			ResourceManager.tankbot.renderPart("back1knee");
			GL11.glPopMatrix();

			GL11.glPushMatrix();

			GL11.glTranslated(0.25, 0.625, 0.9375);
			GL11.glRotated(cy2 - cy3 * 5, 0, 1, 0);
			GL11.glRotated(cy2 * 2, 1, 0, 0);

			GL11.glTranslated(-0.25, -0.625, -0.9375);
			ResourceManager.tankbot.renderPart("back2foot");
			ResourceManager.tankbot.renderPart("back2leg");
			ResourceManager.tankbot.renderPart("back2knee");
			GL11.glPopMatrix();
			
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();

			GL11.glRotated(bite, 0, 1, 0);
			GL11.glRotated(-90, 0, 1, 0);

			ResourceManager.tankbot.renderPart("head");
			ResourceManager.tankbot.renderPart("barrelbottom");
			ResourceManager.tankbot.renderPart("barreltop");

			GL11.glPopMatrix();



			
			GL11.glPopMatrix();
		}
	}
}
