package io.github.chloedawn.portalcompass.mixin;

import io.github.chloedawn.portalcompass.PortalCompassNetwork;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
@SuppressWarnings("UnresolvedMixinReference")
abstract class ServerWorldMixin extends World {
  ServerWorldMixin() {
    //noinspection ConstantConditions
    super(null, null, null, null, false);
  }

  @Inject(method = "method_19536", at = @At("HEAD"))
  private void poiRemoved(final BlockPos position, final PointOfInterestType type, final CallbackInfo callback) {
    if (type == PointOfInterestType.NETHER_PORTAL) {
      PortalCompassNetwork.portalRemoved(this, position);
    }
  }

  @Inject(method = "method_19535", at = @At("HEAD"))
  private void poiAdded(final BlockPos position, final PointOfInterestType type, final CallbackInfo callback) {
    if (type == PointOfInterestType.NETHER_PORTAL) {
      PortalCompassNetwork.portalAdded(this, position);
    }
  }

  @Inject(method = { "onPlayerTeleport", "onPlayerChangeDimension", "onPlayerConnected", "onPlayerRespawned" }, at = @At("TAIL"))
  private void playerLevelChanged(final ServerPlayerEntity player, final CallbackInfo callback) {
    PortalCompassNetwork.playerLevelChanged(this, player);
  }
}
