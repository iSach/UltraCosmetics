package me.isach.ultracosmetics;

import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class CustomPlayer {

    public UUID uuid;
    public Gadget currentGadget = null;
    public Mount currentMount;
    public ParticleEffect currentParticleEffect;
    public Pet currentPet;
    public MenuCategory currentMenu = MenuCategory.GADGETS;

    public enum MenuCategory {
        GADGETS,
        PARTICLEEFFECTS,
        MOUNTS, PETS;
    }

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
        Core.countdownMap.put(getPlayer(), null);
        if (Core.ammoEnabled) {
            if (!Core.ammoFileStorage) {
                Core.sqlUtils.initStats(getPlayer());
            } else {
                SettingsManager.getData(getPlayer());
                for(Gadget g : Core.gadgetList) {
                    if(g.getType().isEnabled()) {
                        SettingsManager.getData(getPlayer()).addDefault("Ammo." + g.getType().toString().toLowerCase(), 0);
                    }
                }
            }
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }


    public void removeGadget() {
        if (currentGadget != null) {
            currentGadget.removeItem();
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Unequip").replaceAll("%gadgetname%", currentGadget.getName()));
            currentGadget.clear();
            currentGadget = null;
        }
    }

    public void removeMount() {
        if (currentMount != null) {
            currentMount.ent.remove();
            currentMount.clear();
            currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
    }

    public void removePet() {
        if (currentPet != null) {
            currentPet.ent.remove();
            currentPet.clear();
            currentPet = null;
        }
    }

    public void clear() {
        removeGadget();
        removeParticleEffect();
        removePet();
        removeMount();
    }

    public void removeParticleEffect() {
        if (currentParticleEffect != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Unsummon").replaceAll("%effectname%", currentParticleEffect.getName()));
            currentParticleEffect = null;
        }
    }

    public int getMoney() {
        try {
            return (int) Core.economy.getBalance(getPlayer());
        } catch (Exception exc) {
            return 0;
        }
    }

    public void addAmmo(String name, int i) {
        if (Core.ammoEnabled) {
            if (Core.ammoFileStorage) {
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) + i);
            } else {
                Core.sqlUtils.addAmmo(getPlayer(), name, i);
            }
        }
    }

    public int getAmmo(String name) {
        if (Core.ammoEnabled) {
            if (Core.ammoFileStorage) {
                return (int) SettingsManager.getData(getPlayer()).get("Ammo." + name);
            } else {
                return Core.sqlUtils.getAmmo(getPlayer(), name);
            }
        }
        return 0;
    }

    public void removeAmmo(String name) {
        if (Core.ammoEnabled) {
            if (Core.ammoFileStorage) {
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) - 1);
            } else {
                Core.sqlUtils.removeAmmo(getPlayer(), name);
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

}
