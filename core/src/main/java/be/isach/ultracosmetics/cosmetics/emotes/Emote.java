package be.isach.ultracosmetics.cosmetics.emotes;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import org.bukkit.inventory.ItemStack;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.cosmetics.emotes
 * Created by: Sacha
 * Created on: 17th June, 2016
 * at 02:44
 */
public class Emote extends Cosmetic<EmoteType> {

    private EmoteAnimation animation;

    private ItemStack itemStack;

    public Emote(UltraPlayer owner, final EmoteType emoteType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.EMOTES, owner, emoteType);

        this.animation = new EmoteAnimation(getType().getTicksPerFrame(), this);

        owner.setCurrentEmote(this);
    }

    @Override
    protected void onEquip() {
        animation.start();
    }

    @Override
    protected void onClear() {
        animation.stop();
        getPlayer().getInventory().setHelmet(null);
    }

    public ItemStack getItemStack() { return itemStack; }

    protected void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
