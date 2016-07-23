package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.cosmetics
 * Created by: sachalewin
 * Date: 21/07/16
 * Project: UltraCosmetics
 * <p>
 * WIP.
 * <p>
 * TODO
 */
public abstract class Cosmetic {

    private UltraPlayer owner;
    private Category category;
    private UltraCosmetics ultraCosmetics;

    public Cosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner) {
        this.owner = owner;
        this.category = category;
        this.ultraCosmetics = ultraCosmetics;
        if (owner == null
                || Bukkit.getPlayer(owner.getUuid()) == null) {
            throw new IllegalArgumentException("Invalid UltraPlayer.");
        }
    }

    public void clear() {

    }

    abstract void onEquip();
    abstract void onClear();
    abstract void onUpdate();

    public final UltraPlayer getOwner() {
        return owner;
    }

    public final UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    public final Category getCategory() {
        return category;
    }

    public final Player getPlayer() {
        return owner.getPlayer();
    }

    public final UUID getOwnerUniqueId() {
        return owner.getUuid();
    }
}
