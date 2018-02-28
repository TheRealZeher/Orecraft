package com.zeher.orecraft.production.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.*;

import org.lwjgl.opengl.GL11;

import com.zeher.orecraft.OreCraft;
import com.zeher.orecraft.client.model.ModelBlockHeatedFluidGenerator;
import com.zeher.orecraft.transfer.tileentity.pipe.TileEntityPoweredEnergyPipe;

@SideOnly(Side.CLIENT)
public class RendererHeatedFluidGeneratorBasic extends TileEntitySpecialRenderer {

	@SideOnly(Side.CLIENT)
	private ModelBlockHeatedFluidGenerator model;

	@SideOnly(Side.CLIENT)
	public RendererHeatedFluidGeneratorBasic() {
		this.model = new ModelBlockHeatedFluidGenerator();
	}

	private static final ResourceLocation texture = new ResourceLocation("orecraft:textures/render/production/generator/heatedfluid/heatedfluid_center.png");

	public void renderAModel(TileEntity tileentity, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glDisable(3008);
		this.model.renderModel(0.0625F);
		GL11.glEnable(3008);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTicks, int destroyStage) {
		renderAModel(tileentity, x, y, z);
	}

}