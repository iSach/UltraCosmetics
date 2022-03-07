package be.isach.ultracosmetics.placeholderapi;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
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
                return ultraPlayer.getSuit(ArmorSlot.HELMET) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.HELMET).getType().getName(ArmorSlot.HELMET);
            case "current_suit_chestplate":
                return ultraPlayer.getSuit(ArmorSlot.CHESTPLATE) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.CHESTPLATE).getType().getName(ArmorSlot.CHESTPLATE);
            case "current_suit_leggings":
                return ultraPlayer.getSuit(ArmorSlot.LEGGINGS) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.LEGGINGS).getType().getName(ArmorSlot.LEGGINGS);
            case "current_suit_boots":
                return ultraPlayer.getSuit(ArmorSlot.BOOTS) == null ? "None" : ultraPlayer.getSuit(ArmorSlot.BOOTS).getType().getName(ArmorSlot.BOOTS);

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

            /*
             * // Gadget ammo case "ammo_batblaster": return "" +
             * ultraPlayer.getAmmo("BatBlaster"); case "ammo_chickenator": return "" +
             * ultraPlayer.getAmmo("Chickenator"); case "ammo_colorbomb": return "" +
             * ultraPlayer.getAmmo("ColorBomb"); case "ammo_discoball": return "" +
             * ultraPlayer.getAmmo("DiscoBall"); case "ammo_etherealpearl": return "" +
             * ultraPlayer.getAmmo("EtherealPearl"); case "ammo_fleshhook": return "" +
             * ultraPlayer.getAmmo("FleshHook"); case "ammo_melonthrower": return "" +
             * ultraPlayer.getAmmo("MelonThrower"); case "ammo_blizzardblaster": return "" +
             * ultraPlayer.getAmmo("BlizzardBlaster"); case "ammo_portalgun": return "" +
             * ultraPlayer.getAmmo("PortalGun"); case "ammo_explosivesheep": return "" +
             * ultraPlayer.getAmmo("ExplosiveSheep"); case "ammo_paintballgun": return "" +
             * ultraPlayer.getAmmo("PaintballGun"); case "ammo_thorhammer": return "" +
             * ultraPlayer.getAmmo("ThorHammer"); case "ammo_antigravity": return "" +
             * ultraPlayer.getAmmo("AntiGravity"); case "ammo_smashdown": return "" +
             * ultraPlayer.getAmmo("SmashDown"); case "ammo_rocket": return "" +
             * ultraPlayer.getAmmo("Rocket"); case "ammo_blackhole": return "" +
             * ultraPlayer.getAmmo("BlackHole"); case "ammo_tsunami": return "" +
             * ultraPlayer.getAmmo("Tsunami"); case "ammo_tnt": return "" +
             * ultraPlayer.getAmmo("TNT"); case "ammo_fungun": return "" +
             * ultraPlayer.getAmmo("FunGun"); case "ammo_parachute": return "" +
             * ultraPlayer.getAmmo("Parachute"); case "ammo_quakegun": return "" +
             * ultraPlayer.getAmmo("QuakeGun"); case "ammo_firework": return "" +
             * ultraPlayer.getAmmo("Firework"); case "ammo_christmastree": return "" +
             * ultraPlayer.getAmmo("ChristmasTree"); case "ammo_freezecannon": return "" +
             * ultraPlayer.getAmmo("FreezeCannon"); case "ammo_snowball": return "" +
             * ultraPlayer.getAmmo("Snowball"); case "ammo_partypopper": return "" +
             * ultraPlayer.getAmmo("PartyPopper"); case "ammo_trampoline": return "" +
             * ultraPlayer.getAmmo("Trampoline");
             */
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
