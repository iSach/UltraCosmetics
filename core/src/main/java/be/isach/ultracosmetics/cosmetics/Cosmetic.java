package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.cosmetics
 * Created by: sachalewin
 * Date: 21/07/16
 * Project: UltraCosmetics
 */
public abstract class Cosmetic<T extends CosmeticType> implements Listener {

    private UltraPlayer owner;
    private Category category;
    private UltraCosmetics ultraCosmetics;
    private boolean equipped;
    private T cosmeticType;

    public Cosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner, T type) {
        this.owner = owner;
        this.category = category;
        this.ultraCosmetics = ultraCosmetics;
        this.cosmeticType = type;
        if (owner == null
                || Bukkit.getPlayer(owner.getUuid()) == null) {
            throw new IllegalArgumentException("Invalid UltraPlayer.");
        }

        if (!owner.getPlayer().hasPermission(type.getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            clear();
        }

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);
    }

    public final void clear() {

        // Send unequip Message.
        // getPlayer().sendMessage();

        // unregister listener.
        HandlerList.unregisterAll(this);

        onClear();
    }

    protected abstract void onEquip();

    protected abstract void onClear();

    public final UltraPlayer getOwner() {
        return owner;
    }

    public final UltraCosmetics getUcInstance() {
        return ultraCosmetics;
    }

    public final Category getCategory() {
        return category;
    }

    public final Player getPlayer() {
        return owner.getPlayer();
    }

    public boolean isEquipped() {
        return equipped;
    }

    public final UUID getOwnerUniqueId() {
        return owner.getUuid();
    }

    public T getCosmeticType() {
        return cosmeticType;
    }
}
