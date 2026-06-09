package dev.aaronhowser.mods.excessive_utilities.datagen.datapack.worldgen

import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BiomeDefaultFeatures
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.features.OreFeatures
import net.minecraft.data.worldgen.placement.OrePlacements
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.biome.BiomeSpecialEffects
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraft.world.level.levelgen.GenerationStep

object ModBiomes {

	val DEEP_DARK = rk("deep_dark")

	fun bootstrap(context: BootstrapContext<Biome>) {

		val placedFeatures = context.lookup(Registries.PLACED_FEATURE)
		val configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER)

		val biomeGenerationSettingsBuilder = BiomeGenerationSettings.Builder(
			placedFeatures,
			configuredCarvers
		)

		BiomeDefaultFeatures.addDefaultOres(biomeGenerationSettingsBuilder)
		BiomeDefaultFeatures.addFossilDecoration(biomeGenerationSettingsBuilder)

		val biome = Biome.BiomeBuilder()
			.hasPrecipitation(false)
			.temperature(0.8f)
			.downfall(0f)
			.specialEffects(
				BiomeSpecialEffects.Builder()
					.fogColor(0x010101)
					.waterColor(4159204)
					.waterFogColor(329011)
					.skyColor(0)
					.build()
			)
			.mobSpawnSettings(
				MobSpawnSettings.Builder().build()
			)
			.generationSettings(
				biomeGenerationSettingsBuilder.build()
			)
			.build()

		context.register(DEEP_DARK, biome)
	}

	private fun rk(path: String): ResourceKey<Biome> {
		return ResourceKey.create(Registries.BIOME, ExcessiveUtilities.modResource(path))
	}

}