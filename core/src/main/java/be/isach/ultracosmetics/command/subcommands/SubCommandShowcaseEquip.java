package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SubCommandShowcaseEquip extends SubCommand {

    private UltraCosmetics plugin;

    public SubCommandShowcaseEquip(UltraCosmetics ultraCosmetics) { // TODO: Permissions
        super("Equips a cosmetic.", "ultracosmetics.*", "/ucs equip <type> <cosmetic> [npc id]", ultraCosmetics, "equip");
        plugin = ultraCosmetics;
    }

    public boolean onCommand(CommandSender commandSender, String... args) {
        Player sender = (Player) commandSender;

        if (args.length < 3) {
            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + getUsage());
            return true;
        }

        NPC npcTarget;
        Player npc;

        // If no NPC specified, use the currently "selected" npc, else parse from arguments
        if (args.length < 4) {
            npcTarget = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "No NPC is selected.");
                return true;
            }
        }
        else {
            npcTarget = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
            if(npcTarget == null) {
                sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + "Invalid NPC ID.");
                return true;
            }
        }

        // Check if NPC is a player-type NPC
        if(npcTarget.getEntity() instanceof Player) npc = (Player) npcTarget.getEntity();
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
                        sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/ucs equip suit <suit type:suit piece> <npc id>.");
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
                            sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/ucs equip suit <suit type:suit piece> <npc id>.");
                        }
                    } else {
                        plugin.getNPCManager().AddNPC(npc.getUniqueId());
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

    @Override
    protected void onExePlayer(Player sender, String... args) {
        onCommand(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        onCommand(sender, args);
    }
}
