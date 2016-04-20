package be.isach.ultracosmetics.cosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectType;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.SuitType;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureRandomizer {

    Player player;
    public Location loc;
    private ItemStack itemStack;
    private String name;

    public static List<GadgetType> gadgetList = new ArrayList<>();
    public static List<GadgetType> ammoList = new ArrayList<>();
    public static List<ParticleEffectType> particleEffectList = new ArrayList<>();
    public static List<MountType> mountList = new ArrayList<>();
    public static List<PetType> petList = new ArrayList<>();
    public static List<MorphType> morphList = new ArrayList<>();
    public static List<Hat> hatList = new ArrayList<>();
    public static List<SuitType> helmetList = new ArrayList<>();
    public static List<SuitType> chestplateList = new ArrayList<>();
    public static List<SuitType> leggingList = new ArrayList<>();
    public static List<SuitType> bootList = new ArrayList<>();

    private static Random random = new Random();

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
        BOOTS
    }

    private static final List<ResultType> RESULT_TYPES = new ArrayList<>();

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

    private static void setupChance(List<ResultType> resultRef, int percent, ResultType resultType) {
        for (int i = 0; i < percent; i++) {
            resultRef.add(resultType);
        }
    }

    public TreasureRandomizer(final Player player, Location location) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        // add ammo.
        if (UltraCosmetics.getInstance().isAmmoEnabled() && ammoList.isEmpty())
            for (GadgetType type : GadgetType.values())
                if (type.isEnabled()
                        && player.hasPermission(type.getPermission())
                        && type.requiresAmmo()
                        && type.canBeFound())
                    this.ammoList.add(type);
        // Add GADGETS! (Not ammo)
        if (gadgetList.isEmpty())
            for (GadgetType type : GadgetType.values())
                if (type.isEnabled()
                        && !player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    this.gadgetList.add(type);
        if (petList.isEmpty())
            for (PetType petType : PetType.enabled())
                if (!player.hasPermission(petType.getPermission())
                        && petType.canBeFound())
                    this.petList.add(petType);
        if (morphList.isEmpty()
                && UltraCosmetics.enabledCategories.contains(Category.MORPHS))
            for (MorphType morph : MorphType.enabled())
                if (!player.hasPermission(morph.getPermission())
                        && morph.canBeFound())
                    this.morphList.add(morph);
        if (particleEffectList.isEmpty())
            for (ParticleEffectType type : ParticleEffectType.enabled())
                if (!player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    this.particleEffectList.add(type);
        if (mountList.isEmpty())
            for (MountType type : MountType.enabled())
                if (!player.hasPermission(type.getPermission())
                        && type.canBeFound())
                    this.mountList.add(type);
        if (hatList.isEmpty())
            for (Hat hat : Hat.enabled())
                if (hat.canBeFound()
                        && !player.hasPermission(hat.getPermission()))
                    this.hatList.add(hat);
        if (helmetList.isEmpty())
            for (SuitType suit : SuitType.enabled())
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.HELMET)))
                    this.helmetList.add(suit);
        if (chestplateList.isEmpty())
            for (SuitType suit : SuitType.enabled())
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.CHESTPLATE)))
                    this.chestplateList.add(suit);
        if (leggingList.isEmpty())
            for (SuitType suit : SuitType.enabled())
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.LEGGINGS)))
                    this.leggingList.add(suit);
        if (bootList.isEmpty())
            for (SuitType suit : SuitType.enabled())
                if (suit.canBeFound()
                        && !player.hasPermission(suit.getPermission(ArmorSlot.BOOTS)))
                    this.bootList.add(suit);

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

        if (Category.MORPHS.isEnabled()
                && !morphList.isEmpty()
                && UltraCosmetics.enabledCategories.contains(Category.MORPHS)
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Morphs.Enabled"))
            setupChance(RESULT_TYPES, MORPHS_CHANCE, ResultType.MORPH);
        if (Category.EFFECTS.isEnabled()
                && !particleEffectList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Effects.Enabled"))
            setupChance(RESULT_TYPES, EFFECTS_CHANCE, ResultType.EFFECT);
        if (Category.GADGETS.isEnabled()) {
            if (!ammoList.isEmpty()
                    && UltraCosmetics.getInstance().isAmmoEnabled()
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
                && UltraCosmetics.moneyTreasureLoot)
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

    List<ResultType> types = new ArrayList();

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
                    if (!UltraCosmetics.getInstance().isAmmoEnabled()) {
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
            }

        } catch (IndexOutOfBoundsException exception) {
            if ((d("Gadgets") ? gadgetList.isEmpty() : true)
                    && (d("Gadgets-Ammo") ? ammoList.isEmpty() : true)
                    && (d("Pets") ? petList.isEmpty() : true)
                    && (d("Morphs") ? morphList.isEmpty() : true)
                    && (d("Mounts") ? mountList.isEmpty() : true)
                    && (d("Hats") ? hatList.isEmpty() : true)
                    && (d("Effects") ? particleEffectList.isEmpty() : true)
                    || RESULT_TYPES.isEmpty())
                giveNothing();
            else
                giveRandomThing();
        } catch (IllegalArgumentException exception) {
            if ((d("Gadgets") ? gadgetList.isEmpty() : true)
                    && (d("Gadgets-Ammo") ? ammoList.isEmpty() : true)
                    && (d("Pets") ? petList.isEmpty() : true)
                    && (d("Morphs") ? morphList.isEmpty() : true)
                    && (d("Mounts") ? mountList.isEmpty() : true)
                    && (d("Hats") ? hatList.isEmpty() : true)
                    && (d("Effects") ? particleEffectList.isEmpty() : true)
                    || RESULT_TYPES.isEmpty())
                giveNothing();
            else
                giveRandomThing();
        }
        switch (UltraCosmetics.getServerVersion()) {
            case v1_8_R3:
                loc.getWorld().playSound(loc, Sound.valueOf("CHEST_OPEN"), 1.4f, 1.5f);
                break;
            case v1_9_R1:
                loc.getWorld().playSound(loc, Sound.BLOCK_CHEST_OPEN, 1.4f, 1.5f);
                break;
        }
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
        RESULT_TYPES.clear();
        types.clear();
    }

    public void giveNothing() {
        try{
        	giveMoney();
        }catch(Exception e){
        	 name = MessageManager.getMessage("Treasure-Chests-Loot.Nothing");
             itemStack = new ItemStack(Material.BARRIER);
        }
    }

    public void giveMoney() {
        int money = MathUtils.randomRangeInt(20, (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        UltraCosmetics.economy.depositPlayer(player, money);
        itemStack = new ItemStack(Material.DOUBLE_PLANT);
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
        UltraCosmetics.getCustomPlayer(player).addAmmo(g.toString().toLowerCase(), ammo);
        itemStack = new MaterialData(g.getMaterial(), g.getData()).toItemStack(1);
        if (ammo > 50) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets-Ammo.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Gadgets-Ammo.Message.message")).replace("%name%", player.getName()).replace("%ammo%", ammo + "").replace("%gadget%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? g.getName() : UltraCosmetics.filterColor(g.getName())));

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
        itemStack = new ItemStack(suitType.getMaterial(armorSlot));
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Suits.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Suits.Message.message")).replace("%name%", player.getName())
                    .replace("%suit%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? suitType.getName(armorSlot) : UltraCosmetics.filterColor(suitType.getName(armorSlot))));
    }

    public void giveRandomGadget() {
        int i = random.nextInt(gadgetList.size());
        GadgetType gadget = gadgetList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.gadget").replace("%gadget%", gadget.getName());
        gadgetList.remove(i);
        givePermission(gadget.getPermission());
        itemStack = new ItemStack(gadget.getMaterial());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Gadgets.Message.message")).replace("%name%", player.getName())
                    .replace("%gadget%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? gadget.getName() : UltraCosmetics.filterColor(gadget.getName())));
    }

    public void giveRandomHat() {
        int i = random.nextInt(hatList.size());
        Hat hat = hatList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Hat").replace("%hat%", hat.getName());
        hatList.remove(i);
        givePermission(hat.getPermission());
        itemStack = hat.getItemStack().clone();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Hats.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Hats.Message.message")).replace("%name%", player.getName()).replace("%hat%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? hat.getName() : UltraCosmetics.filterColor(hat.getName())));
    }

    public void giveRandomPet() {
        int i = random.nextInt(petList.size());
        PetType pet = petList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Pet").replace("%pet%", pet.getMenuName());
        petList.remove(i);
        givePermission(pet.getPermission());
        itemStack = new ItemStack(pet.getMaterial());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Pets.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Pets.Message.message")).replace("%name%", player.getName())
                    .replace("%pet%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? pet.getMenuName() : UltraCosmetics.filterColor(pet.getMenuName())));
    }

    public void giveRandomMount() {
        int i = random.nextInt(mountList.size());
        MountType mount = mountList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Mount").replace("%mount%", mount.getMenuName());
        mountList.remove(i);
        itemStack = new ItemStack(mount.getMaterial());
        givePermission(mount.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Mounts.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Mounts.Message.message"))
                    .replace("%name%", player.getName()).replace("%mount%", (UltraCosmetics.getInstance().placeholdersHaveColor())
                            ? mount.getMenuName() : UltraCosmetics.filterColor(mount.getMenuName())));
    }

    public void giveRandomEffect() {
        int i = random.nextInt(particleEffectList.size());
        ParticleEffectType particleEffect = particleEffectList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Effect").replace("%effect%", particleEffect.getName());
        particleEffectList.remove(i);
        itemStack = new ItemStack(particleEffect.getMaterial());
        givePermission(particleEffect.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Effects.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Effects.Message.message")).replace("%name%", player.getName()).replace("%effect%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? particleEffect.getName() : UltraCosmetics.filterColor(particleEffect.getName())));
    }

    public void giveRandomMorph() {
        int i = random.nextInt(morphList.size());
        MorphType morph = morphList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Morph").replace("%morph%", morph.getName());
        morphList.remove(morph);
        itemStack = new ItemStack(morph.getMaterial());
        givePermission(morph.getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Morphs.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Morphs.Message.message"))
                    .replace("%name%", player.getName()).replace("%morph%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? morph.getName() : UltraCosmetics.filterColor(morph.getName())));
    }


    public static FireworkEffect getRandomFireworkEffect() {
        if (!UltraCosmetics.getInstance().isEnabled())
            return null;
        Random r = new Random();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).withFade(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).build();
        return effect;
    }

    public void givePermission(String permission) {
        String command = (getMessage("TreasureChests.Permission-Add-Command")).replace("%name%", player.getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void spawnRandomFirework(Location location) {
        if (!UltraCosmetics.getInstance().isEnabled())
            return;
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = player.getWorld().spawn(location.clone().add(0.5, 0, 0.5), Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
        }, 2);
    }

}
