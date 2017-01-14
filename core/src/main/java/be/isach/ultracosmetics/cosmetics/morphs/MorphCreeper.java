package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * 
 * 
 * @author 	iSach
 * @since 	08-26-2015
 */
public class MorphCreeper extends Morph {

    private int charge = 0;

    public MorphCreeper(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.CREEPER, ultraCosmetics);

        if (owner != null) {
            CreeperWatcher creeperWatcher = (CreeperWatcher) disguise.getWatcher();

            final MorphCreeper creeper = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || getOwner().getCurrentMorph() != creeper) {
                        cancel();
                        return;
                    }
                    final CreeperWatcher creeperWatcher = (CreeperWatcher) disguise.getWatcher();
                    if (getPlayer().isSneaking()) {
                        creeperWatcher.setIgnited(true);
                        if (charge + 4 <= 100)
                            charge += 4;
                        SoundUtil.playSound(getPlayer(), Sounds.CREEPER_HISS, 1.4f, 1.5f);
                    } else {
                        if (creeperWatcher.isIgnited()) {
                            disguise = new MobDisguise(getType().getDisguiseType());
                            DisguiseAPI.disguiseToAll(getPlayer(), disguise);
                            //  disguise.setShowName(true);
                            if (!getOwner().canSeeSelfMorph())
                                disguise.setViewSelfDisguise(false);
                        }
                        if (charge == 100) {
                            UtilParticles.display(Particles.EXPLOSION_HUGE, getPlayer().getLocation());
                            SoundUtil.playSound(getPlayer(), Sounds.EXPLODE, 1.4f, 1.5f);

                            for (Entity ent : getPlayer().getNearbyEntities(3, 3, 3)) {
                                if (ent instanceof Creature || ent instanceof Player) {
                                    double dX = getPlayer().getLocation().getX() - ent.getLocation().getX();
                                    double dY = getPlayer().getLocation().getY() - ent.getLocation().getY();
                                    double dZ = getPlayer().getLocation().getZ() - ent.getLocation().getZ();
                                    double yaw = Math.atan2(dZ, dX);
                                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                                    double X = Math.sin(pitch) * Math.cos(yaw);
                                    double Y = Math.sin(pitch) * Math.sin(yaw);
                                    double Z = Math.cos(pitch);

                                    Vector vector = new Vector(X, Z, Y);
                                    MathUtils.applyVelocity(ent, vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
                                }
                            }
                            UltraCosmeticsData.get().getVersionManager().getActionBarUtil().sendActionMessage(getPlayer(), "");
                            charge = 0;
                            return;
                        }
                        if (charge > 0)
                            charge -= 4;
                    }
                    if (charge > 0 && charge < 100) {
                        if (charge < 5) {
                            UltraCosmeticsData.get().getVersionManager().getActionBarUtil().sendActionMessage(getPlayer(), "");
                        } else
                            UltraCosmeticsData.get().getVersionManager().getActionBarUtil().sendActionMessage(getPlayer(), MessageManager.getMessage("Morphs.Creeper.charging").replace("%chargelevel%", charge + ""));
                    } else if (charge == 100)
                        UltraCosmeticsData.get().getVersionManager().getActionBarUtil().sendActionMessage(getPlayer(), MessageManager.getMessage("Morphs.Creeper.release-to-explode"));
                }
            }.runTaskTimer(getUltraCosmetics(), 0, 1);
        }
    }

    @Override
    protected void onEquip() {
    }
}
