package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.player.UltraPlayer;
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
            if (s.isString("gadget")) {
                setEnabledGadget(GadgetType.valueOf(s.getString("gadget")));
            }
            if (s.isString("effect")) {
                setEnabledEffect(ParticleEffectType.valueOf(s.getString("effect")));
            }
            if (s.isString("emote")) {
                setEnabledEmote(EmoteType.valueOf(s.getString("emote")));
            }
            if (s.isString("hat")) {
                setEnabledHat(HatType.valueOf(s.getString("hat")));
            }
            if (s.isString("morph")) {
                setEnabledMorph(MorphType.valueOf(s.getString("morph")));
            }
            if (s.isString("mount")) {
                setEnabledMount(MountType.valueOf(s.getString("mount")));
            }
            if (s.isString("pet")) {
                setEnabledPet(PetType.valueOf(s.getString("pet")));
            }

            for (ArmorSlot slot : ArmorSlot.values()) {
                String key = "suit." + slot.toString().toLowerCase();
                if (s.isString(key)) {
                    setEnabledSuitPart(slot, SuitType.getSuitPart(s.getString(key), slot));
                }
            }

            return true;
        } else {
            // TODO MySQL
            return false;
        }
    }

    public void loadToPlayer(UltraPlayer ultraPlayer) {
        if (!UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) {
            return;
        }

        this.ultraPlayer = ultraPlayer;

        if (ultraPlayer.getCosmeticsProfile() != this)
            ultraPlayer.setCosmeticsProfile(this);

        loadToPlayer();
    }

    /**
     * Loads the profile and enabled the cosmetics.
     */
    public void loadToPlayer() {
        if (!ultraPlayer.isOnline()) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(SmartLogger.LogLevel.ERROR, "Failed to load profile for " + ultraPlayer.getUsername() + "!");
            return;
        }

        UltraCosmetics ultraCosmetics = UltraCosmeticsData.get().getPlugin();

        // Gadget
        if (enabledGadget != null
                && enabledGadget.getCategory().isEnabled()
                && enabledGadget.isEnabled())
            enabledGadget.equip(ultraPlayer, ultraCosmetics);

        // Pet
        if (enabledPet != null
                && enabledPet.getCategory().isEnabled()
                && enabledPet.isEnabled())
            enabledPet.equip(ultraPlayer, ultraCosmetics);

        // Emote
        if (enabledEmote != null
                && enabledEmote.getCategory().isEnabled()
                && enabledEmote.isEnabled())
            enabledEmote.equip(ultraPlayer, ultraCosmetics);

        // Hat
        if (enabledHat != null
                && enabledHat.getCategory().isEnabled()
                && enabledHat.isEnabled())
            enabledHat.equip(ultraPlayer, ultraCosmetics);

        // Morph
        if (enabledMorph != null
                && enabledMorph.getCategory().isEnabled()
                && enabledMorph.isEnabled())
            enabledMorph.equip(ultraPlayer, ultraCosmetics);

        // Mount
        if (enabledMount != null
                && enabledMount.getCategory().isEnabled()
                && enabledMount.isEnabled())
            enabledMount.equip(ultraPlayer, ultraCosmetics);

        // Particle Effect
        if (enabledEffect != null
                && enabledEffect.getCategory().isEnabled()
                && enabledEffect.isEnabled())
            enabledEffect.equip(ultraPlayer, ultraCosmetics);

        // Suit
        for (ArmorSlot armorSlot : ArmorSlot.values()) {
            SuitType suitPart = enabledSuitParts.get(armorSlot);
            if (suitPart != null
                    && suitPart.getCategory().isEnabled()
                    && suitPart.isEnabled())
                suitPart.equip(ultraPlayer, ultraCosmetics);
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
        settingsManager.set("enabled.pet", enabledPet != null ? enabledPet.getConfigName() : "none");
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
