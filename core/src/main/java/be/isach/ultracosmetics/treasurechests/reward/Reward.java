package be.isach.ultracosmetics.treasurechests.reward;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * @author RadBuilder
 * @since 01-14-2017
 */
public abstract class Reward {
    private UltraPlayer player;
	private UltraCosmetics ultraCosmetics;
	private RewardType type;

	public Reward(UltraPlayer owner, RewardType type, UltraCosmetics ultraCosmetics) {
    	this.player = owner;
    	this.ultraCosmetics = ultraCosmetics;
    	this.type = type;
    }
	
	public abstract boolean canEarn();
	public abstract void give();
    public abstract String getName();
    public abstract ItemStack getItemStack();
    
    public RewardType getType() {
    	return type;
    }
    
    public UltraPlayer getPlayer() {
    	return player;
    }
    
	public void firework(String color) {
		final Firework f = player.getBukkitPlayer().getWorld().spawn(player.getBukkitPlayer().getLocation().clone().add(0.5, 0, 0.5), Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(getRandomFireworkEffect(color));
        f.setFireworkMeta(fm);
        
        Bukkit.getScheduler().runTaskLater(ultraCosmetics, new Runnable() {
            @Override
            public void run() {
            	f.detonate();
            }
        }, 2L);
	}
	
    public void givePermission(String permission) {
        String command = SettingsManager.getConfig().getString("TreasureChests.Permission-Add-Command").replace("%name%", player.getBukkitPlayer().getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
	
	private FireworkEffect getRandomFireworkEffect(String color) {
        Color c = parseColor(color);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(c).withFade(c).build();
        return effect;
    }
	
	private Color parseColor(String color) {
		switch (color.toLowerCase()) {
		case "aqua":
			return Color.AQUA;
		case "black":
			return Color.BLACK;
		case "blue":
			return Color.BLUE;
		case "fuchsia":
			return Color.FUCHSIA;
		case "gray":
			return Color.GRAY;
		case "green":
			return Color.GREEN;
		case "lime":
			return Color.LIME;
		case "maroon":
			return Color.MAROON;
		case "navy":
			return Color.NAVY;
		case "olive":
			return Color.OLIVE;
		case "orange":
			return Color.ORANGE;
		case "purple":
			return Color.PURPLE;
		case "red":
			return Color.RED;
		case "silver":
			return Color.SILVER;
		case "teal":
			return Color.TEAL;
		case "white":
			return Color.WHITE;
		case "yellow":
			return Color.YELLOW;
		default:
			throw new IllegalArgumentException("Invalid color recieved: \"" + color + "\".");
		}
	}
}
