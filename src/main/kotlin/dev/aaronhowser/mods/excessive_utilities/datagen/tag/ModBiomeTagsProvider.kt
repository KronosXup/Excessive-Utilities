package dev.aaronhowser.mods.excessive_utilities.datagen.tag

import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.BiomeTagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBiomeTagsProvider(
	pOutput: PackOutput,
	pProvider: CompletableFuture<HolderLookup.Provider>,
	existingFileHelper: ExistingFileHelper
) : BiomeTagsProvider(pOutput, pProvider, ExcessiveUtilities.MOD_ID, existingFileHelper) {

	override fun addTags(provider: HolderLookup.Provider) {
		tag(BIOME_MARKER_BLACKLIST)
	}

	companion object {
		private fun create(name: String): TagKey<Biome> {
			return TagKey.create(Registries.BIOME, ExcessiveUtilities.modResource(name))
		}

		val BIOME_MARKER_BLACKLIST = create("biome_marker_blacklist")

	}

}