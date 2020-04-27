package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to save what cosmetics a player toggled.
 */
public class CosmeticsProfile {

    // Player infos
    private UltraPlayer ultraPlayer;
    private UUID uuid;

    // Saved cosmetics
    private GadgetType enabledGadget;
    private PetType enabledPet;
    private EmoteType enabledEmote;
    private HatType enabledHat;
    private MorphType enabledMorph;
    private MountType enabledMount;
    private ParticleEffectType enabledEffect;
    private Map<ArmorSlot, SuitType> enabledSuitParts = new HashMap<>();

    public CosmeticsProfile(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Loads the profile from the player file/mysql data.
     */
    public boolean loadFromData() {
        if (UltraCosmeticsData.get().usingFileStorage()) {
            SettingsManager sm = SettingsManager.getData(uuid);
            if (!sm.contains("enabled")) {
                return false;
            }
            ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
            try {
                setEnabledGadget(GadgetType.valueOf(s.getString("gadget")));
            } catch (Exception exc) {
            }
            try {
                setEnabledEffect(ParticleEffectType.valueOf(s.getString("effect")));
            } catch (Exception exc) {
            }
            try {
                setEnabledEmote(EmoteType.valueOf(s.getString("emote")));
            } catch (Exception exc) {
            }
            try {
                setEnabledHat(HatType.valueOf(s.getString("hat")));
            } catch (Exception exc) {
            }
            try {
                setEnabledMorph(MorphType.valueOf(s.getString("morph")));
            } catch (Exception exc) {
            }
            try {
                setEnabledMount(MountType.valueOf(s.getString("mount")));
            } catch (Exception exc) {
            }
            try {
                setEnabledPet(PetType.valueOf(s.getString("pet.type")));
            } catch (Exception exc) {
            }

            for (ArmorSlot slot : ArmorSlot.values()) {
                try {
                    setEnabledSuitPart(slot, SuitType.valueOf(s.getString("suit." + slot.toString().toLowerCase())));
                } catch (Exception exc) {
                }
            }

            return true;
        } else {
            // TODO MySQL
            return false;
        }
    }

    public void loadToPlayerWithoutSaving(UltraPlayer ultraPlayer) {
        if (!UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) {
            return;
        }

        this.ultraPlayer = ultraPlayer;

        if (ultraPlayer.getCosmeticsProfile() != this)
            ultraPlayer.setCosmeticsProfile(this);

        loadToPlayerWithoutSaving();
    }

    /**
     * Loads the profile and enables the cosmetics.
     */
    public void loadToPlayer() {
        if (!ultraPlayer.isOnline()) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(SmartLogger.LogLevel.ERROR, "Failed to load profile for " + ultraPlayer.getUsername() + "!");
            return;
        }

        UltraCosmetics ultraCosmetics = UltraCosmeticsData.get().getPlugin();

        // Gadget
        if (enabledGadget != null && enabledGadget.isEnabled()
                && enabledGadget.getCategory().isEnabled())
            enabledGadget.equip(ultraPlayer, ultraCosmetics);

        // Pet
        if (enabledPet != null && enabledPet.isEnabled()
                && enabledPet.getCategory().isEnabled())
            enabledPet.equip(ultraPlayer, ultraCosmetics);

        // Emote
        if (enabledEmote != null && enabledEmote.isEnabled()
                && enabledEmote.getCategory().isEnabled())
            enabledEmote.equip(ultraPlayer, ultraCosmetics);

        // Hat
        if (enabledHat != null && enabledHat.isEnabled()
                && enabledHat.getCategory().isEnabled())
            enabledHat.equip(ultraPlayer, ultraCosmetics);

        // Morph
        if (enabledMorph != null && enabledMorph.isEnabled()
                && enabledMorph.getCategory().isEnabled())
            enabledMorph.equip(ultraPlayer, ultraCosmetics);

        // Mount
        if (enabledMount != null && enabledMount.isEnabled()
                && enabledMount.getCategory().isEnabled())
            enabledMount.equip(ultraPlayer, ultraCosmetics);

        // Particle Effect
        if (enabledEffect != null && enabledEffect.isEnabled()
                && enabledEffect.getCategory().isEnabled())
            enabledEffect.equip(ultraPlayer, ultraCosmetics);

        // Suit
        for (ArmorSlot armorSlot : ArmorSlot.values()) {
            SuitType suitPart = enabledSuitParts.get(armorSlot);
            if (suitPart != null && suitPart.isEnabled()
                    && suitPart.getCategory().isEnabled())
                suitPart.equip(ultraPlayer, ultraCosmetics, armorSlot);
        }
    }

    /**
     * Loads the profile and enables the cosmetics without writing to the cosmetics profile.
     */
    public void loadToPlayerWithoutSaving() {
        if (!ultraPlayer.isOnline()) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(SmartLogger.LogLevel.ERROR, "Failed to load profile for " + ultraPlayer.getUsername() + "!");
            return;
        }

        UltraCosmetics ultraCosmetics = UltraCosmeticsData.get().getPlugin();

        // Gadget
        if (enabledGadget != null && enabledGadget.isEnabled()
                && enabledGadget.getCategory().isEnabled())
            enabledGadget.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Pet
        if (enabledPet != null && enabledPet.isEnabled()
                && enabledPet.getCategory().isEnabled())
            enabledPet.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Emote
        if (enabledEmote != null && enabledEmote.isEnabled()
                && enabledEmote.getCategory().isEnabled())
            enabledEmote.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Hat
        if (enabledHat != null && enabledHat.isEnabled()
                && enabledHat.getCategory().isEnabled())
            enabledHat.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Morph
        if (enabledMorph != null && enabledMorph.isEnabled()
                && enabledMorph.getCategory().isEnabled())
            enabledMorph.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Mount
        if (enabledMount != null && enabledMount.isEnabled()
                && enabledMount.getCategory().isEnabled())
            enabledMount.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Particle Effect
        if (enabledEffect != null && enabledEffect.isEnabled()
                && enabledEffect.getCategory().isEnabled())
            enabledEffect.equipWithoutSaving(ultraPlayer, ultraCosmetics);

        // Suit
        for (ArmorSlot armorSlot : ArmorSlot.values()) {
            SuitType suitPart = enabledSuitParts.get(armorSlot);
            if (suitPart != null && suitPart.isEnabled()
                    && suitPart.getCategory().isEnabled())
                suitPart.equipWithoutSaving(ultraPlayer, ultraCosmetics, armorSlot);
        }
    }

    public void save() {
        if (UltraCosmeticsData.get().usingFileStorage()) {
            saveToFile();
        } else {
            // TODO SQL Save
        }
    }

    /**
     * Saves the profile to file.
     */
    public void saveToFile() {
        SettingsManager settingsManager = SettingsManager.getData(uuid);

        settingsManager.set("enabled.gadget", enabledGadget != null ? enabledGadget.getConfigName() : "none");
        settingsManager.set("enabled.effect", enabledEffect != null ? enabledEffect.getConfigName() : "none");
        settingsManager.set("enabled.emote", enabledEmote != null ? enabledEmote.getConfigName() : "none");
        settingsManager.set("enabled.hat", enabledHat != null ? enabledHat.getConfigName() : "none");
        settingsManager.set("enabled.morph", enabledMorph != null ? enabledMorph.getConfigName() : "none");
        settingsManager.set("enabled.mount", enabledMount != null ? enabledMount.getConfigName() : "none");
        settingsManager.set("enabled.pet.type", enabledPet != null ? enabledPet.getConfigName() : "none");
        if(getUltraPlayer() != null && getUltraPlayer().getCurrentPet() != null) { // Save metadata attached to the pet
            settingsManager.set("enabled.pet.color", getUltraPlayer().getCurrentPet().getColorVariantString() != null ? getUltraPlayer().getCurrentPet().getColorVariantString() : "none");
        } else if(enabledPet == null) { // If pet.type is none, clear all the metadata fields as well
            settingsManager.set("enabled.pet.color", "none");
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            SuitType enabledSuitPart = enabledSuitParts.get(slot);
            settingsManager.set("enabled.suit." + slot.toString().toLowerCase(), enabledSuitPart != null ? enabledSuitPart.getConfigName() : "none");
        }
    }

    /**
     * Saves the profile to mysql.
     */
    public void saveToMySQL() {
        // TODO
    }

    /**
     * @return The corresponding UltraPlayer.
     * If the UltraPlayer is offline, returns null as
     * the UltraPlayer is unusable anymore.
     */
    public UltraPlayer getUltraPlayer() {
        if (ultraPlayer.isOnline())
            return ultraPlayer;
        ultraPlayer = null;
        return null;
    }

    public void setUltraPlayer(UltraPlayer ultraPlayer) {
        this.ultraPlayer = ultraPlayer;
    }

    /**
     * @return whether the player this profile belongs to is online or not.
     */
    public boolean isPlayerOnline() {
        return getUltraPlayer() != null;
    }

    public void setEnabledSuitPart(ArmorSlot armorSlot, SuitType suitType) {
        this.enabledSuitParts.put(armorSlot, suitType);
    }

    public SuitType getEnabledSuitParts(ArmorSlot armorSlot) {
        return enabledSuitParts.get(armorSlot);
    }

    public EmoteType getEnabledEmote() {
        return enabledEmote;
    }

    public void setEnabledEmote(EmoteType enabledEmote) {
        this.enabledEmote = enabledEmote;
    }

    public GadgetType getEnabledGadget() {
        return enabledGadget;
    }

    public void setEnabledGadget(GadgetType enabledGadget) {
        this.enabledGadget = enabledGadget;
    }

    public HatType getEnabledHat() {
        return enabledHat;
    }

    public void setEnabledHat(HatType enabledHat) {
        this.enabledHat = enabledHat;
    }

    public MorphType getEnabledMorph() {
        return enabledMorph;
    }

    public void setEnabledMorph(MorphType enabledMorph) {
        this.enabledMorph = enabledMorph;
    }

    public MountType getEnabledMount() {
        return enabledMount;
    }

    public void setEnabledMount(MountType enabledMount) {
        this.enabledMount = enabledMount;
    }

    public ParticleEffectType getEnabledEffect() {
        return enabledEffect;
    }

    public void setEnabledEffect(ParticleEffectType enabledEffect) {
        this.enabledEffect = enabledEffect;
    }

    public PetType getEnabledPet() {
        return enabledPet;
    }

    public void setEnabledPet(PetType enabledPet) {
        this.enabledPet = enabledPet;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
