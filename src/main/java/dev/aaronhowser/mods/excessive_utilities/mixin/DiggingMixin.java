package dev.aaronhowser.mods.excessive_utilities.mixin;

import dev.aaronhowser.mods.excessive_utilities.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Digging;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Digging.class)
public class DiggingMixin<E extends Warden> {

	@Inject(
			method = "stop(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V",
			at = @At("HEAD")
	)
	private void eu$spawnWake(ServerLevel level, E entity, long gameTime, CallbackInfo ci) {
		var eyePos = entity.getEyePosition();
		ItemEntity itemEntity = new ItemEntity(level, eyePos.x, eyePos.y, eyePos.z, new ItemStack(ModItems.WARDENS_WAKE.get()));
		level.addFreshEntity(itemEntity);
	}
}
