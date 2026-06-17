package dev.aaronhowser.mods.excessive_utilities.feature

import dev.aaronhowser.mods.excessive_utilities.datagen.datapack.worldgen.DeepDarkConstants
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class DeepDarkPillarFeature : Feature<NoneFeatureConfiguration>(NoneFeatureConfiguration.CODEC) {

	override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>): Boolean {
		val origin = context.origin()
		val level = context.level()
		val chunkX = floor(origin.x / 16.0).toInt()
		val chunkZ = floor(origin.z / 16.0).toInt()
		val regionX = chunkX shr 2
		val regionZ = chunkZ shr 2

		val regionRandom = RandomSource.create(level.seed + regionX * 65535L + regionZ)
		val spireX = regionX * 64 + 8 + regionRandom.nextInt(48)
		val spireZ = regionZ * 64 + 8 + regionRandom.nextInt(48)

		if (spireX shr 4 != chunkX || spireZ shr 4 != chunkZ) return false

		var placed = false
		val blockRandom = RandomSource.create(chunkX * 341873128712L + chunkZ * 132897987541L)
		val mutablePos = BlockPos.MutableBlockPos()

		for (x in (chunkX shl 4)..((chunkX shl 4) + 15)) {
			for (z in (chunkZ shl 4)..((chunkZ shl 4) + 15)) {
				val dx = spireX - x
				val dz = spireZ - z
				val distanceSquared = dx * dx + dz * dz
				if (distanceSquared >= 256) continue

				val spireDistance = sqrt(distanceSquared.toDouble())

				for (y in (DeepDarkConstants.FLOOR_TOP + 1) until DeepDarkConstants.CEILING_BOTTOM) {
					val distanceToShell = min(y - DeepDarkConstants.FLOOR_TOP, DeepDarkConstants.CEILING_BOTTOM - y)
					var threshold = spireDistance

					if (distanceToShell < 9) {
						threshold -= sqrt(9.0 - distanceToShell)
					}

					if (threshold <= 4 || threshold <= 5 && blockRandom.nextBoolean()) {
						mutablePos.set(x, y, z)
						if (level.isEmptyBlock(mutablePos) || level.getBlockState(mutablePos).canBeReplaced()) {
							level.setBlock(mutablePos, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS)
							placed = true
						}
					}
				}
			}
		}

		return placed
	}

}
