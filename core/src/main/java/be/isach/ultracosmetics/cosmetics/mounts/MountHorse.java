package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

/**
 * Created by sacha on 11/01/17.
 */
// Handles mounts that are real horses, but also variant horses on 1.8
// For variant horses on 1.9+ see MountAbstractHorse
public abstract class MountHorse extends Mount<Horse> {

    public MountHorse(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setup() {
        // setColor has no effect on variant horses so skip if it's a variant horse
        if (getVariant() == null) {
            entity.setColor(getColor());
        } else {
            // Setting variant twice makes it work better with ViaVersion
            entity.setVariant(getVariant());
            entity.setVariant(getVariant());
        }
        entity.setTamed(true);
        entity.setDomestication(1);
        entity.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        entity.setJumpStrength(0.7);
    }

    @Override
    public void onUpdate() {}

    abstract protected Horse.Color getColor();

    @SuppressWarnings("deprecation")
    protected Horse.Variant getVariant() {
        return null;
    }
}
