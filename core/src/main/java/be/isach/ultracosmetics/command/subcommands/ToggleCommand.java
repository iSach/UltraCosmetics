package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.MorphType;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectType;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.SuitType;
import be.isach.ultracosmetics.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 21/12/15.
 */
public class ToggleCommand extends SubCommand {

    public ToggleCommand() {
        super("Toggles a cosmetic.", "ultracosmetics.command.toggle", "/uc toggle <type> <cosmetic> [player]", "toggle");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (args.length < 3) {
            sender.sendMessage("  §c§lIncorrect Usage. " + getUsage());
            return;
        }
        String type = args[1].toLowerCase();
        Player receiver = sender;

        if (type.startsWith("s")) {
            if (args.length > 4
                    && sender.hasPermission(getPermission() + ".others")) {
                receiver = Bukkit.getPlayer(args[4]);
                if (receiver == null) {
                    sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
                    return;
                }
            }
        } else {
            if (args.length > 3
                    && sender.hasPermission(getPermission() + ".others")) {
                receiver = Bukkit.getPlayer(args[3]);
                if (receiver == null) {
                    sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
                    return;
                }
            }
        }

        if (type.startsWith("g")) {
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage("  §c§lThis gadget isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentGadget != null &&
                    cp.currentGadget.getType() == gadgetType) {
                cp.removeGadget();
                return;
            }

            GadgetManager.equipGadget(gadgetType, receiver);
        } else if (type.startsWith("pe")) {
            PetType petType;
            try {
                petType = PetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Pet"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < PetType.enabled().size(); i++)
                    sb.append(PetType.enabled().get(i).toString().toLowerCase() + ((i != PetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lPet Types: §c" + sb.toString());
                return;
            }

            if (!petType.isEnabled()) {
                sender.sendMessage("  §c§lThis pet isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentPet != null &&
                    cp.currentPet.getType() == petType) {
                cp.removePet();
                return;
            }

            PetManager.equipPet(petType, receiver);
        } else if (type.startsWith("pa")) {
            ParticleEffectType effectType;
            try {
                effectType = ParticleEffectType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Effect"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ParticleEffectType.enabled().size(); i++)
                    sb.append(ParticleEffectType.enabled().get(i).toString().toLowerCase() + ((i != ParticleEffectType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lEffect Types: §c" + sb.toString());
                return;
            }

            if (!effectType.isEnabled()) {
                sender.sendMessage("  §c§lThis effect isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentParticleEffect != null &&
                    cp.currentParticleEffect.getType() == effectType) {
                cp.removeParticleEffect();
                return;
            }

            ParticleEffectManager.equipEffect(effectType, receiver);
        } else if (type.startsWith("mou")) {
            MountType mountType;
            try {
                mountType = MountType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MountType.enabled().size(); i++)
                    sb.append(MountType.enabled().get(i).toString().toLowerCase() + ((i != MountType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMount Types: §c" + sb.toString());
                return;
            }

            if (!mountType.isEnabled()) {
                sender.sendMessage("  §c§lThis mount isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentMount != null &&
                    cp.currentMount.getType() == mountType) {
                cp.removeMount();
                return;
            }

            MountManager.equipMount(mountType, receiver);
        } else if (type.startsWith("mor")) {
            MorphType morphType;
            try {
                morphType = MorphType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MorphType.enabled().size(); i++)
                    sb.append(MorphType.enabled().get(i).toString().toLowerCase() + ((i != MorphType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
                return;
            }

            if (!morphType.isEnabled()) {
                sender.sendMessage("  §c§lThis morph isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentMorph != null &&
                    cp.currentMorph.getType() == morphType) {
                cp.removeMorph();
                return;
            }

            MorphManager.equipMorph(morphType, receiver);
        } else if (type.startsWith("h")) {
            Hat hat;
            try {
                hat = Hat.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Hat"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Hat.enabled().size(); i++)
                    sb.append(Hat.enabled().get(i).toString().toLowerCase() + ((i != Hat.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
                return;
            }

            if (!hat.isEnabled()) {
                sender.sendMessage("  §c§lThis hat isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentHat != null &&
                    cp.currentHat == hat) {
                cp.removeHat();
                return;
            }

            HatManager.equipHat(hat, receiver);
        } else if (type.startsWith("s")) {
            String a;
            if (args.length < 4) {
                sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
                return;
            } else {
                a = args[3];
                if (!a.startsWith("h")
                        && !a.startsWith("c")
                        && !a.startsWith("l")
                        && !a.startsWith("b")) {
                    sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
                    return;
                }
            }

            ArmorSlot armorSlot = null;

            switch (a.charAt(0)) {
                case 'h':
                    armorSlot = ArmorSlot.HELMET;
                    break;
                case 'c':
                    armorSlot = ArmorSlot.CHESTPLATE;
                    break;
                case 'l':
                    armorSlot = ArmorSlot.LEGGINGS;
                    break;
                case 'b':
                    armorSlot = ArmorSlot.BOOTS;
                    break;
            }

            if (armorSlot == null) return;

            SuitType suitType;
            try {
                suitType = SuitType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Suit"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < SuitType.enabled().size(); i++)
                    sb.append(SuitType.enabled().get(i).toString().toLowerCase() + ((i != SuitType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lSuit Types: §c" + sb.toString());
                return;
            }

            if (!suitType.isEnabled()) {
                sender.sendMessage("  §c§lThis suit isn't enabled!");
                return;
            }

            CustomPlayer customPlayer = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (customPlayer.getSuit(armorSlot) != null
                    && customPlayer.getSuit(armorSlot).getType() == suitType) {
                customPlayer.removeSuit(armorSlot);
                return;
            }
            SuitManager.equipSuit(suitType, receiver, armorSlot);
        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        if (args.length < 4) {
            sender.sendMessage("  §c§lIncorrect Usage. /uc toggle <type> <cosmetic> <player>");
            return;
        }
        String type = args[1].toLowerCase();
        Player receiver;

        if (type.startsWith("s")) {
            if (args.length < 5) {
                sender.sendMessage("  §c§lIncorrect Usage. /uc toggle suit <suit> <part> <player>");
                return;
            }
            receiver = Bukkit.getPlayer(args[4]);
            if (receiver == null) {
                sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
                return;
            }
        } else {
            receiver = Bukkit.getPlayer(args[3]);
            if (receiver == null) {
                sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
                return;
            }
        }

        if (type.startsWith("g")) {
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage("  §c§lThis gadget isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentGadget != null &&
                    cp.currentGadget.getType() == gadgetType) {
                cp.removeGadget();
                return;
            }

            GadgetManager.equipGadget(gadgetType, receiver);
        } else if (type.startsWith("pe")) {
            PetType petType;
            try {
                petType = PetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Pet"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < PetType.enabled().size(); i++)
                    sb.append(PetType.enabled().get(i).toString().toLowerCase() + ((i != PetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lPet Types: §c" + sb.toString());
                return;
            }

            if (!petType.isEnabled()) {
                sender.sendMessage("  §c§lThis pet isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentPet != null &&
                    cp.currentPet.getType() == petType) {
                cp.removePet();
                return;
            }

            PetManager.equipPet(petType, receiver);
        } else if (type.startsWith("pa")) {
            ParticleEffectType effectType;
            try {
                effectType = ParticleEffectType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Effect"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ParticleEffectType.enabled().size(); i++)
                    sb.append(ParticleEffectType.enabled().get(i).toString().toLowerCase() + ((i != ParticleEffectType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lEffect Types: §c" + sb.toString());
                return;
            }

            if (!effectType.isEnabled()) {
                sender.sendMessage("  §c§lThis effect isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentParticleEffect != null &&
                    cp.currentParticleEffect.getType() == effectType) {
                cp.removeParticleEffect();
                return;
            }

            ParticleEffectManager.equipEffect(effectType, receiver);
        } else if (type.startsWith("mou")) {
            MountType mountType;
            try {
                mountType = MountType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MountType.enabled().size(); i++)
                    sb.append(MountType.enabled().get(i).toString().toLowerCase() + ((i != MountType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMount Types: §c" + sb.toString());
                return;
            }

            if (!mountType.isEnabled()) {
                sender.sendMessage("  §c§lThis mount isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentMount != null &&
                    cp.currentMount.getType() == mountType) {
                cp.removeMount();
                return;
            }

            MountManager.equipMount(mountType, receiver);
        } else if (type.startsWith("mor")) {
            MorphType morphType;
            try {
                morphType = MorphType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MorphType.enabled().size(); i++)
                    sb.append(MorphType.enabled().get(i).toString().toLowerCase() + ((i != MorphType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
                return;
            }

            if (!morphType.isEnabled()) {
                sender.sendMessage("  §c§lThis morph isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentMorph != null &&
                    cp.currentMorph.getType() == morphType) {
                cp.removeMorph();
                return;
            }

            MorphManager.equipMorph(morphType, receiver);
        } else if (type.startsWith("h")) {
            Hat hat;
            try {
                hat = Hat.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Hat"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Hat.enabled().size(); i++)
                    sb.append(Hat.enabled().get(i).toString().toLowerCase() + ((i != Hat.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
                return;
            }

            if (!hat.isEnabled()) {
                sender.sendMessage("  §c§lThis hat isn't enabled!");
                return;
            }

            CustomPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (cp.currentHat != null &&
                    cp.currentHat == hat) {
                cp.removeHat();
                return;
            }

            HatManager.equipHat(hat, receiver);
        } else if (type.startsWith("s")) {
            String a;
            if (args.length < 4) {
                sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
                return;
            } else {
                a = args[3];
                if (!a.startsWith("h")
                        && !a.startsWith("c")
                        && !a.startsWith("l")
                        && !a.startsWith("b")) {
                    sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
                    return;
                }
            }

            ArmorSlot armorSlot = null;

            switch (a.charAt(0)) {
                case 'h':
                    armorSlot = ArmorSlot.HELMET;
                    break;
                case 'c':
                    armorSlot = ArmorSlot.CHESTPLATE;
                    break;
                case 'l':
                    armorSlot = ArmorSlot.LEGGINGS;
                    break;
                case 'b':
                    armorSlot = ArmorSlot.BOOTS;
                    break;
            }

            if (armorSlot == null) return;

            SuitType suitType;
            try {
                suitType = SuitType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Suit"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < SuitType.enabled().size(); i++)
                    sb.append(SuitType.enabled().get(i).toString().toLowerCase() + ((i != SuitType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lSuit Types: §c" + sb.toString());
                return;
            }

            if (!suitType.isEnabled()) {
                sender.sendMessage("  §c§lThis suit isn't enabled!");
                return;
            }

            CustomPlayer customPlayer = UltraCosmetics.getPlayerManager().getCustomPlayer(receiver);
            if (customPlayer.getSuit(armorSlot) != null
                    && customPlayer.getSuit(armorSlot).getType() == suitType) {
                customPlayer.removeSuit(armorSlot);
                return;
            }
            SuitManager.equipSuit(suitType, receiver, armorSlot);
        }
    }
}
