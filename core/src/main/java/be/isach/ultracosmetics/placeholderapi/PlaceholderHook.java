package be.isach.ultracosmetics.placeholderapi;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI hook.
 *
 * @author RadBuilder
 * @since 2.5
 */
public class PlaceholderHook extends PlaceholderExpansion {

    private UltraCosmetics ultraCosmetics;

    public PlaceholderHook(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        if (identifier.startsWith("ammo_")) {
            String gadget = identifier.substring(5);
            GadgetType type = GadgetType.valueOf(gadget);
            if (type == null) return null;
            return String.valueOf(ultraPlayer.getAmmo(type));
        }
        switch (identifier) {
            // Current cosmetics
            case "current_gadget":
                return ultraPlayer.getCurrentGadget() == null ? "None" : ultraPlayer.getCurrentGadget().getType().getName();
            case "current_mount":
                return ultraPlayer.getCurrentMount() == null ? "None" : ultraPlayer.getCurrentMount().getType().getName();
            case "current_particleeffect":
                return ultraPlayer.getCurrentParticleEffect() == null ? "None" : ultraPlayer.getCurrentParticleEffect().getType().getName();
            case "current_pet":
                return ultraPlayer.getCurrentPet() == null ? "None" : ultraPlayer.getCurrentPet().getType().getName();
            case "current_morph":
                return ultraPlayer.getCurrentMorph() == null ? "None" : ultraPlayer.getCurrentMorph().getType().getName();
            case "current_hat":
                return ultraPlayer.getCurrentHat() == null ? "None" : ultraPlayer.getCurrentHat().getType().getName();
            case "current_emote":
                return ultraPlayer.getCurrentEmote() == null ? "None" : ultraPlayer.getCurrentEmote().getType().getName();
            case "current_suit_helmet":
                return ultraPlayer.getSuit(ArmorSlot.HELMET) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.HELMET).getType().getName();
            case "current_suit_chestplate":
                return ultraPlayer.getSuit(ArmorSlot.CHESTPLATE) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.CHESTPLATE).getType().getName();
            case "current_suit_leggings":
                return ultraPlayer.getSuit(ArmorSlot.LEGGINGS) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.LEGGINGS).getType().getName();
            case "current_suit_boots":
                return ultraPlayer.getSuit(ArmorSlot.BOOTS) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.BOOTS).getType().getName();

            // Balance, keys, and user-specific settings
            case "balance":
                return "" + ultraPlayer.getBalance();
            case "keys":
                return "" + ultraPlayer.getKeys();
            case "gadgets_enabled":
                return "" + ultraPlayer.hasGadgetsEnabled();
            case "morph_selfview":
                return "" + ultraPlayer.canSeeSelfMorph();
            case "treasurechest_active":
                return "" + (ultraPlayer.getCurrentTreasureChest() != null);
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return "ultracosmetics";
    }

    @Override
    public String getAuthor() {
        return ultraCosmetics.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return ultraCosmetics.getDescription().getVersion();
    }
}
