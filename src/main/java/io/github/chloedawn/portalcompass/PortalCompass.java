package io.github.chloedawn.portalcompass;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public final class PortalCompass {
  public static final String NAMESPACE = "portalcompass";

  private static final Multimap<World, BlockPos> PORTALS = HashMultimap.create();
  private static final Logger LOGGER = LogManager.getLogger();

  private PortalCompass() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public static void init() {
    LOGGER.debug("Registering compass item");
    Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "compass"),
      new PortalCompassItem(new Item.Settings().group(ItemGroup.TOOLS)));
  }

  static void addPortal(final World world, final BlockPos position) {
    if (!PORTALS.put(world, position)) {
      LOGGER.warn("Duplicate position {}", position);
    }
  }

  static void removePortal(final World world, final BlockPos position) {
    if (!PORTALS.remove(world, position)) {
      LOGGER.warn("No such position {}", position);
    }
  }

  static @Unmodifiable Set<BlockPos> getPositions(final World world) {
    return ImmutableSet.copyOf(PORTALS.get(world));
  }
}
