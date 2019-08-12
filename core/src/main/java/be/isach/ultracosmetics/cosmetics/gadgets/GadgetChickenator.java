package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_IDLE, 1.4f, 1.5f);
		SoundUtil.playSound(getPlayer(), Sounds.EXPLODE, 0.3f, 1.5f);
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			spawnRandomFirework(chicken.getLocation());
			SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_HURT, 1.4f, 1.5f);
			chicken.remove();
			for (int i = 0; i < 30; i++) {
				final Item ITEM = chicken.getWorld().dropItem(chicken.getLocation(), ItemFactory.create(UCMaterial.COOKED_CHICKEN, UltraCosmeticsData.get().getItemNoPickupString()));
				ITEM.setPickupDelay(30000);
				ITEM.setVelocity(new Vector(MathUtils.random.nextDouble() - 0.5, MathUtils.random.nextDouble() / 2.0, MathUtils.random.nextDouble() - 0.5));
				items.add(ITEM);
			}
			Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> items.forEach(Item::remove), 50);
		}, 9);
		getPlayer().updateInventory();
	}
	
	@Override
	public void onUpdate() {
	}
	
	@Override
	public void onClear() {
		for (Item i : items) {
			i.remove();
		}
	}
	
	public static FireworkEffect getRandomFireworkEffect() {
		FireworkEffect.Builder builder = FireworkEffect.builder();
		return builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).withFade(Color.WHITE).build();
	}
	
	public void spawnRandomFirework(Location location) {
		final ArrayList<Firework> fireworks = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			final Firework f = getPlayer().getWorld().spawn(location, Firework.class);
			
			FireworkMeta fm = f.getFireworkMeta();
			fm.addEffect(getRandomFireworkEffect());
			f.setFireworkMeta(fm);
			fireworks.add(f);
		}
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			for (Firework f : fireworks)
				f.detonate();
		}, 2);
	}
	
	@Override
	void onLeftClick() {
	}
}
