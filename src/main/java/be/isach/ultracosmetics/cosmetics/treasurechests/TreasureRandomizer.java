package be.isach.ultracosmetics.cosmetics.treasurechests;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
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

    public static List<Gadget> gadgetList = new ArrayList<>();
    public static List<ParticleEffect> particleEffectList = new ArrayList<>();
    public static List<Mount> mountList = new ArrayList<>();
    public static List<Pet> petList = new ArrayList<>();
    public static List<Morph> morphList = new ArrayList<>();
    public static List<Hat> hatList = new ArrayList<>();

    private enum ResultType {
        GADGET,
        MONEY,
        MORPH,
        MOUNT,
        EFFECT,
        PET,
        HAT
    }

    private static final List<ResultType> RESULT_REF = new ArrayList<>();

    private static final int MONEY_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Money.Chance");
    private static final int GADGETS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Gadgets-Ammo.Chance");
    private static final int MORPHS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Morphs.Chance");
    private static final int PETS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Pets.Chance");
    private static final int EFFECTS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Effects.Chance");
    private static final int MOUNTS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Mounts.Chance");
    private static final int HATS_CHANCE = SettingsManager.getConfig().getInt("TreasureChests.Loots.Hats.Chance");

    private static void setupChance(List<ResultType> resultRef, int percent, ResultType resultType) {
        for (int i = 0; i < percent; i++) {
            resultRef.add(resultType);
        }
    }

    public TreasureRandomizer(final Player player, Location location) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        for (Gadget g : Core.getGadgets()) {
            if (g.getType().isEnabled()
                    && player.hasPermission(g.getType().getPermission())
                    && g.getType().requiresAmmo()
                    && g.canBeFound())
                this.gadgetList.add(g);
        }
        if (petList.isEmpty())
            for (Pet pet : Core.getPets()) {
                if (pet.getType().isEnabled()
                        && !player.hasPermission(pet.getType().getPermission())
                        && pet.canBeFound())
                    this.petList.add(pet);
            }
        if (morphList.isEmpty())
            for (Morph morph : Core.getMorphs()) {
                if (morph.getType().isEnabled()
                        && !player.hasPermission(morph.getType().getPermission())
                        && morph.canBeFound())
                    this.morphList.add(morph);
            }
        if (particleEffectList.isEmpty())
            for (ParticleEffect particleEffect : Core.getParticleEffects()) {
                if (particleEffect.getType().isEnabled()
                        && particleEffect.canBeFound()
                        && !player.hasPermission(particleEffect.getType().getPermission()))
                    this.particleEffectList.add(particleEffect);
            }
        if (mountList.isEmpty())
            for (Mount m : Core.getMounts()) {
                if (m.getType().isEnabled()
                        && m.canBeFound()
                        && !player.hasPermission(m.getType().getPermission()))
                    this.mountList.add(m);
            }
        if (hatList.isEmpty())
            for (Hat hat : Core.getHats()) {
                if (hat.isEnabled()
                        && hat.canBeFound()
                        && !player.hasPermission(hat.getPermission()))
                    this.hatList.add(hat);
            }
        if (!Category.MOUNTS.isEnabled())
            mountList.clear();
        if (!Category.GADGETS.isEnabled())
            gadgetList.clear();
        if (!Category.EFFECTS.isEnabled())
            particleEffectList.clear();
        if (!Category.PETS.isEnabled())
            petList.clear();
        if (!Category.MORPHS.isEnabled())
            morphList.clear();
        if (!Category.HATS.isEnabled())
            hatList.clear();
        if (Category.MORPHS.isEnabled()
                && !morphList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Morphs.Enabled"))
            setupChance(RESULT_REF, MORPHS_CHANCE, ResultType.MORPH);
        if (Category.EFFECTS.isEnabled()
                && !particleEffectList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Effects.Enabled"))
            setupChance(RESULT_REF, EFFECTS_CHANCE, ResultType.EFFECT);
        if (Category.GADGETS.isEnabled()
                && !gadgetList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Enabled"))
            setupChance(RESULT_REF, GADGETS_CHANCE, ResultType.GADGET);
        if (Category.PETS.isEnabled()
                && !petList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Pets.Enabled"))
            setupChance(RESULT_REF, PETS_CHANCE, ResultType.PET);
        if (Category.MOUNTS.isEnabled()
                && !mountList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Mounts.Enabled"))
            setupChance(RESULT_REF, MOUNTS_CHANCE, ResultType.MOUNT);
        if (Category.HATS.isEnabled()
                && !hatList.isEmpty()
                && (boolean) SettingsManager.getConfig().get("TreasureChests.Loots.Hats.Enabled"))
            setupChance(RESULT_REF, HATS_CHANCE, ResultType.HAT);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Enabled"))
            setupChance(RESULT_REF, MONEY_CHANCE, ResultType.MONEY);
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
                types = new ArrayList(RESULT_REF);
                Collections.shuffle(types);
            }

            ResultType type = types.get(0);

            types = new ArrayList();

            switch (type) {
                case MONEY:
                    giveMoney();
                    break;
                case GADGET:
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
            }

        } catch (IndexOutOfBoundsException exception) {
            giveMoney();
        }
        loc.getWorld().playSound(loc, Sound.CHEST_OPEN, 3, 1);

    }

    public String getName() {
        return name;
    }

    public void clear() {
        petList.clear();
        gadgetList.clear();
        particleEffectList.clear();
        mountList.clear();
        morphList.clear();
        hatList.clear();
        RESULT_REF.clear();
        types.clear();
    }

    public void giveMoney() {
        int money = MathUtils.randomRangeInt(20, (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        Core.economy.depositPlayer(player, money);
        itemStack = new ItemStack(Material.getMaterial(175));
        if (money > 3 * (int) SettingsManager.getConfig().get("TreasureChests.Loots.Money.Max") / 4) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Money.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Money.Message.message")).replace("%name%", player.getName()).replace("%money%", money + ""));
    }

    public void giveAmmo() {
        int i = MathUtils.randomRangeInt(0, gadgetList.size() - 1);
        Gadget g = gadgetList.get(i);
        int ammo = MathUtils.randomRangeInt((int)SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Min"), (int) SettingsManager.getConfig().get("TreasureChests.Loots.Gadgets-Ammo.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Gadget").replace("%name%", g.getName()).replace("%ammo%", ammo + "");
        gadgetList.remove(i);
        Core.getCustomPlayer(player).addAmmo(g.getType().toString().toLowerCase(), ammo);
        itemStack = new MaterialData(g.getMaterial(), g.getData()).toItemStack(1);
        if (ammo > 50) {
            spawnRandomFirework(loc);
        }
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Gadgets-Ammo.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Gadgets-Ammo.Message.message")).replace("%name%", player.getName()).replace("%ammo%", ammo + "").replace("%gadget%", (Core.placeHolderColor) ? g.getName() : Core.filterColor(g.getName())));

    }

    public void giveRandomHat() {
        int i = MathUtils.randomRangeInt(0, hatList.size() - 1);
        Hat hat = hatList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Hat").replace("%hat%", hat.getName());
        hatList.remove(i);
        givePermission(hat.getPermission());
        itemStack = hat.getItemStack().clone();
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Hats.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Hats.Message.message")).replace("%name%", player.getName()).replace("%hat%", (Core.placeHolderColor) ? hat.getName() : Core.filterColor(hat.getName())));
    }

    public void giveRandomPet() {
        int i = MathUtils.randomRangeInt(0, petList.size() - 1);
        Pet pet = petList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Pet").replace("%pet%", pet.getMenuName());
        petList.remove(i);
        givePermission(pet.getType().getPermission());
        itemStack = new ItemStack(pet.getMaterial());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Pets.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Pets.Message.message")).replace("%name%", player.getName()).replace("%pet%", (Core.placeHolderColor) ? pet.getMenuName() : Core.filterColor(pet.getMenuName())));
    }

    public void giveRandomMount() {
        int i = MathUtils.randomRangeInt(0, mountList.size() - 1);
        Mount mount = mountList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Mount").replace("%mount%", mount.getMenuName());
        mountList.remove(i);
        itemStack = new ItemStack(mount.getMaterial());
        givePermission(mount.getType().getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Mounts.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Mounts.Message.message")).replace("%name%", player.getName()).replace("%mount%", (Core.placeHolderColor) ? mount.getMenuName() : Core.filterColor(mount.getMenuName())));
    }

    public void giveRandomEffect() {
        int i = MathUtils.randomRangeInt(0, particleEffectList.size() - 1);
        ParticleEffect particleEffect = particleEffectList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Effect").replace("%effect%", particleEffect.getName());
        particleEffectList.remove(i);
        itemStack = new ItemStack(particleEffect.getMaterial());
        givePermission(particleEffect.getType().getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Effects.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Effects.Message.message")).replace("%name%", player.getName()).replace("%effect%", (Core.placeHolderColor) ? particleEffect.getName() : Core.filterColor(particleEffect.getName())));
    }

    public void giveRandomMorph() {
        int i = MathUtils.randomRangeInt(0, morphList.size() - 1);
        Morph morph = morphList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Morph").replace("%morph%", morph.getName());
        morphList.remove(i);
        itemStack = new ItemStack(morph.getMaterial());
        givePermission(morph.getType().getPermission());
        spawnRandomFirework(loc);
        if (SettingsManager.getConfig().getBoolean("TreasureChests.Loots.Morphs.Message.enabled"))
            Bukkit.broadcastMessage((getMessage("TreasureChests.Loots.Morphs.Message.message")).replace("%name%", player.getName()).replace("%morph%", (Core.placeHolderColor) ? morph.getName() : Core.filterColor(morph.getName())));
    }


    public static FireworkEffect getRandomFireworkEffect() {
        if (!Core.getPlugin().isEnabled())
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
        if (!Core.getPlugin().isEnabled())
            return;
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = player.getWorld().spawn(location.clone().add(0.5, 0, 0.5), Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
        }, 2);
    }

}
