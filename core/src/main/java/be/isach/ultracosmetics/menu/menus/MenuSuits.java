package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Suit {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public final class MenuSuits extends CosmeticMenu<SuitType> {

    private static final int[] SLOTS = new int[] {10, 11, 12, 13, 14, 15, 16};

    public MenuSuits(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS);
    }

    @Override
    protected int getSize() {
        return 54;
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player, int page) {
        int from = (page - 1) * getItemsPerPage();
        int to = page * getItemsPerPage();
        List<SuitCategory> enabled = SuitCategory.enabled();
        for (int i = from; i < to && i < enabled.size(); i++) {
            SuitCategory cat = enabled.get(i);
            ItemStack wholeEquipStack = XMaterial.HOPPER.parseItem();
            ItemMeta wholeEquipMeta = wholeEquipStack.getItemMeta();
            wholeEquipMeta.setDisplayName(Category.SUITS.getActivateTooltip() + " " + MessageManager.getMessage("Suits." + cat.getConfigName() + ".whole-equip"));
            wholeEquipMeta.setLore(Arrays.asList("", MessageManager.getMessage("Suits.Whole-Equip-Lore"), ""));
            wholeEquipStack.setItemMeta(wholeEquipMeta);
            putItem(inventory, SLOTS[i % getItemsPerPage()] - 9, wholeEquipStack, clickData -> {
                for (ArmorSlot armorSlot : ArmorSlot.values()) {
                    SuitType type = cat.getPiece(armorSlot);
                    if (player.hasPermission(type.getPermission())) {
                        if (player.getSuit(armorSlot) != null
                                && player.getSuit(armorSlot).getType() == type) {
                            continue;
                        }
                        toggleOn(clickData.getClicker(), type, getUltraCosmetics());
                    }
                }
                if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                    player.getBukkitPlayer().closeInventory();
                } else {
                    open(player, getCurrentPage(player));
                }
            });
        }
    }

    @Override
    public List<SuitType> enabled() {
        return SuitType.enabled();
    }

    @Override
    protected Map<Integer,SuitType> getSlots(int page, UltraPlayer player) {
        int from = (page - 1) * getItemsPerPage();
        int to = page * getItemsPerPage();
        Map<Integer,SuitType> slots = new HashMap<>();
        List<SuitCategory> enabled = SuitCategory.enabled();
        for (int i = from; i < to && i < enabled.size(); i++) {
            SuitCategory cat = enabled.get(i);
            int row = 0;
            // always in order of: helmet, chestplate, leggings, boots.
            // places the suit parts in columns
            for (SuitType type : cat.getPieces()) {
                slots.put(SLOTS[i % getItemsPerPage()] + row++ * 9, type);
            }
        }
        return slots;
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        suitType.equip(ultraPlayer, ultraCosmetics);
    }

    protected void toggleOff(UltraPlayer ultraPlayer, ArmorSlot armorSlot) {
        ultraPlayer.removeSuit(armorSlot);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer, SuitType type) {
        if (type == null) {
            ultraPlayer.removeSuit();
            return;
        }
        toggleOff(ultraPlayer, type.getSlot());
    }

    @Override
    protected int getItemsPerPage() {
        return 7;
    }

    @Override
    protected int getMaxPages(UltraPlayer player) {
        int i = 0;
        for (SuitCategory cat : SuitCategory.enabled()) {
            if (player.hasPermission(cat.getHelmet().getPermission())
                    || player.hasPermission(cat.getChestplate().getPermission())
                    || player.hasPermission(cat.getLeggings().getPermission())
                    || player.hasPermission(cat.getBoots().getPermission())) {
                i++;
            }
        }
        return Math.max(1, ((i - 1) / getItemsPerPage()) + 1);
    }

    @Override
    public boolean hasEquipped(UltraPlayer ultraPlayer, SuitType type) {
        return ultraPlayer.hasSuitPartOn(type.getSlot());
    }
}