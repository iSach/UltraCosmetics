package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * A cosmetic instance summoned by a player.
 * 
 * @author 	iSach
 * @since 	07-21-2016
 */
public abstract class Cosmetic<T extends CosmeticType> extends BukkitRunnable implements Listener {

    private UltraPlayer owner;
    private Category category;
    private UltraCosmetics ultraCosmetics;
    protected boolean equipped;
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

        if (!owner.getBukkitPlayer().hasPermission(type.getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);
    }

    public void equip() {
        this.equipped = true;

        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getActivateConfig());
        mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
        getPlayer().sendMessage(mess);

        onEquip();
    }

    public void clear() {

        // Send unequip Message.
        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getDeactivateConfig());
        mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
        getPlayer().sendMessage(mess);

        // unregister listener.
        HandlerList.unregisterAll(this);

        // Cancel task.
        cancel();

        // Call untask finally.
        onClear();
    }

    @Override
    public void run() {
    }

    protected abstract void onEquip();

    protected abstract void onClear();

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
        return owner.getBukkitPlayer();
    }

    public boolean isEquipped() {
        return equipped;
    }

    public final UUID getOwnerUniqueId() {
        return owner.getUuid();
    }

    public T getType() {
        return cosmeticType;
    }

    protected String getTypeName() {
        return getType().getName();
    }
}