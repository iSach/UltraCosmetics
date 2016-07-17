package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:44
 */
public class Emote {

    private UUID owner;

    private EmoteType emoteType;

    private EmoteAnimation animation;

    private boolean equipped;

    private ItemStack itemStack;

    public Emote(final UUID owner, final EmoteType emoteType) {
        this.emoteType = emoteType;

        if (owner == null) return;

        this.owner = owner;
        this.equipped = false;
        this.animation = new EmoteAnimation(getEmoteType().getTicksPerFrame(), this);

        if (!getPlayer().hasPermission(getEmoteType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }
        UltraCosmetics.getCustomPlayer(getPlayer()).setEmote(this);
    }

    public void equip() {
        animation.start();
        this.equipped = true;
    }

    public void clear() {
        animation.stop();
        getPlayer().getInventory().setHelmet(null);
        this.equipped = false;
    }

    public boolean isEquipped() {
        return equipped;
    }

    /**
     * @return EmoteType.
     */
    public EmoteType getEmoteType() {
        return emoteType;
    }

    public ItemStack getItemStack() { return itemStack; }

    protected void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Get the pet owner.
     *
     * @return the UUID of the owner.
     */
    protected final UUID getOwner() {
        return owner;
    }

    /**
     * Get the player owner.
     *
     * @return The player from getOwner.
     */
    protected final Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public String getName() {
        return getEmoteType().getName();
    }
}
