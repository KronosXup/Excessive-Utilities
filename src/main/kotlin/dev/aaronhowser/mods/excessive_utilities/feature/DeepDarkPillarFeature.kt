package dev.aaronhowser.mods.excessive_utilities.feature

import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import kotlin.math.sqrt

class DeepDarkPillarFeature : Feature<NoneFeatureConfiguration>(NoneFeatureConfiguration.CODEC) {

	override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>): Boolean {
		val origin = context.origin()
		val level = context.level()

		val height = 200
		val minRadius = 15
		val maxRadius = 25

		val mutable = BlockPos.MutableBlockPos()

		val centerX = origin.x
		val centerZ = origin.z

		for (dy in 0 until height) {
			val y = origin.y + dy

			val percentUp = dy.toDouble() / height
			val radius = Mth.lerp(percentUp, minRadius.toDouble(), maxRadius.toDouble())

			val maxRad = Mth.ceil(radius)

			for (dx in -maxRad..maxRad) {
				val x = centerX + dx

				for (dz in -maxRad..maxRad) {
					val dist = sqrt((dx * dx.toFloat()) + (dz * dz))
					if (dist > radius) continue

					val z = centerZ + dz

					mutable.set(x, y, z)
					level.setBlock(mutable, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS)
				}
			}

		}

		return true
	}

}