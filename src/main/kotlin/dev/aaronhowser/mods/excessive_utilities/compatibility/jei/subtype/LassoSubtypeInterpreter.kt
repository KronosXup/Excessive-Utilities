package dev.aaronhowser.mods.excessive_utilities.compatibility.jei.subtype

import dev.aaronhowser.mods.excessive_utilities.registry.ModDataComponents
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter
import mezz.jei.api.ingredients.subtypes.UidContext
import net.minecraft.world.item.ItemStack

object LassoSubtypeInterpreter : ISubtypeInterpreter<ItemStack> {

	override fun getSubtypeData(ingredient: ItemStack, context: UidContext): Any? {
		return ingredient.get(ModDataComponents.ENTITY_TYPE)
	}

	@Deprecated("Deprecated in Java")
	override fun getLegacyStringSubtypeInfo(ingredient: ItemStack, context: UidContext): String {
		val entityType = ingredient.get(ModDataComponents.ENTITY_TYPE) ?: return ""
		return entityType.toString()
	}
}