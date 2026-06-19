package dev.aaronhowser.mods.excessive_utilities.compatibility.jei.extension

import dev.aaronhowser.mods.aaron.client.AaronClientUtil
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.isItem
import dev.aaronhowser.mods.excessive_utilities.item.UnstableIngotItem
import dev.aaronhowser.mods.excessive_utilities.recipe.crafting.ShapedUnstableRecipe
import dev.aaronhowser.mods.excessive_utilities.registry.ModDataComponents
import dev.aaronhowser.mods.excessive_utilities.registry.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeHolder

class ShapedUnstableRecipeExtension : ICraftingCategoryExtension<ShapedUnstableRecipe> {

	override fun setRecipe(
		recipeHolder: RecipeHolder<ShapedUnstableRecipe>,
		builder: IRecipeLayoutBuilder,
		craftingGridHelper: ICraftingGridHelper,
		focuses: IFocusGroup
	) {
		val registry = AaronClientUtil.localLevel?.registryAccess() ?: return

		val recipe = recipeHolder.value
		val result = recipe.getResultItem(registry)

		craftingGridHelper.createAndSetOutputs(builder, listOf(result))
		craftingGridHelper.createAndSetIngredients(builder, getDisplayIngredients(recipe), recipe.width, recipe.height)
	}

	override fun isHandled(recipeHolder: RecipeHolder<ShapedUnstableRecipe>): Boolean {
		return recipeHolder.value.isSpecial
	}

	private fun getDisplayIngredients(recipe: ShapedUnstableRecipe): List<Ingredient> {
		return recipe.ingredients.map { ingredient ->
			val stacks = ingredient.items
			if (stacks.none { it.isItem(ModItems.UNSTABLE_INGOT) }) return@map ingredient

			Ingredient.of(
				stacks.flatMap { stack ->
					if (!stack.isItem(ModItems.UNSTABLE_INGOT)) return@flatMap listOf(stack)

					when (recipe.requiredStability) {
						ShapedUnstableRecipe.Stability.STABLE -> listOf(stableIngot())
						ShapedUnstableRecipe.Stability.UNSTABLE -> listOf(unstableIngot())
						ShapedUnstableRecipe.Stability.EITHER -> listOf(stableIngot(), unstableIngot())
					}
				}.stream()
			)
		}
	}

	private fun stableIngot(): ItemStack {
		return ModItems.UNSTABLE_INGOT.toStack()
	}

	private fun unstableIngot(): ItemStack {
		val stack = stableIngot()
		stack.set(ModDataComponents.COUNTDOWN, UnstableIngotItem.MAX_COUNTDOWN)
		stack.set(ModDataComponents.CRAFTED_IN_MENU, UnstableIngotItem.VANILLA_CRAFTING_MENU_ID)
		return stack
	}

}
