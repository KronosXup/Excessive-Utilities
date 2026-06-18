package dev.aaronhowser.mods.excessive_utilities.item

import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import dev.aaronhowser.mods.excessive_utilities.item.tier.OpiniumTier
import dev.aaronhowser.mods.excessive_utilities.registry.ModAttributes
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.component.Unbreakable

class KikokuItem(properties: Properties) : SwordItem(OpiniumTier, properties) {

	override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
		return super.hurtEnemy(stack, target, attacker)
	}

	companion object {
		val RENDING_MODIFIER = ExcessiveUtilities.modResource("kikoku_rending")

		val DEFAULT_PROPERTIES: () -> Properties = {
			Properties()
				.stacksTo(1)
				.setNoRepair()
				.component(DataComponents.UNBREAKABLE, Unbreakable(false))
				.attributes(
					createAttributes(OpiniumTier, -1f, -2.4f)
						.withModifierAdded(
							ModAttributes.SOUL_RENDING,
							AttributeModifier(
								RENDING_MODIFIER,
								2.0,
								AttributeModifier.Operation.ADD_VALUE
							),
							EquipmentSlotGroup.MAINHAND
						)
				)
		}
	}

}