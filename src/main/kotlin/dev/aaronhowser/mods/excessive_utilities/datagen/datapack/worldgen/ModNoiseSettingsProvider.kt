package dev.aaronhowser.mods.excessive_utilities.datagen.datapack.worldgen

import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunctions
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseRouter
import net.minecraft.world.level.levelgen.NoiseSettings
import net.minecraft.world.level.levelgen.SurfaceRules

object ModNoiseSettingsProvider {

	val DEEP_DARK = rk("deep_dark")

	fun bootstrap(context: BootstrapContext<NoiseGeneratorSettings>) {
		val densityFunction = context.lookup(Registries.DENSITY_FUNCTION)

		val deepDarkDensity = densityFunction.getOrThrow(ModDensityFunctionProvider.DEEP_DARK)

		val settings = NoiseGeneratorSettings(
			NoiseSettings(0, 256, 1, 1),
			Blocks.STONE.defaultBlockState(),
			Blocks.AIR.defaultBlockState(),
			NoiseRouter(
				deepDarkDensity.value(),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
				DensityFunctions.constant(0.0),
			),
			SurfaceRules.sequence(),
			listOf(),
			0,
			false,
			false,
			false,
			false
		)

		context.register(DEEP_DARK, settings)
	}

	private fun rk(name: String): ResourceKey<NoiseGeneratorSettings> {
		return ResourceKey.create(Registries.NOISE_SETTINGS, ExcessiveUtilities.modResource(name))
	}

}