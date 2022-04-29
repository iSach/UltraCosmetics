package be.isach.ultracosmetics.cosmetics.mounts.abstracthorse;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.inventory.ItemStack;

// Class for mounts that are like horses but not normal horses
public abstract class MountAbstractHorse extends Mount {

    public MountAbstractHorse(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        AbstractHorse horse = (AbstractHorse) entity;
        horse.setTamed(true);
        horse.setDomestication(1);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setJumpStrength(0.7);
    }
}
