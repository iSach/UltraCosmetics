package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.TextUtil;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * A cosmetic instance summoned by a player.
 *
 * @author iSach
 * @since 07-21-2016
 */
public abstract class Cosmetic<T extends CosmeticType> extends BukkitRunnable implements Listener {
    private UltraPlayer owner;
    private Category category;
    private UltraCosmetics ultraCosmetics;
    protected boolean equipped;
    private T cosmeticType;
    private UUID ownerUniqueId;

    public Cosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner, T type) {
        this.owner = owner;
        this.ownerUniqueId = owner.getUuid();
        this.category = category;
        this.ultraCosmetics = ultraCosmetics;
        this.cosmeticType = type;
        if (owner == null) {
            throw new IllegalArgumentException("Invalid UltraPlayer.");
        }
    }

    public void equip() { // TODO: Handle permissions and this NPC check correctly.
        if (!owner.getBukkitPlayer().hasPermission(getType().getPermission()) && !CitizensAPI.getNPCRegistry().isNPC(Bukkit.getEntity(ownerUniqueId))) { // Check if owner has correct permissions AND IS NOT AN NPC.
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);

        this.equipped = true;

        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getActivateConfig());
        if (category == Category.PETS && cosmeticType instanceof PetType && owner.getPetName((PetType) cosmeticType) != null) {
            mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics())
                    + " " + ChatColor.GRAY + "(" + owner.getPetName((PetType) cosmeticType) + ChatColor.GRAY + ")");
        } else {
            mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
        }
        getPlayer().sendMessage(mess);

        onEquip();
    }

    public void clear() {
        // Send unequip Message.
        try {
            String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getDeactivateConfig());
            if (category == Category.PETS && cosmeticType instanceof PetType && owner.getPetName((PetType) cosmeticType) != null) {
                mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics())
                        + " " + ChatColor.GRAY + "(" + owner.getPetName((PetType) cosmeticType) + ChatColor.GRAY + ")");
            } else {
                mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
            }
            getPlayer().sendMessage(mess);
        } catch (Exception ignored) {
        }

        // unregister listener.
        HandlerList.unregisterAll(this);

        try {
            // Cancel task.
            cancel();
        } catch (Exception ignored) {
            // Not Scheduled yet. Ignore.
        }

        // Call untask finally.
        onClear();

        owner = null;
    }

    @Override
    public void run() {
    }

    protected abstract void onEquip();

    protected abstract void onClear();

    public final UltraPlayer getOwner() {
        if (owner == null) {
            // Try to fix.
            try {
                owner = getUltraCosmetics().getPlayerManager().getUltraPlayer((Player)Bukkit.getEntity(getOwnerUniqueId()));
            } catch (Exception exc) {

            }
        }
        return owner;
    }

    public final UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    public final Category getCategory() {
        return category;
    }

    public final Player getPlayer() {
        if (owner == null) {
            return null;
        }
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
}