package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.SheepWatcher;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cryptomorin.xseries.XSound;

/**
 * Represents an instance of a sheep morph summoned by a player.
 *
 * @author RadBuilder
 * @since 07-03-2017
 */
public class MorphSheep extends Morph {
    private long coolDown = 0;
    private int count = 0;

    public MorphSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("sheep"), ultraCosmetics);
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer() == getPlayer()) {
            if (coolDown > System.currentTimeMillis()) return;
            event.setCancelled(true);
            XSound.ENTITY_SHEEP_AMBIENT.play(event.getPlayer().getLocation(), 1.0f, 1.0f);
            SheepWatcher sheepWatcher = (SheepWatcher) getDisguise().getWatcher();
            count = 0;
            Bukkit.getScheduler().runTaskTimer(getUltraCosmetics(), () -> {
                if (count > 9) {
                    cancel();
                    return;
                }
                sheepWatcher.setColor(DyeColor.values()[RANDOM.nextInt(DyeColor.values().length)]);
                count++;
            }, 0, 2);
            coolDown = System.currentTimeMillis() + 3000;
        }
    }
}
