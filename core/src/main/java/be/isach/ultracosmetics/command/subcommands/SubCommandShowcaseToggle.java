package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class SubCommandShowcaseToggle implements CommandExecutor {

    private UltraCosmetics plugin;

    public SubCommandShowcaseToggle(UltraCosmetics uc) {
        plugin = uc;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player sender = (Player) commandSender;

        if (args.length < 3) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "/ucs toggle <type> <cosmetic> [npc id]");
            return true;
        }

        Entity npcEntity;
        Player npc;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 4) {
            npcEntity = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender).getEntity();
        }
        else {
            npcEntity = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3])).getEntity();
        }

        // Check if NPC is a player-type NPC
        if(npcEntity instanceof Player) npc = (Player) npcEntity;
        else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "NPC is invalid. NPCs must be of player type.");
            return true;
        }

        String type = args[1].toLowerCase();
        String cosm = args[2].toLowerCase();

        try {
            if (!UltraCosmeticsData.get().getEnabledWorlds().contains(npc.getWorld().getName())) {
                sender.sendMessage(MessageManager.getMessage("World-Disabled"));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
            return true;
        }

        Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
        if (categories.length == 1) {
            Category category = (Category) categories[0];
            try {
                UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(npc);
                if (category == Category.SUITS) {
                    try {
                        ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
                        other.removeSuit(armorSlot);
                    } catch (Exception ex) {
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <npc id>.");
                        return true;
                    }
                } else {
                    other.removeCosmetic(category);
                }
            } catch (Exception exc) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
                return true;
            }

            Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).toArray();
            if (cosmeticTypes.length == 1) {
                CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];

                try {
                    UltraPlayer other = plugin.getPlayerManager().getUltraPlayer(npc);
                    if (cosmeticType.getCategory() == Category.SUITS) {
                        try {
                            ArmorSlot armorSlot = ArmorSlot.getByName(cosm.split(":")[1]);
                            SuitType suitType = SuitType.valueOf(cosm.split(":")[0]);
                            suitType.equip(other, plugin, armorSlot);
                        } catch (Exception ex) {
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <npc id>.");
                        }
                    } else {
                        cosmeticType.equip(other, plugin);
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §3§lSuccess.");
                    }
                } catch (Exception exc) {
                    sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid NPC ID.");
                }

            } else {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
            }
        } else {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
        }
        return true;
    }
}
