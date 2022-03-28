package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.util.*;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureRandomizer {
    private static final int MONEY_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Chance");
    private static final int AMMO_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets-Ammo.Chance");
    private static final Random random = new Random();
    private final WeightedSet<ResultType> resultTypes = new WeightedSet<>();
    private final List<GadgetType> ammoList = new ArrayList<>();
    private final List<CommandReward> commandRewardList = new ArrayList<>();
    private Location loc;
    private final Player player;
    private ItemStack itemStack;
    private String name;

    public TreasureRandomizer(final Player player, Location location) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        // add ammo.
        if (Category.GADGETS.isEnabled() && UltraCosmeticsData.get().isAmmoEnabled() && ammoList.isEmpty())
            for (GadgetType type : GadgetType.values())
                if (type.isEnabled()
                        && type.requiresAmmo()
                        && type.canBeFound())
                    ammoList.add(type);
        // Add GADGETS! (Not ammo)
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

        for (Category cat : Category.values()) {
            setupChance(cat);
        }

        if (Category.GADGETS.isEnabled()) {
            if (!ammoList.isEmpty()
                    && UltraCosmeticsData.get().isAmmoEnabled()
                    && SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets-Ammo.Enabled")) {
                addWeightedResult(AMMO_CHANCE, ResultType.AMMO);
            }
        }
        
        if (UltraCosmeticsData.get().useMoneyTreasureLoot()) {
            addWeightedResult(MONEY_CHANCE, ResultType.MONEY);
        }

        for (CommandReward commandReward : commandRewardList) {
            addWeightedResult(commandReward.getChance(), ResultType.COMMAND);
        }
    }

    private void setupChance(Category category) {
        // word case, like "Pets" rather than "PETS"
        String configName = category.name().substring(0, 1) + category.name().substring(1).toLowerCase();
        String configPath = "TreasureChests.Loots." + configName;
        if (!SettingsManager.getConfig().getBoolean(configPath + ".Enabled")) return;
        if (!category.isEnabled()) return;
        addWeightedResult(SettingsManager.getConfig().getInt(configPath + ".Chance"), ResultType.fromCategory(category));
    }

    private void addWeightedResult(int weight, ResultType type) {
        resultTypes.add(type, weight);
    }

    private boolean hasUnlockableInCategory(Category category) {
        for (CosmeticType<?> type : category.getEnabled()) {
            if (!player.hasPermission(type.getPermission())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasUnlockedInCategory(Category category) {
        for (CosmeticType<?> type : category.getEnabled()) {
            if (player.hasPermission(type.getPermission())) {
                return true;
            }
        }
        return false;
    }

    private FireworkEffect getRandomFireworkEffect() {
        if (!UltraCosmeticsData.get().getPlugin().isEnabled())
            return null;
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(randomColor()).withFade(randomColor()).build();
    }

    private static Color randomColor() {
        Random r = ThreadLocalRandom.current();
        return Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    private String getConfigMessage(String s) {
        String message = SettingsManager.getConfig().getString(s);
        if (message == null) {
            return ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Error";
        }
        return ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", MessageManager.getMessage("Prefix")));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void giveRandomThing() {
        SoundUtil.playSound(loc, Sounds.CHEST_OPEN, 1.4f, 1.5f);
        WeightedSet<ResultType> filtered = new WeightedSet<>(resultTypes);
        // remove the key if the player has no unlockables
        // TODO: although robust, this can fire off a large number of permission checks. Optimize?
        filtered.filter(r -> r.category != null && !hasUnlockableInCategory(r.category));
        if (!hasUnlockedInCategory(Category.GADGETS)) {
            filtered.remove(ResultType.AMMO);
        }
        if (filtered.size() == 0) {
            giveFallback();
            return;
        }
        ResultType type = filtered.getRandom();

        switch (type) {
            case MONEY:
                giveMoney();
                break;
            case AMMO:
                giveAmmo();
                break;
            case MOUNT:
                giveRandomCosmetic(MountType.enabled(), "Mount", "Mounts");
                break;
            case MORPH:
                giveRandomCosmetic(MorphType.enabled(), "Morph", "Morphs");
                break;
            case PET:
                giveRandomCosmetic(PetType.enabled(), "Pet", "Pets");
                break;
            case EFFECT:
                giveRandomCosmetic(ParticleEffectType.enabled(), "Effect", "Effects");
                break;
            case HAT:
                giveRandomCosmetic(HatType.enabled(), "Hat", "Hats");
                break;
            case GADGET:
                giveRandomCosmetic(GadgetType.enabled(), "Gadget", "Gadgets");
                break;
            case SUIT:
                giveRandomCosmetic(SuitType.enabled(), "Suit", "Suits");
                break;
            case EMOTE:
                giveRandomCosmetic(EmoteType.enabled(), "Emote", "Emotes");
                break;
            case COMMAND:
                giveRandomCommandReward();
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void clear() {
        ammoList.clear();
        commandRewardList.clear();
        resultTypes.clear();
    }

    public void giveFallback() {
        if (UltraCosmeticsData.get().getPlugin().getEconomyHandler().isUsingEconomy()) {
            giveMoney();
        } else {
            giveNothing();
        }
    }

    public void giveNothing() {
        name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
        itemStack = new ItemStack(Material.BARRIER);
    }

    public void giveMoney() {
        int max = (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max");
        int min = SettingsManager.getConfig().contains("TreasureChests.Loots.Money.Min") ? SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Min") : (max > 20 ? 20 : 0);
        int money = MathUtils.randomRangeInt(min, max);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        UltraCosmeticsData.get().getPlugin().getEconomyHandler().deposit(player, money);
        itemStack = XMaterial.SUNFLOWER.parseItem();
        if (money > 3 * SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Max") / 4) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Message.enabled")) {
            broadcast((getConfigMessage("TreasureChests.Loots.Money.Message.message")).replace("%money%", money + ""));
        }
    }

    public void giveAmmo() {
        List<GadgetType> ammoOpts = new ArrayList<GadgetType>(ammoList);
        ammoOpts.removeIf(k -> !player.hasPermission(k.getPermission()));
        GadgetType g = ammoOpts.get(random.nextInt(ammoOpts.size()));
        int ammoMin = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets-Ammo.Min");
        int ammoMax = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets-Ammo.Max");
        int ammo;
        if (ammoMin < ammoMax) {
            ammo = random.nextInt(ammoMax - ammoMin) + ammoMin;
        } else {
            ammo = ammoMin;
        }
        name = MessageManager.getMessage("Treasure-Chests-Loot.Ammo").replace("%name%", g.getName()).replace("%ammo%", String.valueOf(ammo));

        UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).addAmmo(g, ammo);
        itemStack = g.getMaterial().parseItem();
        // if the player received more than half of what they could have, send a firework
        if (ammo > (ammoMax - ammoMin) / 2 + ammoMin) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets-Ammo.Message.enabled")) {
            broadcast((getConfigMessage("TreasureChests.Loots.Gadgets-Ammo.Message.message")).replace("%ammo%", String.valueOf(ammo)).replace("%gadget%", (UltraCosmeticsData.get().arePlaceholdersColored()) ? g.getName() : TextUtil.filterColor(g.getName())));
        }
    }

    public void giveRandomCosmetic(List<? extends CosmeticType<?>> cosmetics, String lang, String configName) {
        // the string manipulation in this function is bad
        // and should be replaced with something that makes sense
        WeightedSet<CosmeticType<?>> weightedCosmetics = new WeightedSet<>();
        for (CosmeticType<?> cosmetic : cosmetics) {
            if (player.hasPermission(cosmetic.getPermission())) continue;
            weightedCosmetics.add(cosmetic, cosmetic.getChestWeight());
        }
        CosmeticType<?> cosmetic = weightedCosmetics.getRandom();
        name = MessageManager.getMessage("Treasure-Chests-Loot." + lang).replace("%" + lang.toLowerCase() + "%", cosmetic.getName());
        givePermission(cosmetic.getPermission());
        spawnRandomFirework(loc);
        itemStack = cosmetic.getItemStack();
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots." + configName + ".Message.enabled")) {
            String message = (getConfigMessage("TreasureChests.Loots." + configName + ".Message.message"))
                    .replace("%" + lang.toLowerCase() + "%", UltraCosmeticsData.get().arePlaceholdersColored() ? cosmetic.getName() : TextUtil.filterColor(cosmetic.getName()));
            broadcast(message);
        }
    }

    public void giveRandomCommandReward() {
        WeightedSet<CommandReward> rewards = new WeightedSet<>();
        for (CommandReward commandReward : commandRewardList) {
            rewards.add(commandReward, commandReward.getChance());
        }
        CommandReward reward = rewards.getRandom();
        rewards.clear();
        for (String command : reward.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%name%", player.getName()));
        }
        name = reward.getName().replace("%name%", player.getName());
        itemStack = reward.getItemStack();
        spawnRandomFirework(loc);

        if (reward.getMessageEnabled()) {
            broadcast(ChatColor.translateAlternateColorCodes('&', reward.getMessage().replace("%prefix%", MessageManager.getMessage("Prefix"))));
        }
    }

    public void givePermission(String permission) {
        String command = SettingsManager.getConfig().getString("TreasureChests.Permission-Add-Command").replace("%name%", player.getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void spawnRandomFirework(Location location) {
        if (!UltraCosmeticsData.get().getPlugin().isEnabled()) return;
        final List<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = player.getWorld().spawn(location.clone().add(0.5, 0, 0.5), Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
            f.setMetadata("UCFirework", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), 1));
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            for (Firework f : fireworks)
                f.detonate();
        }, 2);
    }

    private void broadcast(String message) {
        message = message.replace("%name%", player.getName());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player || (SettingsManager.isAllowedWorld(player.getWorld())
                    && UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).isTreasureNotifying())) {
                player.sendMessage(message);
            }
        }
    }

    public void setLocation(Location newLoc) {
        loc = newLoc;
    }

    private enum ResultType {
        AMMO,
        GADGET(Category.GADGETS),
        MONEY,
        MORPH(Category.MORPHS),
        MOUNT(Category.MOUNTS),
        EFFECT(Category.EFFECTS),
        PET(Category.PETS),
        HAT(Category.HATS),
        SUIT(Category.SUITS),
        EMOTE(Category.EMOTES),
        COMMAND,
        ;
        private final Category category;
        private ResultType() {
            this(null);
        }

        private ResultType(Category category) {
            this.category = category;
        }

        private static ResultType fromCategory(Category cat) {
            for (ResultType type : values()) {
                if (type.category == cat) {
                    return type;
                }
            }
            return null;
        }
    }
}
