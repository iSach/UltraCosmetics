package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by sacha on 30/08/15.
 */
public class MorphMenuListener implements Listener {

    @EventHandler
    public void morphSelection(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Morphs"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Morphs"))
                    || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                return;
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                MenuListener.openMainMenu((Player) event.getWhoClicked());
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Third-Person-View"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).setSeeSelfMorph(true);
                event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.EYE_OF_ENDER, (byte) 0x0, MessageManager.getMessage("Disable-Third-Person-View")));
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    Morph morph = Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph;
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.disguiseType);
                    me.libraryaddict.disguise.DisguiseAPI.disguiseToAll(event.getWhoClicked(), morph.disguise);
                    morph.disguise.setShowName(true);
                    if (!Core.getCustomPlayer((Player) event.getWhoClicked()).canSeeSelfMorph())
                        morph.disguise.setViewSelfDisguise(false);
                }
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Third-Person-View"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).setSeeSelfMorph(false);
                event.getInventory().setItem(event.getSlot(), ItemFactory.create(Material.ENDER_PEARL, (byte) 0x0, MessageManager.getMessage("Enable-Third-Person-View")));
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    Morph morph = Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph;
                    morph.disguise = new me.libraryaddict.disguise.disguisetypes.MobDisguise(morph.disguiseType);
                    me.libraryaddict.disguise.DisguiseAPI.disguiseToAll(event.getWhoClicked(), morph.disguise);
                    morph.disguise.setShowName(true);
                    if (!Core.getCustomPlayer((Player) event.getWhoClicked()).canSeeSelfMorph())
                        morph.disguise.setViewSelfDisguise(false);
                }
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Morph"))) {
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).currentMorph != null) {
                    event.getWhoClicked().closeInventory();
                    Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                    MenuListener.openMorphsMenu((Player) event.getWhoClicked());
                } else return;
                return;
            }
            event.getWhoClicked().closeInventory();
            if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Unmorph"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                return;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Morph"))) {
                Core.getCustomPlayer((Player) event.getWhoClicked()).removeMorph();
                StringBuilder sb = new StringBuilder();
                String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Morph"), "");
                int j = name.split(" ").length;
                if (name.contains("("))
                    j--;
                for (int i = 1; i < j; i++) {
                    sb.append(name.split(" ")[i]);
                    try {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i + 1] != null)
                            sb.append(" ");
                    } catch (Exception exc) {

                    }
                }
                MenuListener.activateMorphByType(MenuListener.getMorphByName(sb.toString()), (Player) event.getWhoClicked());
            }

        }
    }

}
