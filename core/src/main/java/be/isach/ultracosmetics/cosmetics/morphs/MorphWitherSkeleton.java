package be.isach.ultracosmetics.cosmetics.morphs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;

/**
 * @author iSach
 * @since 10-18-2015
 */
public class MorphWitherSkeleton extends Morph implements PlayerAffectingCosmetic {
    private boolean inCooldown;

    public MorphWitherSkeleton(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("witherskeleton"), ultraCosmetics);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && !inCooldown) {
            inCooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> inCooldown = false, 200);
            for (Entity ent : getPlayer().getNearbyEntities(3, 3, 3)) {
                if (canAffect(ent)) {
                    MathUtils.applyVelocity(ent, ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).setY(1));
                }
            }
            final List<Entity> items = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Location itemLoc = getPlayer().getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D);
                items.add(ItemFactory.spawnUnpickableItem(XMaterial.BONE.parseItem(), itemLoc, MathUtils.getRandomVector()));
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                for (Entity bone : items) {
                    bone.remove();
                }
                items.clear();
            }, 50);
            XSound.ENTITY_SKELETON_HURT.play(getPlayer(), 0.4f, (float) Math.random() + 1f);
        }
    }
}
