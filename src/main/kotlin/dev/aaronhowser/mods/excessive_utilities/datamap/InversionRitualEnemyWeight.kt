package dev.aaronhowser.mods.excessive_utilities.datamap

import com.mojang.serialization.Codec
import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.registries.datamaps.DataMapType

class InversionRitualEnemyWeight(
	val weight: Double
) {

	companion object {
		val CODEC: Codec<InversionRitualEnemyWeight> =
			Codec.DOUBLE.xmap(::InversionRitualEnemyWeight, InversionRitualEnemyWeight::weight)

		val DATA_MAP: DataMapType<EntityType<*>, InversionRitualEnemyWeight> =
			DataMapType
				.builder(
					ExcessiveUtilities.modResource("inversion_ritual_enemy_weight"),
					Registries.ENTITY_TYPE,
					CODEC
				)
				.synced(CODEC, true)
				.build()

	}

}