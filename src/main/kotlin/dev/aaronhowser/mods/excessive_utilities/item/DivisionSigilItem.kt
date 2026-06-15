package dev.aaronhowser.mods.excessive_utilities.item

import dev.aaronhowser.mods.aaron.misc.AaronExtensions.getDirectionName
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.isBlock
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.isHolder
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.isItem
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.tell
import dev.aaronhowser.mods.aaron.misc.AaronExtensions.toComponent
import dev.aaronhowser.mods.excessive_utilities.datagen.language.ModItemLang
import dev.aaronhowser.mods.excessive_utilities.datagen.language.ModMenuLang
import dev.aaronhowser.mods.excessive_utilities.datagen.language.ModMessageLang
import dev.aaronhowser.mods.excessive_utilities.datagen.tag.ModItemTagsProvider
import dev.aaronhowser.mods.excessive_utilities.handler.division_sigil.DivisionSigilActivation
import dev.aaronhowser.mods.excessive_utilities.registry.ModDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.items.IItemHandler

class DivisionSigilItem(properties: Properties) : Item(properties) {

	override fun useOn(context: UseOnContext): InteractionResult {
		val level = context.level
		if (level.isClientSide) return InteractionResult.PASS

		val player = context.player ?: return InteractionResult.SUCCESS
		val pos = context.clickedPos
		val stack = context.itemInHand

		if (isInverted(stack)) return InteractionResult.PASS

		if (checkActivationReady(player, pos)) return InteractionResult.SUCCESS
		if (checkInversionReady(player, pos)) return InteractionResult.SUCCESS

		return InteractionResult.PASS
	}

	override fun getName(stack: ItemStack): Component {
		return if (isInverted(stack)) {
			ModItemLang.PSEUDO_INVERSION_SIGIL.toComponent()
		} else {
			super.getName(stack)
		}
	}

	override fun isFoil(stack: ItemStack): Boolean {
		return isInverted(stack)
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		if (isInverted(stack)) {
			tooltipComponents += ModMenuLang.INFINITE_USES.toComponent()
			return
		}

		val remainingUses = stack.getOrDefault(ModDataComponents.REMAINING_USES, 0)
		tooltipComponents += ModMenuLang.REMAINING_USES.toComponent(remainingUses)
	}

	override fun isBarVisible(stack: ItemStack): Boolean {
		return !isInverted(stack)
	}

	override fun getBarWidth(stack: ItemStack): Int {
		val maxWidth = 13
		if (isInverted(stack)) return maxWidth

		val remainingUses = stack.getOrDefault(ModDataComponents.REMAINING_USES, 0)
		return (remainingUses * maxWidth) / USES_AFTER_ACTIVATION
	}

	companion object {
		const val USES_AFTER_ACTIVATION = 256
		private const val CHEST_HORIZONTAL_OFFSET = 5

		fun defaultProperties(): Properties {
			return Properties()
				.stacksTo(1)
				.fireResistant()
				.component(ModDataComponents.REMAINING_USES, USES_AFTER_ACTIVATION)
		}

		fun isInverted(stack: ItemStack): Boolean {
			return !stack.has(ModDataComponents.REMAINING_USES)
		}

		private fun checkActivationReady(
			player: Player,
			pos: BlockPos
		): Boolean {
			val level = player.level() as? ServerLevel ?: return false
			if (!level.getBlockState(pos).isBlock(Blocks.ENCHANTING_TABLE)) return false

			val result = DivisionSigilActivation.isValidSetup(level, pos)
			sendMessages(player, result.messages)
			return result.isReady
		}

		private fun checkInversionReady(
			player: Player,
			pos: BlockPos
		): Boolean {
			val level = player.level() as? ServerLevel ?: return false
			if (!level.getBlockState(pos).isBlock(Blocks.BEACON)) return false

			val result = getInversionResult(level, pos)
			sendMessages(player, result.messages)
			return result.isReady
		}

		private fun sendMessages(
			player: Player,
			messages: List<Component>
		) {
			for (message in messages) {
				player.tell(message)
			}
		}

		private fun getInversionResult(
			level: ServerLevel,
			beaconPos: BlockPos
		): DivisionSigilActivation.ActivationResult {
			val result = DivisionSigilActivation.ActivationResult(isReady = true)

			if (!level.isLoaded(beaconPos)) {
				result.isReady = false
				return result
			}

			if (!level.getBlockState(beaconPos).isBlock(Blocks.BEACON)) {
				result.isReady = false
				return result
			}

			checkInversionBiome(level, beaconPos, result)
			if (!result.isReady) {
				return result
			}

			checkInversionChests(level, beaconPos, result)
			if (!result.isReady) {
				return result
			}

			checkInversionPattern(level, beaconPos, result)
			if (!result.isReady) {
				return result
			}

			checkInversionItemContents(level, beaconPos, result)
			if (!result.isReady) {
				return result
			}

			result.addMessages(
				ModMessageLang.INVERSION_READY_ONE.toComponent(),
				ModMessageLang.INVERSION_READY_TWO.toComponent()
			)

			return result
		}

		private fun checkInversionBiome(
			level: ServerLevel,
			beaconPos: BlockPos,
			result: DivisionSigilActivation.ActivationResult
		) {
			if (!level.getBiome(beaconPos).isHolder(Tags.Biomes.IS_END)) {
				result.failWithMessages(ModMessageLang.INVERSION_END_ONLY.toComponent())
			}
		}

		private fun checkInversionChests(
			level: ServerLevel,
			beaconPos: BlockPos,
			result: DivisionSigilActivation.ActivationResult
		) {
			for (direction in Direction.Plane.HORIZONTAL) {
				val checkPos = beaconPos.offset(direction.normal.multiply(CHEST_HORIZONTAL_OFFSET))
				val itemHandler = level.getCapability(
					Capabilities.ItemHandler.BLOCK,
					checkPos,
					direction.opposite
				)

				if (itemHandler == null) {
					result.failWithMessages(
						ModMessageLang.INVERSION_MISSING_CHEST.toComponent(direction.getDirectionName())
					)
				}
			}
		}

		private fun checkInversionPattern(
			level: ServerLevel,
			beaconPos: BlockPos,
			result: DivisionSigilActivation.ActivationResult
		) {
			// ◼ = redstone wire, ◻ = tripwire (string), B = beacon (center, skipped)
			val patternRows = listOf(
				"◼◻◻◻◻◻◻◻◻",
				"◼◻◼◼◼◼◼◼◼",
				"◼◻◼◻◻◻◻◻◼",
				"◼◻◼◻◼◼◼◻◼",
				"◼◻◼◻B◻◼◻◼",
				"◼◻◼◼◼◻◼◻◼",
				"◼◻◻◻◻◻◼◻◼",
				"◼◼◼◼◼◼◼◻◼",
				"◻◻◻◻◻◻◻◻◼",
			)

			val centerRow = 4
			val centerColumn = 4

			for ((rowIndex, rowString) in patternRows.withIndex()) {
				val northOffset = rowIndex - centerRow
				for ((columnIndex, character) in rowString.withIndex()) {
					val westOffset = columnIndex - centerColumn
					val checkPos = beaconPos.north(northOffset).west(westOffset)
					val checkState = level.getBlockState(checkPos)

					if (character == '◼' && !checkState.isBlock(Blocks.REDSTONE_WIRE)) {
						result.failWithMessages(
							ModMessageLang.INVERSION_MISSING_REDSTONE.toComponent(checkPos.x, checkPos.y, checkPos.z)
						)

						return
					}

					if (character == '◻' && !checkState.isBlock(Blocks.TRIPWIRE)) {
						result.failWithMessages(
							ModMessageLang.INVERSION_MISSING_STRING.toComponent(checkPos.x, checkPos.y, checkPos.z)
						)

						return
					}
				}
			}
		}

		private fun checkInversionItemContents(
			level: ServerLevel,
			beaconPos: BlockPos,
			result: DivisionSigilActivation.ActivationResult
		) {
			val contentRequirements = mapOf(
				Direction.NORTH to ModItemTagsProvider.CHILDREN_OF_FIRE,
				Direction.SOUTH to ModItemTagsProvider.GIFTS_OF_EARTH,
				Direction.EAST to ModItemTagsProvider.DESCENDANTS_OF_WATER,
				Direction.WEST to ModItemTagsProvider.SPICES_OF_AIR,
			)

			for ((direction, itemTag) in contentRequirements) {
				val checkPos = beaconPos.offset(direction.normal.multiply(CHEST_HORIZONTAL_OFFSET))

				val itemHandler = level.getCapability(
					Capabilities.ItemHandler.BLOCK,
					checkPos,
					direction.opposite
				) ?: continue

				val count = if (itemTag == ModItemTagsProvider.DESCENDANTS_OF_WATER) {
					// Potions share the same Item class, so count stacks rather than unique items
					countMatchingStacks(itemHandler, itemTag)
				} else {
					countUniqueMatchingItems(itemHandler, itemTag)
				}

				val requiredUniqueCount = 12
				if (count < requiredUniqueCount) {
					result.failWithMessages(
						ModMessageLang.INVERSION_MISSING_ITEMS.toComponent(
							requiredUniqueCount,
							itemTag.location.toString(),
							direction.getDirectionName(),
							count
						)
					)
				}
			}
		}

		private fun countMatchingStacks(
			itemHandler: IItemHandler,
			itemTag: TagKey<Item>
		): Int {
			var count = 0
			for (slot in 0 until itemHandler.slots) {
				val stack = itemHandler.getStackInSlot(slot)
				if (stack.isItem(itemTag)) {
					count += stack.count
				}
			}
			return count
		}

		private fun countUniqueMatchingItems(
			itemHandler: IItemHandler,
			itemTag: TagKey<Item>
		): Int {
			val uniqueItems = mutableSetOf<Item>()
			for (slot in 0 until itemHandler.slots) {
				val stack = itemHandler.getStackInSlot(slot)
				if (stack.isItem(itemTag)) {
					uniqueItems.add(stack.item)
				}
			}
			return uniqueItems.size
		}

	}

}