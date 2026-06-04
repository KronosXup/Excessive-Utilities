package dev.aaronhowser.mods.excessive_utilities.packet.server_to_client

import dev.aaronhowser.mods.aaron.packet.AaronPacket
import dev.aaronhowser.mods.excessive_utilities.ExcessiveUtilities
import dev.aaronhowser.mods.excessive_utilities.item.AngelRingItem
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.*
import kotlin.jvm.optionals.getOrNull

class UpdatePlayerWingPacket(
	val playerUuid: UUID,
	val wingType: Optional<AngelRingItem.Type>
) : AaronPacket() {

	constructor(player: Player, wingType: AngelRingItem.Type?) : this(player.uuid, Optional.ofNullable(wingType))

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
		return TYPE
	}

	override fun handleOnClient(context: IPayloadContext) {
		val type = wingType.getOrNull()
		if (type == null) {
			AngelRingItem.PLAYER_WINGS.remove(playerUuid)
		} else {
			AngelRingItem.PLAYER_WINGS[playerUuid] = type
		}
	}

	companion object {
		val TYPE: CustomPacketPayload.Type<UpdatePlayerWingPacket> =
			makeType(ExcessiveUtilities.MOD_ID, "update_player_wing")

		val STREAM_CODEC: StreamCodec<ByteBuf, UpdatePlayerWingPacket> =
			StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, UpdatePlayerWingPacket::playerUuid,
				ByteBufCodecs.optional(AngelRingItem.Type.STREAM_CODEC), UpdatePlayerWingPacket::wingType,
				::UpdatePlayerWingPacket
			)
	}

}