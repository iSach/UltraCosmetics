package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a melon thrower gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetMelonThrower extends Gadget implements Listener {
	
	private Random random = new Random();
	private Item melon = null;
	private World world = null;
	
	public GadgetMelonThrower(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.MELONTHROWER, ultraCosmetics);
	}
	
	@EventHandler
	public void onTakeUpMelon(PlayerPickupItemEvent event) {
		if (event.getItem().hasMetadata("UC#MELONITEM")
		    && event.getItem().getTicksLived() > 5
		    && affectPlayers) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
			SoundUtil.playSound(getPlayer().getLocation(), Sounds.BURP, 1.4f, 1.5f);
			event.setCancelled(true);
			event.getItem().remove();
		}
	}
	
	@Override
	void onRightClick() {
		this.world = getPlayer().getWorld();
		SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
		Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.MELON_BLOCK, (byte) 0x0, UltraCosmeticsData.get().getItemNoPickupString()));
		item.setPickupDelay(0);
		item.setMetadata("UNPICKABLEUP", new FixedMetadataValue(getUltraCosmetics(), "UC#MELONBLOCK"));
		item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
		melon = item;
	}
	
	@Override
	public void onUpdate() {
		try {
			Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
				if (melon == null || !melon.isValid()) {
					return;
				}
				if (melon.isOnGround()) {
					melon.getWorld().playEffect(melon.getLocation(), Effect.STEP_SOUND, 103);
					for (int i = 0; i < 8; i++) {
						final Item newItem = getPlayer().getWorld().dropItem(melon.getLocation(), ItemFactory.create(Material.MELON, (byte) 0x0, UltraCosmeticsData.get().getItemNoPickupString()));
						newItem.setVelocity(new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5).multiply(0.75D));
						newItem.setMetadata("UC#MELONITEM", new FixedMetadataValue(getUltraCosmetics(), "UC#MELONTHROWER"));
						Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), new BukkitRunnable() {
							@Override
							public void run() {
								if (newItem.isValid()) {
									newItem.remove();
								}
							}
						}, 100);
					}
					melon.remove();
					melon = null;
				}
			});
		} catch (Exception exc) {
		}
	}
	
	@Override
	public void onClear() {
		if (melon != null) {
			melon.remove();
		}
		
		if (world != null) {
			for (Item i : world.getEntitiesByClass(Item.class)) {
				if (i.hasMetadata("UC#MELONITEM")) {
					i.remove();
				}
			}
		}
		
	}
	
	@Override
	void onLeftClick() {
	}
}
