package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.util.*;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureRandomizer {
    private static final int MONEY_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Chance");
    private static final int GADGET_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets.Chance");
    private static final int AMMO_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets-Ammo.Chance");
    private static final int MORPHS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Morphs.Chance");
    private static final int PETS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Pets.Chance");
    private static final int EFFECTS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Effects.Chance");
    private static final int MOUNTS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Mounts.Chance");
    private static final int HATS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Hats.Chance");
    private static final int HELMET_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Suits.Chance") / 4;
    private static final int CHESTPLATE_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Suits.Chance") / 4;
    private static final int LEGGINGS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Suits.Chance") / 4;
    private static final int BOOTS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Suits.Chance") / 4;
    private static final int EMOTES_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Emotes.Chance");
    private static final List<ResultType> RESULT_TYPES = new ArrayList<>();
    public static ArrayList<Firework> fireworksList = new ArrayList<>();
    private static List<GadgetType> gadgetList = new ArrayList<>();
    private static List<GadgetType> ammoList = new ArrayList<>();
    private static List<ParticleEffectType> particleEffectList = new ArrayList<>();
    private static List<MountType> mountList = new ArrayList<>();
    private static List<PetType> petList = new ArrayList<>();
    private static List<MorphType> morphList = new ArrayList<>();
    private static List<HatType> hatList = new ArrayList<>();
    private static List<SuitType> helmetList = new ArrayList<>();
    private static List<SuitType> chestplateList = new ArrayList<>();
    private static List<SuitType> leggingList = new ArrayList<>();
    private static List<SuitType> bootList = new ArrayList<>();
    private static List<EmoteType> emoteList = new ArrayList<>();
    private static List<CommandReward> commandRewardList = new ArrayList<>();
    private static Random random = new Random();
    public Location loc;
    List<ResultType> types = new ArrayList();
    private Player player;
    private ItemStack itemStack;
    private String name;

    public TreasureRandomizer(final Player player, Location location) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        // add ammo.
        if (UltraCosmeticsData.get().isAmmoEnabled() && ammoList.isEmpty())
            for (GadgetType type : GadgetType.values())
                if (type.isEnabled()
                        && player.hasPermission(type.getPermission())
                        && type.requiresAmmo()
                        && type.canBeFound())
                    ammoList.add(type);
        // Add GADGETS! (Not ammo)
        if (gadgetList.isEmpty())
            for (GadgetType type : GadgetType.values())
                if (type.isEnabled()
                        && !player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    gadgetList.add(type);
        if (petList.isEmpty())
            for (PetType petType : PetType.enabled())
                if (!player.hasPermission(petType.getPermission())
                        && petType.canBeFound())
                    petList.add(petType);
        if (morphList.isEmpty()
                && Category.MORPHS.isEnabled())
            for (MorphType morph : MorphType.enabled())
                if (!player.hasPermission(morph.getPermission())
                        && morph.canBeFound())
                    morphList.add(morph);
        if (particleEffectList.isEmpty())
            for (ParticleEffectType type : ParticleEffectType.enabled())
                if (!player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    particleEffectList.add(type);
        if (mountList.isEmpty())
            for (MountType type : MountType.enabled())
                if (!player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    mountList.add(type);
        if (hatList.isEmpty())
            for (HatType hat : HatType.enabled()) {
                if (hat.canBeFound()
                        && !player.hasPermission(hat.getPermission())) {
                    hatList.add(hat);
                }
            }
        if (helmetList.isEmpty())
            for (CosmeticType cosmeticType : SuitType.enabled()) {
                SuitType suit = (SuitType) cosmeticType;
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.HELMET)))
                    helmetList.add(suit);
            }
        if (chestplateList.isEmpty())
            for (CosmeticType cosmeticType : SuitType.enabled()) {
                SuitType suit = (SuitType) cosmeticType;
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.CHESTPLATE)))
                    chestplateList.add(suit);
            }
        if (leggingList.isEmpty())
            for (CosmeticType cosmeticType : SuitType.enabled()) {
                SuitType suit = (SuitType) cosmeticType;
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.LEGGINGS)))
                    leggingList.add(suit);
            }
        if (bootList.isEmpty())
            for (CosmeticType cosmeticType : SuitType.enabled()) {
                SuitType suit = (SuitType) cosmeticType;
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.BOOTS)))
                    bootList.add(suit);
            }
        if (emoteList.isEmpty())
            for (EmoteType emoteType : EmoteType.enabled())
                if (emoteType.canBeFound()
                        && !player.hasPermission(emoteType.getPermission()))
                    emoteList.add(emoteType);
        if (commandRewardList.isEmpty()) {
            CustomConfiguration config = UltraCosmeticsData.get().getPlugin().getConfig();

            for (String key : config.getConfigurationSection("TreasureChests.Loots.Commands").getKeys(false)) {
                String path = "TreasureChests.Loots.Commands." + key;
                if (config.getBoolean(path + ".Enabled")) {
                    String cancelPermission = config.getString(path + ".Cancel-If-Permission");
                    if (cancelPermission.equals("no") || !player.hasPermission(cancelPermission)) {
                        commandRewardList.add(new CommandReward(path));
                    }
                }
            }
        }

        if (!Category.MOUNTS.isEnabled())
            mountList.clear();
        if (!Category.GADGETS.isEnabled()) {
            ammoList.clear();
            gadgetList.clear();
        }
        if (!Category.EFFECTS.isEnabled())
            particleEffectList.clear();
        if (!Category.PETS.isEnabled())
            petList.clear();
        if (!Category.MORPHS.isEnabled())
            morphList.clear();
        if (!Category.HATS.isEnabled())
            hatList.clear();
        if (!Category.SUITS.isEnabled()) {
            helmetList.clear();
            chestplateList.clear();
            leggingList.clear();
            bootList.clear();
        }
        if (!Category.EMOTES.isEnabled())
            emoteList.clear();

        if (Category.MORPHS.isEnabled()
                && !morphList.isEmpty()
                && Category.MORPHS.isEnabled()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Morphs.Enabled"))
            setupChance(RESULT_TYPES, MORPHS_CHANCE, ResultType.MORPH);
        if (Category.EFFECTS.isEnabled()
                && !particleEffectList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Effects.Enabled"))
            setupChance(RESULT_TYPES, EFFECTS_CHANCE, ResultType.EFFECT);
        if (Category.GADGETS.isEnabled()) {
            if (!ammoList.isEmpty()
                    && UltraCosmeticsData.get().isAmmoEnabled()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Enabled"))
                setupChance(RESULT_TYPES, AMMO_CHANCE, ResultType.AMMO);
            if (!gadgetList.isEmpty()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets.Enabled"))
                setupChance(RESULT_TYPES, GADGET_CHANCE, ResultType.GADGET);
        }
        if (Category.PETS.isEnabled()
                && !petList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Pets.Enabled"))
            setupChance(RESULT_TYPES, PETS_CHANCE, ResultType.PET);
        if (Category.MOUNTS.isEnabled()
                && !mountList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Mounts.Enabled"))
            setupChance(RESULT_TYPES, MOUNTS_CHANCE, ResultType.MOUNT);
        if (Category.HATS.isEnabled()
                && !hatList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Hats.Enabled"))
            setupChance(RESULT_TYPES, HATS_CHANCE, ResultType.HAT);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled")
                && UltraCosmeticsData.get().useMoneyTreasureLoot())
            setupChance(RESULT_TYPES, MONEY_CHANCE, ResultType.MONEY);
        if (Category.SUITS.isEnabled()) {
            if (!helmetList.isEmpty()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Suits.Enabled"))
                setupChance(RESULT_TYPES, HELMET_CHANCE, ResultType.HELMET);
            if (!chestplateList.isEmpty()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Suits.Enabled"))
                setupChance(RESULT_TYPES, CHESTPLATE_CHANCE, ResultType.CHESTPLATE);
            if (!leggingList.isEmpty()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Suits.Enabled"))
                setupChance(RESULT_TYPES, LEGGINGS_CHANCE, ResultType.LEGGINGS);
            if (!bootList.isEmpty()
                    && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Suits.Enabled"))
                setupChance(RESULT_TYPES, BOOTS_CHANCE, ResultType.BOOTS);
        }
        if (Category.EMOTES.isEnabled()
                && !emoteList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Emotes.Enabled"))
            setupChance(RESULT_TYPES, EMOTES_CHANCE, ResultType.EMOTE);

        for (CommandReward commandReward : commandRewardList) {
            for (int i = 0; i < commandReward.getChance(); i++) {
                RESULT_TYPES.add(ResultType.COMMAND);
            }
        }
    }

    private static void setupChance(List<ResultType> resultRef, int percent, ResultType resultType) {
        for (int i = 0; i < percent; i++) {
            resultRef.add(resultType);
        }
    }

    public static FireworkEffect getRandomFireworkEffect() {
        if (!UltraCosmeticsData.get().getPlugin().isEnabled())
            return null;
        Random r = new Random();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).withFade(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).build();
    }

    private String getMessage(String s) {
        try {
            return ChatColor.translateAlternateColorCodes('&', ((String) SettingsManager.getConfig().get(s)).replace("%prefix%", MessageManager.getMessage("Prefix")));
        } catch (Exception exc) {
            return "§c§lError";
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void giveRandomThing() {
        try {
            if (types.isEmpty()) {
                types = new ArrayList(RESULT_TYPES);
                Collections.shuffle(types);
            }

            ResultType type = types.get(0);

            types = new ArrayList();

            switch (type) {
                case MONEY:
                    giveMoney();
                    break;
                case AMMO:
                    if (!UltraCosmeticsData.get().isAmmoEnabled()) {
                        giveRandomThing();
                        break;
                    }
                    giveAmmo();
                    break;
                case MOUNT:
                    giveRandomMount();
                    break;
                case MORPH:
                    giveRandomMorph();
                    break;
                case PET:
                    giveRandomPet();
                    break;
                case EFFECT:
                    giveRandomEffect();
                    break;
                case HAT:
                    giveRandomHat();
                    break;
                case GADGET:
                    giveRandomGadget();
                    break;
                case HELMET:
                    giveRandomSuit(ArmorSlot.HELMET);
                    break;
                case CHESTPLATE:
                    giveRandomSuit(ArmorSlot.CHESTPLATE);
                    break;
                case LEGGINGS:
                    giveRandomSuit(ArmorSlot.LEGGINGS);
                    break;
                case BOOTS:
                    giveRandomSuit(ArmorSlot.BOOTS);
                    break;
                case EMOTE:
                    giveRandomEmote();
                    break;
                case COMMAND:
                    giveRandomCommandReward();
                    break;
            }

        } catch (IndexOutOfBoundsException | IllegalArgumentException exception) {
            if ((!d("Gadgets") || gadgetList.isEmpty())
                    && (!d("Gadgets-Ammo") || ammoList.isEmpty())
                    && (!d("Pets") || petList.isEmpty())
                    && (!d("Morphs") || morphList.isEmpty())
                    && (!d("Mounts") || mountList.isEmpty())
                    && (!d("Hats") || hatList.isEmpty())
                    && (!d("Effects") || particleEffectList.isEmpty())
                    || RESULT_TYPES.isEmpty())
                giveNothing();
            else
                giveRandomThing();
        }
        SoundUtil.playSound(loc, Sounds.CHEST_OPEN, 1.4f, 1.5f);
    }

    private boolean d(String s) {
        return (boolean) SettingsManager.getConfig().get("TreasureChests.Loots." + s + ".Enabled");
    }

    public String getName() {
        return name;
    }

    public void clear() {
        petList.clear();
        ammoList.clear();
        gadgetList.clear();
        particleEffectList.clear();
        mountList.clear();
        morphList.clear();
        hatList.clear();
        helmetList.clear();
        chestplateList.clear();
        leggingList.clear();
        bootList.clear();
        emoteList.clear();
        commandRewardList.clear();
        RESULT_TYPES.clear();
        types.clear();
    }

    public void giveNothing() {
        if (UltraCosmeticsData.get().getPlugin().getEconomyHandler().isUsingEconomy()) {
            try {
                giveMoney();
            } catch (Exception e) {
                name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
                itemStack = new ItemStack(Material.BARRIER);
            }
        } else {
            name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
            itemStack = new ItemStack(Material.BARRIER);
        }
    }

    public void giveMoney() {
        if (!UltraCosmeticsData.get().getPlugin().getEconomyHandler().isUsingEconomy()) {
            giveNothing();
            return;
        }
        int max = (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max");
        int min = SettingsManager.getConfig().contains("TreasureChests.Loots.Money.Min") ? SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Max") : (max > 20 ? 20 : 0);
        int money = MathUtils.randomRangeInt(min, max);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        UltraCosmeticsData.get().getPlugin().getEconomyHandler().deposit(player, money);
        itemStack = new ItemStack(BlockUtils.getOldMaterial("DOUBLE_PLANT"));
        if (money > 3 * (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max") / 4)
            spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Money.Message.message")).replace("%name%", player.getName()).replace("%money%", money + ""));
    }

    public void giveAmmo() {
        int i = random.nextInt(ammoList.size());
        GadgetType g = ammoList.get(i);
        int ammo = MathUtils.randomRangeInt((int) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Min"), (int) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Ammo").replace("%name%", g.getName()).replace("%ammo%", ammo + "");
        ammoList.remove(i);
        UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).addAmmo(g.toString().toLowerCase(), ammo);
        itemStack = g.getMaterial().parseItem();
        if (ammo > 50) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets-Ammo.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Gadgets-Ammo.Message.message")).replace("%name%", player.getName()).replace("%ammo%", ammo + "").replace("%gadget%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? g.getName() : TextUtil.filterColor(g.getName())));

    }

    public void giveRandomSuit(ArmorSlot armorSlot) {
        List<SuitType> list = null;
        switch (armorSlot) {
            case HELMET:
                list = helmetList;
                break;
            case CHESTPLATE:
                list = chestplateList;
                break;
            case LEGGINGS:
                list = leggingList;
                break;
            case BOOTS:
                list = bootList;
                break;
        }
        int i = random.nextInt(list.size());
        SuitType suitType = list.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Suit").replace("%suit%", suitType.getName(armorSlot));
        list.remove(i);
        givePermission(suitType.getPermission(armorSlot));
        itemStack = suitType.getMaterial(armorSlot).parseItem();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Suits.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Suits.Message.message")).replace("%name%", player.getName())
                    .replace("%suit%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? suitType.getName(armorSlot) : TextUtil.filterColor(suitType.getName(armorSlot))));
    }

    public void giveRandomGadget() {
        int i = random.nextInt(gadgetList.size());
        GadgetType gadget = gadgetList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.gadget").replace("%gadget%", gadget.getName());
        gadgetList.remove(i);
        givePermission(gadget.getPermission());
        itemStack = gadget.getMaterial().parseItem();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Gadgets.Message.message")).replace("%name%", player.getName())
                    .replace("%gadget%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? gadget.getName() : TextUtil.filterColor(gadget.getName())));
    }

    public void giveRandomHat() {
        int i = random.nextInt(hatList.size());
        HatType hat = hatList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Hat").replace("%hat%", hat.getName());
        hatList.remove(i);
        givePermission(hat.getPermission());
        itemStack = hat.getItemStack().clone();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Hats.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Hats.Message.message")).replace("%name%", player.getName()).replace("%hat%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? hat.getName() : TextUtil.filterColor(hat.getName())));
    }

    public void giveRandomPet() {
        int i = random.nextInt(petList.size());
        PetType pet = petList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Pet").replace("%pet%", pet.getName());
        petList.remove(i);
        givePermission(pet.getPermission());
        itemStack = pet.getMaterial().parseItem();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Pets.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Pets.Message.message")).replace("%name%", player.getName())
                    .replace("%pet%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? pet.getName() : TextUtil.filterColor(pet.getName())));
    }

    public void giveRandomEmote() {
        int i = random.nextInt(emoteList.size());
        EmoteType emoteType = emoteList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Emote").replace("%emote%", emoteType.getName());
        emoteList.remove(i);
        givePermission(emoteType.getPermission());
        itemStack = new ItemStack(emoteType.getFrames().get(emoteType.getMaxFrames() - 1));
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Emotes.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Emotes.Message.message")).replace("%name%", player.getName())
                    .replace("%emote%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? emoteType.getName() : TextUtil.filterColor(emoteType.getName())));
    }

    public void giveRandomMount() {
        int i = random.nextInt(mountList.size());
        MountType mount = mountList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Mount").replace("%mount%", mount.getMenuName());
        mountList.remove(i);
        itemStack = mount.getMaterial().parseItem();
        givePermission(mount.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Mounts.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Mounts.Message.message"))
                    .replace("%name%", player.getName()).replace("%mount%", (UltraCosmeticsData.get().arePlaceholdersColored())
                            ? mount.getMenuName() : TextUtil.filterColor(mount.getMenuName())));
    }

    public void giveRandomEffect() {
        int i = random.nextInt(particleEffectList.size());
        ParticleEffectType particleEffect = particleEffectList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Effect").replace("%effect%", particleEffect.getName());
        particleEffectList.remove(i);
        itemStack = particleEffect.getMaterial().parseItem();
        givePermission(particleEffect.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Effects.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Effects.Message.message")).replace("%name%", player.getName()).replace("%effect%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? particleEffect.getName() : TextUtil.filterColor(particleEffect.getName())));
    }

    public void giveRandomMorph() {
        int i = random.nextInt(morphList.size());
        MorphType morph = morphList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Morph").replace("%morph%", morph.getName());
        morphList.remove(morph);
        itemStack = morph.getMaterial().parseItem();
        givePermission(morph.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Morphs.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Morphs.Message.message"))
                    .replace("%name%", player.getName()).replace("%morph%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? morph.getName() : TextUtil.filterColor(morph.getName())));
    }

    public void giveRandomCommandReward() {
        ArrayList<CommandReward> rewards = new ArrayList<>();
        for (CommandReward commandReward : commandRewardList) {
            for (int i = 0; i < commandReward.getChance(); i++) {
                rewards.add(commandReward);
            }
        }
        CommandReward reward = rewards.get(random.nextInt(rewards.size()));
        rewards.clear();
        for (String command : reward.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', command.replace("%name%", player.getName())));
        }
        name = reward.getName().replace("%name%", player.getName());
        itemStack = new ItemStack(reward.getMaterial());
        spawnRandomFirework(loc);

        if (reward.getMessageEnabled())
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', reward.getMessage().replace("%name%", player.getName()).replace("%prefix%", MessageManager.getMessage("Prefix"))));
    }

    public void givePermission(String permission) {
        String command = (getMessage("TreasureChests.Permission-Add-Command")).replace("%name%", player.getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void spawnRandomFirework(Location location) {
        if (!UltraCosmeticsData.get().getPlugin().isEnabled())
            return;
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = player.getWorld().spawn(location.clone().add(0.5, 0, 0.5), Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
            fireworksList.add(f);
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            for (Firework f : fireworks)
                f.detonate();
        }, 2);
    }

    private enum ResultType {
        AMMO,
        GADGET,
        MONEY,
        MORPH,
        MOUNT,
        EFFECT,
        PET,
        HAT,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        EMOTE,
        COMMAND
    }


}
