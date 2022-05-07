package be.isach.ultracosmetics.cosmetics.morphs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;

/**
 * Represents an instance of a mooshroom morph summoned by a player.
 *
 * @author RadBuilder
 * @since 07-03-2017
 */
public class MorphMooshroom extends Morph implements PlayerAffectingCosmetic {
    private boolean inCooldown = false;

    public MorphMooshroom(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("mooshroom"), ultraCosmetics);
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
                Item soup = getPlayer().getWorld().dropItem(getPlayer().getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D), ItemFactory
                        .create(XMaterial.MUSHROOM_STEW, UltraCosmeticsData.get().getItemNoPickupString()));
                soup.setVelocity(MathUtils.getRandomVector());
                soup.setMetadata("UNPICKABLEUP", new FixedMetadataValue(getUltraCosmetics(), ""));
                items.add(soup);
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                for (Entity soup : items) {
                    soup.remove();
                }
                items.clear();
            }, 50);
            XSound.ENTITY_SHEEP_SHEAR.play(getPlayer(), 0.4f, (float) Math.random() + 1f);
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    protected void onClear() {
    }
}
