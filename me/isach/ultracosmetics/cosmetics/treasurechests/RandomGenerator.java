package me.isach.ultracosmetics.cosmetics.treasurechests;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.morphs.Morph;
import me.isach.ultracosmetics.cosmetics.mounts.Mount;
import me.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import me.isach.ultracosmetics.cosmetics.pets.Pet;
import me.isach.ultracosmetics.util.MathUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sacha on 19/08/15.
 */
public class RandomGenerator {

    Player player;
    public Location loc;
    int percent;
    private Material material;
    private String name;
    private byte data = (byte) 0x0;

    public static List<Gadget> gadgetList = new ArrayList<>();
    public static List<ParticleEffect> particleEffectList = new ArrayList<>();
    public static List<Mount> mountList = new ArrayList<>();
    public static List<Pet> petList = new ArrayList<>();
    public static List<Morph> morphList = new ArrayList<>();

    public RandomGenerator(final Player player, Location location) {
        this.loc = location.add(0.5, 0, 0.5);
        this.player = player;
        for (Gadget g : Core.getGadgets()) {
            if (g.getType().isEnabled()
                    && player.hasPermission(g.getType().getPermission())
                    && g.getType().requiresAmmo())
                this.gadgetList.add(g);
        }
        if (petList.isEmpty())
            for (Pet pet : Core.getPets()) {
                if (pet.getType().isEnabled()
                        && !player.hasPermission(pet.getType().getPermission()))
                    this.petList.add(pet);
            }
        if (morphList.isEmpty())
            for (Morph morph : Core.getMorphs()) {
                if (morph.getType().isEnabled()
                        && !player.hasPermission(morph.getType().getPermission()))
                    this.morphList.add(morph);
            }
        if (particleEffectList.isEmpty())
            for (ParticleEffect particleEffect : Core.getParticleEffects()) {
                if (particleEffect.getType().isEnabled()
                        && !player.hasPermission(particleEffect.getType().getPermission()))
                    this.particleEffectList.add(particleEffect);
            }
        if (mountList.isEmpty())
            for (Mount m : Core.getMounts()) {
                if (m.getType().isEnabled()
                        && !player.hasPermission(m.getType().getPermission()))
                    this.mountList.add(m);
            }
        if (!Core.Category.MOUNTS.isEnabled())
            mountList.clear();
        if (!Core.Category.GADGETS.isEnabled())
            gadgetList.clear();
        if (!Core.Category.EFFECTS.isEnabled())
            particleEffectList.clear();
        if (!Core.Category.PETS.isEnabled())
            petList.clear();
        if (!Core.Category.MORPHS.isEnabled())
            morphList.clear();
    }

    public byte getData() {
        return data;
    }

    public Material getMaterial() {
        return material;
    }

    public void giveRandomThing() {
        percent = MathUtils.randomRangeInt(0, 100);
        try {
            if (percent >= 15) {
                giveRandomCommon();
                return;
            } else if (percent >= 4) {
                if (percent >= 10) {
                    giveRandomRarePet();
                    return;
                } else {
                    giveRandomRareMount();
                    return;
                }
            } else {
                if (percent < 4)
                    giveRandomLegendary();
                return;
            }
        } catch (IndexOutOfBoundsException exception) {
            giveMoney();
        }
        loc.getWorld().playSound(loc, Sound.CHEST_OPEN, 3, 1);
    }

    private void giveRandomCommon() {
        if (!(boolean) SettingsManager.getConfig().get("TreasureChests.Money-Loot.Enabled")) {
            giveAmmo();
            return;
        }
        if (percent >= 85)
            giveMoney();
        else
            giveAmmo();
    }

    public String getName() {
        return name;
    }

    public void clear() {
        petList.clear();
        gadgetList.clear();
        particleEffectList.clear();
        mountList.clear();
    }

    private void giveMoney() {
        int money = MathUtils.randomRangeInt(20, (int) SettingsManager.getConfig().get("TreasureChests.Money-Loot.Max"));
        name = MessageManager.getMessage("Treasure-Chests-Loot.Money").replace("%money%", money + "");
        Core.economy.depositPlayer(player, money);
        material = Material.getMaterial(175);
        if (money > 3 * (int) SettingsManager.getConfig().get("TreasureChests.Money-Loot.Max") / 4) {
            spawnRandomFirework(loc);
        }
    }

    private void giveAmmo() {
        int i = MathUtils.randomRangeInt(0, gadgetList.size() - 1);
        Gadget g = gadgetList.get(i);
        int ammo = MathUtils.randomRangeInt(15, 100);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Gadget").replace("%name%", g.getName()).replace("%ammo%", ammo + "");
        gadgetList.remove(i);
        Core.getCustomPlayer(player).addAmmo(g.getType().toString().toLowerCase(), ammo);
        material = g.getMaterial();
        data = g.getData();
        if (ammo > 50) {
            spawnRandomFirework(loc);
        }
    }

    private void giveRandomRarePet() {
        int i = MathUtils.randomRangeInt(0, petList.size() - 1);
        Pet pet = petList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Pet").replace("%pet%", pet.getMenuName());
        petList.remove(i);
        givePermission(pet.getType().getPermission());
        material = pet.getMaterial();
        spawnRandomFirework(loc);
    }

    public void givePermission(String permission) {
        String command = ((String) SettingsManager.getConfig().get("TreasureChests.Permission-Add-Command")).replace("%name%", player.getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private void giveRandomRareMount() {
        int i = MathUtils.randomRangeInt(0, mountList.size() - 1);
        Mount mount = mountList.get(i);
        name = MessageManager.getMessage("Treasure-Chests-Loot.Mount").replace("%mount%", mount.getMenuName());
        mountList.remove(i);
        material = mount.getMaterial();
        givePermission(mount.getType().getPermission());
        spawnRandomFirework(loc);
    }

    private void giveRandomLegendary() {
        int j = MathUtils.randomRangeInt(0, 2);
        if(j == 0) {
            int i = MathUtils.randomRangeInt(0, morphList.size() - 1);
            ParticleEffect particleEffect = particleEffectList.get(i);
            name = MessageManager.getMessage("Treasure-Chests-Loot.Effect").replace("%effect%", particleEffect.getName());
            particleEffectList.remove(i);
            material = particleEffect.getMaterial();
            givePermission(particleEffect.getType().getPermission());
            spawnRandomFirework(loc);
            Bukkit.broadcastMessage(MessageManager.getMessage("Found-Legendary").replace("%name%", player.getName()).replace("%found%", name));
            loc.getWorld().playSound(loc, Sound.WITHER_DEATH, 3, 1);
        } else {
            int i = MathUtils.randomRangeInt(0, morphList.size() - 1);
            Morph morph = morphList.get(i);
            name = MessageManager.getMessage("Treasure-Chests-Loot.Morph").replace("%morph%", morph.getName());
            particleEffectList.remove(i);
            material = morph.getMaterial();
            givePermission(morph.getType().getPermission());
            spawnRandomFirework(loc);
            Bukkit.broadcastMessage(MessageManager.getMessage("Found-Legendary").replace("%name%", player.getName()).replace("%found%", name));
            loc.getWorld().playSound(loc, Sound.WITHER_DEATH, 3, 1);
        }
    }


    public static FireworkEffect getRandomFireworkEffect() {
        if (!Core.getPlugin().isEnabled())
            return null;
        Random r = new Random();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).withFade(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).build();
        return effect;
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
