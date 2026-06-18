package dev.aaronhowser.mods.excessive_utilities.client.render.block_entity

import com.mojang.blaze3d.vertex.PoseStack
import dev.aaronhowser.mods.aaron.client.AaronClientUtil
import dev.aaronhowser.mods.aaron.client.render.AaronRenderUtil
import dev.aaronhowser.mods.excessive_utilities.block_entity.QedBlockEntity
import dev.aaronhowser.mods.excessive_utilities.config.ServerConfig
import dev.aaronhowser.mods.excessive_utilities.registry.ModBlocks
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB

class QedBER(
	val context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<QedBlockEntity> {

	override fun render(
		blockEntity: QedBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val player = AaronClientUtil.localPlayer ?: return
		if (!player.isHolding(ModBlocks.ENDER_FLUX_CRYSTAL.asItem())) return

		val radius = ServerConfig.CONFIG.qedRadius.get().toDouble()
		val minX = -radius
		val minY = -radius
		val minZ = -radius
		val maxX = radius + 1
		val maxY = radius + 1
		val maxZ = radius + 1

		AaronRenderUtil.renderCubeWireframe(
			poseStack,
			minX, minY, minZ,
			maxX, maxY, maxZ,
			color = 0xFF12BCA3.toInt()
		)

	}

	override fun getRenderBoundingBox(blockEntity: QedBlockEntity): AABB {
		val radius = ServerConfig.CONFIG.qedRadius.get().toDouble()
		return AABB(blockEntity.blockPos).inflate(radius)
	}

}
