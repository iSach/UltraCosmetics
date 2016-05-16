package be.isach.ultracosmetics.cosmetics.gadgets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Matthew on 2016/5/16.
 */
public class Gadget_1_9 implements Listener{
    public Gadget obj;
    public Gadget_1_9(Gadget g){
        this.obj = g;
    }

    @EventHandler
    public void cancelOffHandMove(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() != null) {
            if (event.getMainHandItem().equals(obj.getItemStack())) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
                return;
            }
        }
        if (event.getOffHandItem() != null) {
            if (event.getOffHandItem().equals(obj.getItemStack())) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
                return;
            }
        }
    }


}
