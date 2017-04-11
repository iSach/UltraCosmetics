package be.isach.ultracosmetics.tempchests.reward;

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
	
	/**
	 * Clears the list of possible rewards.
	 */
	public abstract void clear();
	/**
	 * @return {@code true} if the {@link be.isach.ultracosmetics.tempchests.reward.RewardType RewardType}
	 * can be earned, {@code false} otherwise.
	 */
	public abstract boolean canEarn();
	/**
	 * Gives the reward to the player.
	 */
	public abstract void give();
    /**
     * @return The reward name.
     */
    public abstract String getName();
    /**
     * @return The reward ItemStack.
     */
    public abstract ItemStack getItemStack();
    
    /**
     * @return Gets the RewardType.
     */
    public RewardType getType() {
    	return type;
    }
    
    /**
     * @return Gets the player.
     */
    public UltraPlayer getPlayer() {
    	return player;
    }
    
	/**
	 * Spawns a firework at the player's location.
	 * 
	 * @param color The color of the firework.
	 */
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
	
    /**
     * Gives a permission to a player using the {@code Permission-Add-Command} specified in the config.
     * 
     * @param permission The permission to give to the player.
     */
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
