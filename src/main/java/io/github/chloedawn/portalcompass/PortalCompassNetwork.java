package io.github.chloedawn.portalcompass;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public final class PortalCompassNetwork {
  public static final Identifier REMOVE_PORTAL = new Identifier(PortalCompass.NAMESPACE, "remove_portal");
  public static final Identifier ADD_PORTAL = new Identifier(PortalCompass.NAMESPACE, "add_portal");
  public static final Identifier SET_PORTALS = new Identifier(PortalCompass.NAMESPACE, "set_portals");

  private static final ServerSidePacketRegistry SERVER = ServerSidePacketRegistry.INSTANCE;
  private static final Logger LOGGER = LogManager.getLogger();

  private PortalCompassNetwork() {
    throw new UnsupportedOperationException();
  }

  public static void portalRemoved(final World world, final BlockPos position) {
    PortalCompass.removePortal(world, position);
    PlayerStream.world(world).forEach(p -> SERVER.sendToPlayer(p, REMOVE_PORTAL, encode(position)));
  }

  public static void portalAdded(final World world, final BlockPos position) {
    PortalCompass.addPortal(world, position);
    PlayerStream.world(world).forEach(p -> SERVER.sendToPlayer(p, ADD_PORTAL, encode(position)));
  }

  public static void playerLevelChanged(final World world, final ServerPlayerEntity player) {
    SERVER.sendToPlayer(player, SET_PORTALS, encode(PortalCompass.getPositions(world)));
  }

  private static PacketByteBuf encode(final BlockPos position) {
    LOGGER.debug("Encoding block position {}", position);
    final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBlockPos(position);
    return buf;
  }

  private static PacketByteBuf encode(final Set<BlockPos> positions) {
    LOGGER.debug("Encoding block positions {}", positions);
    final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(positions.size());
    positions.forEach(buf::writeBlockPos);
    return buf;
  }
}
