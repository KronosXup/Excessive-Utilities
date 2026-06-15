package dev.aaronhowser.mods.excessive_utilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.aaronhowser.mods.excessive_utilities.handler.CursedEarthHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

	@Redirect(
			method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
			)
	)
	private void eu$tintCursedEntity(
			EntityModel<LivingEntity> model,
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			int packedLight,
			int packedOverlay,
			int color,
			LivingEntity entity,
			float entityYaw,
			float partialTick,
			PoseStack originalPoseStack,
			MultiBufferSource bufferSource,
			int originalPackedLight
	) {
		model.renderToBuffer(
				poseStack,
				vertexConsumer,
				packedLight,
				packedOverlay,
				CursedEarthHandler.INSTANCE.isCursed(entity) ? CursedEarthHandler.darkenColor(color) : color
		);
	}

}
