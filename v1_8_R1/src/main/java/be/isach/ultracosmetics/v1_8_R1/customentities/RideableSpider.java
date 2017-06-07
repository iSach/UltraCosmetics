package be.isach.ultracosmetics.v1_8_R1.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.util.BlockUtils;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EnchantmentManager;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntitySpider;
import net.minecraft.server.v1_8_R1.LocaleI18n;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.craftbukkit.v1_8_R1.util.UnsafeList;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

/**
 * @author RadBuilder
 */
public class RideableSpider extends EntitySpider implements IMountCustomEntity {

	boolean isOnGround;

	public RideableSpider(World world) {
		super(world);
	}

	@Override
	public void removeAi() {
		removeSelectors();
	}

	private void removeSelectors() {
		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * WASD Control.
	 *
	 * @param sideMot
	 * @param forMot
	 */
	@Override
	public void g(float sideMot, float forMot) {
		if (!CustomEntities.customEntities.contains(this)) {
			super.g(sideMot, forMot);
			return;
		}
		if (this.passenger != null && this.passenger instanceof EntityHuman
		    && CustomEntities.customEntities.contains(this)) {
			this.lastYaw = this.yaw = this.passenger.yaw;
			this.pitch = this.passenger.pitch * 0.5F;
			this.setYawPitch(this.yaw, this.pitch); //Update the pitch and yaw
			this.aG = this.aE = this.yaw;
			sideMot = ((EntityLiving) this.passenger).aX * 0.5F;
			forMot = ((EntityLiving) this.passenger).aY;

			Field jump = null; //Jumping
			try {
				jump = EntityLiving.class.getDeclaredField("aW");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
			jump.setAccessible(true);

			if (jump != null && BlockUtils.isOnGround(this.getBukkitEntity())) {
				try {
					if (jump.getBoolean(this.passenger)) {
						this.motY = 0.3D;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			this.S = 1.0F;
			this.aI = this.yaw;
			if (!this.world.isStatic) {
				this.j(0.2f);

				if (bL()) {
					if (V()) {
						double d0 = locY;
						float f3 = 0.8F;
						float f4 = 0.02F;
						float f2 = EnchantmentManager.b(this);
						if (f2 > 3.0F) {
							f2 = 3.0F;
						}

						if (f2 > 0.0F) {
							f3 += (0.5460001F - f3) * f2 / 3.0F;
							f4 += (bH() * 1.0F - f4) * f2 / 3.0F;
						}

						a(sideMot, forMot, f4);
						move(motX, motY, motZ);
						motX *= f3;
						motY *= 0.800000011920929D;
						motZ *= f3;
						motY -= 0.02D;
						if ((positionChanged) && (c(motX, motY + 0.6000000238418579D - locY + d0, motZ)))
							motY = 0.300000011920929D;
					} else if (ab()) {
						double d0 = locY;
						a(sideMot, forMot, 0.02F);
						move(motX, motY, motZ);
						motX *= 0.5D;
						motY *= 0.5D;
						motZ *= 0.5D;
						motY -= 0.02D;
						if ((positionChanged) && (c(motX, motY + 0.6000000238418579D - locY + d0, motZ)))
							motY = 0.300000011920929D;
					} else {
						float f5 = world.getType(new BlockPosition(MathHelper.floor(locX), MathHelper.floor(getBoundingBox().b) - 1, MathHelper.floor(locZ))).getBlock().frictionFactor * 0.91F;

						float f6 = 0.1627714F / (f5 * f5 * f5);
						float f3 = bH() * f6;

						a(sideMot, forMot, f3);
						f5 = world.getType(new BlockPosition(MathHelper.floor(locX), MathHelper.floor(getBoundingBox().b) - 1, MathHelper.floor(locZ))).getBlock().frictionFactor * 0.91F;

						if (j_()) {
							float f4 = 0.15F;
							motX = MathHelper.a(motX, -f4, f4);
							motZ = MathHelper.a(motZ, -f4, f4);
							fallDistance = 0.0F;
							if (motY < -0.15D) {
								motY = -0.15D;
							}

							if (motY < 0.0D) {
								motY = 0.0D;
							}
						}

						move(motX, motY, motZ);
						if ((positionChanged) && (j_())) {
							motY = 0.2D;
						}

						if ((world.isStatic) && ((!world.isLoaded(new BlockPosition((int) locX, 0, (int) locZ))) || (!world.getChunkAtWorldCoords(new BlockPosition((int) locX, 0, (int) locZ)).o()))) {
							if (locY > 0.0D)
								motY = -0.1D;
							else
								motY = 0.0D;
						} else {
							motY += 0D;
						}

						motY *= 0.9800000190734863D;
						motX *= f5;
						motZ *= f5;
					}
				}

				aw = ax;
				double d0 = locX - lastX;
				double d1 = locZ - lastZ;

				float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
				if (f2 > 1.0F) {
					f2 = 1.0F;
				}

				ax += (f2 - ax) * 0.4F;
				ay += ax;

				super.g(sideMot, forMot);
			}


			this.aw = this.ax;
			double d0 = this.locX - this.lastX;
			double d1 = this.locZ - this.lastZ;
			float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
			if (f4 > 1.0F) {
				f4 = 1.0F;
			}

			this.ax += (f4 - this.ax) * 0.4F;
			this.ay += this.ax;
		} else {
			this.S = 0.5F;
			this.aI = 0.02F;
			super.g(sideMot, forMot);
		}
	}

	@Override
	public String getName() {
		return LocaleI18n.get("entity.Spider.name");
	}

	@Override
	public Entity getEntity() {
		return getBukkitEntity();
	}
}
