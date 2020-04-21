package io.github.chloedawn.portalcompass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Set;

final class PortalCompassClient {
  private static final Set<BlockPos> PORTALS = new HashSet<>(0);
  private static final Logger LOGGER = LogManager.getLogger();

  private PortalCompassClient() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public static void init() {
    LOGGER.debug("Registering client packet handlers");
    final ClientSidePacketRegistry client = ClientSidePacketRegistry.INSTANCE;
    client.register(PortalCompassNetwork.REMOVE_PORTAL, PortalCompassClient::removePortal);
    client.register(PortalCompassNetwork.ADD_PORTAL, PortalCompassClient::addPortal);
    client.register(PortalCompassNetwork.SET_PORTALS, PortalCompassClient::setPortals);
  }

  @Environment(EnvType.CLIENT)
  static @Nullable BlockPos nearestPortal() {
    final @Nullable ClientPlayerEntity player = MinecraftClient.getInstance().player;
    if (player != null) {
      final BlockPos origin = player.getBlockPos();
      @Nullable BlockPos nearest = null;
      double nearestSq = Double.POSITIVE_INFINITY;
      for (final BlockPos position : PORTALS) {
        if (origin.getSquaredDistance(position) < nearestSq) {
          nearestSq = origin.getSquaredDistance(position);
          nearest = position;
        }
      }
      return nearest;
    }
    return null;
  }

  static void removePortal(final PacketContext context, final PacketByteBuf buf) {
    final BlockPos position = buf.readBlockPos();
    context.getTaskQueue().execute(() -> removePortal(position));
  }

  static void addPortal(final PacketContext context, final PacketByteBuf buf) {
    final BlockPos position = buf.readBlockPos();
    context.getTaskQueue().execute(() -> addPortal(position));
  }

  static void setPortals(final PacketContext context, final PacketByteBuf buf) {
    final int size = buf.readInt();
    final Set<BlockPos> portals = new HashSet<>(size);
    for (int i = 0; i < size; ++i) {
      portals.add(buf.readBlockPos());
    }
    context.getTaskQueue().execute(() -> setPortals(portals));
  }

  private static void removePortal(final BlockPos position) {
    if (!PORTALS.remove(position)) {
      LOGGER.warn("No such position {}", position);
    }
  }

  private static void addPortal(final BlockPos position) {
    if (!PORTALS.add(position)) {
      LOGGER.warn("Duplicate position {}", position);
    }
  }

  private static void setPortals(final Set<BlockPos> positions) {
    PORTALS.clear();
    for (final BlockPos position : positions) {
      addPortal(position);
    }
  }
}
