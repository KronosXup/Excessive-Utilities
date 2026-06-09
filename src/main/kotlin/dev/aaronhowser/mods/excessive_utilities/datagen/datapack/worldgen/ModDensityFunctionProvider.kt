package dev.aaronhowser.mods.excessive_utilities.datagen.datapack.worldgen

import com.mojang.serialization.MapCodec
import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.util.KeyDispatchDataCodec
import net.minecraft.world.level.levelgen.DensityFunction

object ModDensityFunctionProvider {

	val DEEP_DARK = rk("deep_dark")

	fun bootstrap(context: BootstrapContext<DensityFunction>) {
		context.register(
			DEEP_DARK,
			DeepDarkDensityFunction()
		)
	}


	private fun rk(name: String): ResourceKey<DensityFunction> {
		return ResourceKey.create(Registries.DENSITY_FUNCTION, ExcessiveUtilities.modResource(name))
	}

	class DeepDarkDensityFunction : DensityFunction.SimpleFunction {

		override fun compute(context: DensityFunction.FunctionContext): Double {
			val y = context.blockY().toDouble()

			return when {
				y < 62.0 -> 1.0
				y < 120.0 -> -1.0
				else -> 1.0
			}
		}

		override fun minValue(): Double = -1.0
		override fun maxValue(): Double = 1.0

		override fun codec(): KeyDispatchDataCodec<out DensityFunction?> {
			return CODEC
		}

		companion object {
			val CODEC: KeyDispatchDataCodec<DeepDarkDensityFunction> =
				KeyDispatchDataCodec(MapCodec.unit(DeepDarkDensityFunction()))
		}
	}

}