package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * 
 * @author 	iSach
 * @since 	10-18-2015
 */
public class MorphWitherSkeleton extends Morph {

    boolean inCooldown;

    public MorphWitherSkeleton(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.WITHERSKELETON, ultraCosmetics);
    }

    @Override
    protected void onEquip() {
    }

    @Override
    public void onUpdate() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && !inCooldown) {
            inCooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> inCooldown = false, 200);
            for (Entity ent : getPlayer().getNearbyEntities(3, 3, 3)) {
                if (ent instanceof Player || ent instanceof Creature)
                    MathUtils.applyVelocity(ent, ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).setY(1));
            }
            final List<Entity> items = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Item bone = getPlayer().getWorld().dropItem(getPlayer().getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D), ItemFactory.create(Material.BONE, (byte) 0, UUID.randomUUID().toString()));
                bone.setVelocity(MathUtils.getRandomVector());
                bone.setMetadata("UNPICKABLEUP", new FixedMetadataValue(getUltraCosmetics(), ""));
                items.add(bone);
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> {
                for (Entity bone : items)
                    bone.remove();
                items.clear();
            }, 50);
            SoundUtil.playSound(getPlayer(), Sounds.SKELETON_HURT, 0.4f, (float) Math.random() + 1f);
        }
    }
}
