package dev.aaronhowser.mods.excessive_utilities.datagen.datapack

import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object ModDamageTypeProvider {

	private fun createRk(name: String): ResourceKey<DamageType> =
		ResourceKey.create(Registries.DAMAGE_TYPE, ExcessiveUtilities.modResource(name))

	val DOOM = createRk("doom")
	val DARKNESS = createRk("darkness")

	fun bootstrap(context: BootstrapContext<DamageType>) {
		context.register(
			DOOM,
			DamageType(
				"eu_doom",
				0f
			)
		)

		context.register(
			DARKNESS,
			DamageType(
				"eu_darkness",
				0f
			)
		)
	}

}