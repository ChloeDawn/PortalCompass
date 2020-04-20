package io.github.chloedawn.portalcompass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

final class PortalCompassItem extends Item {
  PortalCompassItem(final Settings settings) {
    super(settings);
    this.addPropertyGetter(new Identifier("portalcompass", "angle"), new ItemPropertyGetter() {
      private double angle;
      private double step;
      private long lastTick;

      @Environment(EnvType.CLIENT)
      public float call(final ItemStack stack, @Nullable World world, final @Nullable LivingEntity holder) {
        if (holder == null && !stack.isInFrame()) {
          return 0.0F;
        }
        final boolean isHeld = holder != null;
        final Entity entity = isHeld ? holder : Objects.requireNonNull(stack.getFrame());
        if (world == null) {
          world = entity.world;
        }
        double needle = getNeedleAngle(isHeld, entity);
        if (isHeld) {
          needle = this.nextAngle(world, needle);
        }
        return MathHelper.floorMod((float) needle, 1.0F);
      }

      private double nextAngle(final World world, final double needle) {
        if (world.getTime() != this.lastTick) {
          this.lastTick = world.getTime();
          double delta = needle - this.angle;
          delta = MathHelper.floorMod(delta + 0.5, 1.0) - 0.5;
          this.step += delta * 0.1;
          this.step *= 0.8;
          this.angle = MathHelper.floorMod(this.angle + this.step, 1.0);
        }
        return this.angle;
      }

      private double getFrameYaw(final ItemFrameEntity frame) {
        return MathHelper.wrapDegrees(180 + frame.getHorizontalFacing().getHorizontal() * 90);
      }

      private double getNeedleAngle(final boolean isHeld, final Entity entity) {
        double yaw = isHeld ? (double) entity.yaw : this.getFrameYaw((ItemFrameEntity) entity);
        yaw = MathHelper.floorMod(yaw / 360.0, 1.0);
        final @Nullable BlockPos nearest = PortalCompassClient.nearestPortal();
        if (nearest != null) {
          final double angle = Math.atan2(nearest.getZ() - entity.getZ(), nearest.getX() - entity.getX()) / (Math.PI * 2.0);
          return 0.5 - (yaw - 0.25 - angle);
        } else {
          return Math.random();
        }
      }
    });
  }
}
