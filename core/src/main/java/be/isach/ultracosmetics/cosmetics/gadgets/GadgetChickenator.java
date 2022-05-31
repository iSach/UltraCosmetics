package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a chickenator gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetChickenator extends Gadget {

    private List<Item> items = new ArrayList<>();

    public GadgetChickenator(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("chickenator"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        final Chicken chicken = (Chicken) getPlayer().getWorld().spawnEntity(getPlayer().getEyeLocation(), EntityType.CHICKEN);
        chicken.setNoDamageTicks(500);
        chicken.setVelocity(getPlayer().getLocation().getDirection().multiply(Math.PI / 1.5));
        XSound.ENTITY_CHICKEN_AMBIENT.play(getPlayer(), 1.4f, 1.5f);
        XSound.ENTITY_GENERIC_EXPLODE.play(getPlayer(), 0.3f, 1.5f);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            spawnRandomFirework(chicken.getLocation());
            XSound.ENTITY_CHICKEN_HURT.play(getPlayer(), 1.4f, 1.5f);
            chicken.remove();
            for (int i = 0; i < 30; i++) {
                items.add(ItemFactory.createUnpickableItemVariance(XMaterial.COOKED_CHICKEN, chicken.getLocation(), RANDOM, 1));
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> items.forEach(Item::remove), 50);
        }, 9);
        getPlayer().updateInventory();
    }

    @Override
    public void onClear() {
        for (Item i : items) {
            i.remove();
        }
        items.clear();
    }

    public static FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).withFade(Color.WHITE).build();
    }

    public void spawnRandomFirework(Location location) {
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Firework f = getPlayer().getWorld().spawn(location, Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            fm.setDisplayName("uc_firework");
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            for (Firework f : fireworks)
                f.detonate();
        }, 2);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Firework)) return;
        FireworkMeta fm = ((Firework) event.getDamager()).getFireworkMeta();
        if (fm.getDisplayName().equals("uc_firework"))
            event.setCancelled(true);
    }
}
