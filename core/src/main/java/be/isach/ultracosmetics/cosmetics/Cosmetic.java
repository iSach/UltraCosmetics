package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

/**
 * A cosmetic instance summoned by a player.
 *
 * @author iSach
 * @since 07-21-2016
 */
public abstract class Cosmetic<T extends CosmeticType<?>> extends BukkitRunnable implements Listener {
    protected static final Random RANDOM = new Random();
    private final UltraPlayer owner;
    private final Category category;
    private final UltraCosmetics ultraCosmetics;
    protected boolean equipped;
    protected final T cosmeticType;
    private final UUID ownerUniqueId;

    public Cosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner, T type) {
        this.owner = owner;
        if (owner == null
                || Bukkit.getPlayer(owner.getUUID()) == null) {
            throw new IllegalArgumentException("Invalid UltraPlayer.");
        }
        this.ownerUniqueId = owner.getUUID();
        this.category = category;
        this.ultraCosmetics = ultraCosmetics;
        this.cosmeticType = type;
    }

    public void equip() {
        if (!owner.getBukkitPlayer().hasPermission(getType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        if (!ultraCosmetics.areCosmeticsAllowedInRegion(getPlayer())) {
            getPlayer().sendMessage(MessageManager.getMessage("Region-Disabled"));
            return;
        }

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);

        this.equipped = true;

        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getActivateConfig());
        mess = filterPlaceholders(mess);
        getPlayer().sendMessage(mess);

        onEquip();
    }

    public void clear() {
        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getDeactivateConfig());
        mess = filterPlaceholders(mess);
        getPlayer().sendMessage(mess);

        HandlerList.unregisterAll(this);

        try {
            cancel();
        } catch (IllegalStateException ignored) {} // not scheduled yet

        // Call untask finally. (in main thread)
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
        return ownerUniqueId;
    }

    public T getType() {
        return cosmeticType;
    }

    protected String getTypeName() {
        return getType().getName();
    }

    protected String filterPlaceholders(String message) {
        return message.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
    }
}