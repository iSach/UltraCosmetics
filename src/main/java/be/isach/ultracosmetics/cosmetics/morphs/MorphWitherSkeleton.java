package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class MorphWitherSkeleton extends Morph {

    boolean inCooldown;

    public MorphWitherSkeleton(UUID owner) {
        super(DisguiseType.WITHER_SKELETON, Material.SKULL_ITEM, (byte) 1, "WitherSkeleton", "ultracosmetics.morphs.witherskeleton", owner, MorphType.WITHERSKELETON);
        Core.registerListener(this);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer()
                && !inCooldown) {
            inCooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    inCooldown = false;
                }
            }, 200);
            for(Entity ent : getPlayer().getNearbyEntities(3, 3, 3)) {
                if(ent instanceof Player || ent instanceof Creature)
                    MathUtils.applyVelocity(ent, ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).setY(1));
            }
            final List<Entity> items = new ArrayList<>();
            for(int i = 0; i < 20; i++) {
                Item bone = getPlayer().getWorld().dropItem(getPlayer().getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D), ItemFactory.create(Material.BONE, (byte)0, UUID.randomUUID().toString()));
                bone.setVelocity(MathUtils.getRandomVector());
                items.add(bone);
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                   for(Entity bone : items)
                       bone.remove();
                    items.clear();
                }
            }, 50);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.SKELETON_HURT, 0.4F, (float)(Math.random() + 1.0D));
        }
    }
}
