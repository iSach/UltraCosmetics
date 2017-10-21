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
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @author RadBuilder
 * @since 12-21-2015
 */
public class SubCommandToggle extends SubCommand {


	public SubCommandToggle(UltraCosmetics ultraCosmetics) {
		super("Toggles a cosmetic.", "ultracosmetics.command.toggle", "/uc toggle <type> <cosmetic> [player]", ultraCosmetics, "toggle");
	}

	@Override
	protected void onExePlayer(Player sender, String... args) {
		UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);

		if (args.length < 3) {
			sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l" + getUsage());
			return;
		}

		String type = args[1].toLowerCase();
		String cosm = args[2].toLowerCase();

		if (args.length > 3) {
			try {
				if (!UltraCosmeticsData.get().getEnabledWorlds().contains(Bukkit.getPlayer(args[3]).getWorld().getName())) {
					sender.sendMessage(MessageManager.getMessage("World-Disabled"));
					return;
				}
			} catch (Exception e) {
				sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
				return;
			}
		} else {
			if (!UltraCosmeticsData.get().getEnabledWorlds().contains(sender.getWorld().getName())) {
				sender.sendMessage(MessageManager.getMessage("World-Disabled"));
				return;
			}
		}

		Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
		if (categories.length == 1) {
			Category category = (Category) categories[0];
			if (args.length > 3) {
				try {
					UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
					if (category == Category.SUITS) {
						try {
							ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
							other.removeSuit(armorSlot);
						} catch (Exception ex) {
							sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
							return;
						}
					} else {
						other.removeCosmetic(category);
					}
				} catch (Exception exc) {
					sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
					return;
				}
			} else {
				if (ultraPlayer.getCosmetic(category) != null) {
					if (category == Category.SUITS) {
						try {
							ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
							ultraPlayer.removeSuit(armorSlot);
						} catch (Exception ex) {
							sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
						}
					} else {
						ultraPlayer.removeCosmetic(category);
					}
					return;
				}
			}
			Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().contains(cosm.split(":")[0])).toArray();
			if (cosmeticTypes.length == 1) {
				CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
				if (args.length > 3) {
					try {
						UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
						if (cosmeticType.getCategory() == Category.SUITS) {
							try {
								ArmorSlot armorSlot = ArmorSlot.getByName(cosm.split(":")[1]);
								SuitType suitType = SuitType.valueOf(cosm.split(":")[0]);
								suitType.equip(other, getUltraCosmetics(), armorSlot);
							} catch (Exception ex) {
								sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
							}
						} else {
							cosmeticType.equip(other, getUltraCosmetics());
						}
					} catch (Exception exc) {
						sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
					}
				} else {
					if (cosmeticType.getCategory() == Category.SUITS) {
						try {
							ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
							SuitType suitType = SuitType.valueOf(args[2].split(":")[0]);
							suitType.equip(ultraPlayer, getUltraCosmetics(), armorSlot);
						} catch (Exception ex) {
							sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
						}
					} else {
						cosmeticType.equip(ultraPlayer, getUltraCosmetics());
					}
				}
			} else {
				sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
			}
		} else {
			sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
		}
	}

	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		if (args.length < 4) {
			sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle <type> <cosmetic> <player>");
			return;
		}

		String type = args[1].toLowerCase();
		String cosm = args[2].toLowerCase();

		try {
			if (!UltraCosmeticsData.get().getEnabledWorlds().contains(Bukkit.getPlayer(args[3]).getWorld().getName())) {
				sender.sendMessage(MessageManager.getMessage("World-Disabled"));
				return;
			}		} catch (Exception e) {
			sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
			return;
		}

		Object[] categories = Arrays.stream(Category.values()).filter(category -> category.isEnabled() && category.toString().toLowerCase().startsWith(type)).toArray();
		if (categories.length == 1) {
			Category category = (Category) categories[0];
			try {
				UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
				if (category == Category.SUITS) {
					try {
						ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
						other.removeSuit(armorSlot);
					} catch (Exception ex) {
						sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
						return;
					}
				} else {
					other.removeCosmetic(category);
				}
			} catch (Exception exc) {
				sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
				return;
			}
			Object[] cosmeticTypes = category.getEnabled().stream().filter(cosmeticType -> cosmeticType.isEnabled() && cosmeticType.toString().toLowerCase().startsWith(cosm.split(":")[0])).toArray();
			if (cosmeticTypes.length == 1) {
				CosmeticType cosmeticType = (CosmeticType) cosmeticTypes[0];
				try {
					UltraPlayer other = getUltraCosmetics().getPlayerManager().getUltraPlayer(Bukkit.getPlayer(args[3]));
					if (cosmeticType.getCategory() == Category.SUITS) {
						try {
							ArmorSlot armorSlot = ArmorSlot.getByName(args[2].split(":")[1]);
							SuitType suitType = SuitType.valueOf(args[2].split(":")[0]);
							suitType.equip(other, getUltraCosmetics(), armorSlot);
						} catch (Exception ex) {
							sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§l/uc toggle suit <suit type:suit piece> <player>.");
						}
					} else {
						cosmeticType.equip(other, getUltraCosmetics());
					}
				} catch (Exception exc) {
					sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid player.");
				}
			} else {
				sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid cosmetic.");
			}
		} else {
			sender.sendMessage(MessageManager.getMessage("Prefix") + " §c§lInvalid category.");
		}
	}
}