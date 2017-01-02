package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Package: be.isach.ultracosmetics.command.subcommands
 * Created by: Sacha
 * Date: 21/12/15
 * Project: UltraCosmetics
 */
public class SubCommandToggle extends SubCommand {

    private UltraCosmetics ultraCosmetics;

    public SubCommandToggle(UltraCosmetics ultraCosmetics) {
        super("Toggles a cosmetic.", "ultracosmetics.command.toggle", "/uc toggle <type> <cosmetic> [player]", ultraCosmetics, "toggle");
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
//        if (args.length < 3) {
//            sender.sendMessage("  §c§lIncorrect Usage. " + getUsage());
//            return;
//        }
//        String type = args[1].toLowerCase();
//        Player receiver = sender;
//
//        if (type.startsWith("s")) {
//            if (args.length > 4
//                    && sender.hasPermission(getPermission() + ".others")) {
//                receiver = Bukkit.getBukkitPlayer(args[4]);
//                if (receiver == null) {
//                    sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
//                    return;
//                }
//            }
//        } else {
//            if (args.length > 3
//                    && sender.hasPermission(getPermission() + ".others")) {
//                receiver = Bukkit.getBukkitPlayer(args[3]);
//                if (receiver == null) {
//                    sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
//                    return;
//                }
//            }
//        }
//
//        if (type.startsWith("g")) {
//            GadgetType gadgetType;
//            try {
//                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < GadgetType.enabled().size(); i++)
//                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!gadgetType.isEnabled()) {
//                sender.sendMessage("  §c§lThis gadget isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = getUltraCosmetics().getPlayerManager().getUltraPlayer(receiver);
//            if (cp.getCurrentGadget() != null &&
//                    cp.getCurrentGadget().getType() == gadgetType) {
//                cp.removeGadget();
//                return;
//            }
//
//            gadgetType.equip(receiver, ultraCosmetics);
//        } else if (type.startsWith("pe")) {
//            PetType petType;
//            try {
//                petType = PetType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Pet"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < PetType.enabled().size(); i++)
//                    sb.append(PetType.enabled().get(i).toString().toLowerCase() + ((i != PetType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lPet Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!petType.isEnabled()) {
//                sender.sendMessage("  §c§lThis pet isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentPet != null &&
//                    cp.currentPet.getType() == petType) {
//                cp.removePet();
//                return;
//            }
//
//            MenuPets.equipPet(petType, receiver);
//        } else if (type.startsWith("pa")) {
//            ParticleEffectType effectType;
//            try {
//                effectType = ParticleEffectType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Effect"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < ParticleEffectType.enabled().size(); i++)
//                    sb.append(ParticleEffectType.enabled().get(i).toString().toLowerCase() + ((i != ParticleEffectType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lEffect Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!effectType.isEnabled()) {
//                sender.sendMessage("  §c§lThis effect isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentParticleEffect != null &&
//                    cp.currentParticleEffect.getType() == effectType) {
//                cp.removeParticleEffect();
//                return;
//            }
//
//            MenuParticleEffects.equipEffect(effectType, receiver);
//        } else if (type.startsWith("mou")) {
//            MountType mountType;
//            try {
//                mountType = MountType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < MountType.enabled().size(); i++)
//                    sb.append(MountType.enabled().get(i).toString().toLowerCase() + ((i != MountType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMount Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!mountType.isEnabled()) {
//                sender.sendMessage("  §c§lThis mount isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentMount != null &&
//                    cp.currentMount.getType() == mountType) {
//                cp.removeMount();
//                return;
//            }
//
//            MenuMounts.equipMount(mountType, receiver, ultraCosmetics);
//        } else if (type.startsWith("e")) {
//            EmoteType emoteType = null;
//            boolean stopNow = false;
//            try {
//                emoteType = EmoteType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                stopNow = true;
//            }
//
//            if(stopNow || emoteType == null) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Emote"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < EmoteType.enabled().size(); i++)
//                    sb.append(EmoteType.enabled().get(i).toString().toLowerCase() + ((i != EmoteType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lEmote Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!emoteType.isEnabled()) {
//                sender.sendMessage("  §c§lThis emote isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentEmote != null &&
//                    cp.currentEmote.getEmoteType() == emoteType) {
//                cp.removePet();
//                return;
//            }
//
//            MenuEmotes.equipEmote(emoteType, receiver);
//        } else if (type.startsWith("mor")) {
//            MorphType morphType;
//            try {
//                morphType = MorphType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < MorphType.enabled().size(); i++)
//                    sb.append(MorphType.enabled().get(i).toString().toLowerCase() + ((i != MorphType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!morphType.isEnabled()) {
//                sender.sendMessage("  §c§lThis morph isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentMorph != null &&
//                    cp.currentMorph.getType() == morphType) {
//                cp.removeMorph();
//                return;
//            }
//
//            MenuMorphs.equipMorph(morphType, receiver);
//        } else if (type.startsWith("h")) {
//            HatType hat;
//            try {
//                hat = HatType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Hat"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < HatType.enabled().size(); i++)
//                    sb.append(HatType.enabled().get(i).toString().toLowerCase() + ((i != HatType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!hat.isEnabled()) {
//                sender.sendMessage("  §c§lThis hat isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentHat != null &&
//                    cp.currentHat == hat) {
//                cp.removeHat();
//                return;
//            }
//
//            MenuHats.equipHat(hat, receiver);
//        } else if (type.startsWith("s")) {
//            String a;
//            if (args.length < 4) {
//                sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
//                return;
//            } else {
//                a = args[3];
//                if (!a.startsWith("h")
//                        && !a.startsWith("c")
//                        && !a.startsWith("l")
//                        && !a.startsWith("b")) {
//                    sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
//                    return;
//                }
//            }
//
//            ArmorSlot armorSlot = null;
//
//            switch (a.charAt(0)) {
//                case 'h':
//                    armorSlot = ArmorSlot.HELMET;
//                    break;
//                case 'c':
//                    armorSlot = ArmorSlot.CHESTPLATE;
//                    break;
//                case 'l':
//                    armorSlot = ArmorSlot.LEGGINGS;
//                    break;
//                case 'b':
//                    armorSlot = ArmorSlot.BOOTS;
//                    break;
//            }
//
//            if (armorSlot == null) return;
//
//            SuitType suitType;
//            try {
//                suitType = (SuitType) SuitType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Suit"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < SuitType.enabled().size(); i++)
//                    sb.append(SuitType.enabled().get(i).toString().toLowerCase() + ((i != SuitType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lSuit Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!suitType.isEnabled()) {
//                sender.sendMessage("  §c§lThis suit isn't enabled!");
//                return;
//            }
//
//            UltraPlayer customPlayer = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (customPlayer.getSuit(armorSlot) != null
//                    && customPlayer.getSuit(armorSlot).getType() == suitType) {
//                customPlayer.removeSuit(armorSlot);
//                return;
//            }
//            MenuSuits.equipSuit(suitType, receiver, armorSlot, ultraCosmetics);
//        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
//        if (args.length < 4) {
//            sender.sendMessage("  §c§lIncorrect Usage. /uc toggle <type> <cosmetic> <player>");
//            return;
//        }
//        String type = args[1].toLowerCase();
//        Player receiver;
//
//        if (type.startsWith("s")) {
//            if (args.length < 5) {
//                sender.sendMessage("  §c§lIncorrect Usage. /uc toggle suit <suit> <part> <player>");
//                return;
//            }
//            receiver = Bukkit.getBukkitPlayer(args[4]);
//            if (receiver == null) {
//                sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
//                return;
//            }
//        } else {
//            receiver = Bukkit.getBukkitPlayer(args[3]);
//            if (receiver == null) {
//                sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
//                return;
//            }
//        }
//
//        if (type.startsWith("g")) {
//            GadgetType gadgetType;
//            try {
//                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < GadgetType.enabled().size(); i++)
//                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!gadgetType.isEnabled()) {
//                sender.sendMessage("  §c§lThis gadget isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentGadget != null &&
//                    cp.currentGadget.getGadgetType() == gadgetType) {
//                cp.removeGadget();
//                return;
//            }
//
//            MenuGadgets_old.equipGadget(gadgetType, receiver, ultraCosmetics);
//        } else if (type.startsWith("pe")) {
//            PetType petType;
//            try {
//                petType = PetType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Pet"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < PetType.enabled().size(); i++)
//                    sb.append(PetType.enabled().get(i).toString().toLowerCase() + ((i != PetType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lPet Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!petType.isEnabled()) {
//                sender.sendMessage("  §c§lThis pet isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentPet != null &&
//                    cp.currentPet.getType() == petType) {
//                cp.removePet();
//                return;
//            }
//
//            MenuPets.equipPet(petType, receiver);
//        } else if (type.startsWith("pa")) {
//            ParticleEffectType effectType;
//            try {
//                effectType = ParticleEffectType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Effect"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < ParticleEffectType.enabled().size(); i++)
//                    sb.append(ParticleEffectType.enabled().get(i).toString().toLowerCase() + ((i != ParticleEffectType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lEffect Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!effectType.isEnabled()) {
//                sender.sendMessage("  §c§lThis effect isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentParticleEffect != null &&
//                    cp.currentParticleEffect.getType() == effectType) {
//                cp.removeParticleEffect();
//                return;
//            }
//
//            MenuParticleEffects.equipEffect(effectType, receiver);
//        } else if (type.startsWith("mou")) {
//            MountType mountType;
//            try {
//                mountType = MountType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < MountType.enabled().size(); i++)
//                    sb.append(MountType.enabled().get(i).toString().toLowerCase() + ((i != MountType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMount Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!mountType.isEnabled()) {
//                sender.sendMessage("  §c§lThis mount isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentMount != null &&
//                    cp.currentMount.getType() == mountType) {
//                cp.removeMount();
//                return;
//            }
//
//            MenuMounts.equipMount(mountType, receiver, ultraCosmetics);
//        } else if (type.startsWith("mor")) {
//            MorphType morphType;
//            try {
//                morphType = MorphType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < MorphType.enabled().size(); i++)
//                    sb.append(MorphType.enabled().get(i).toString().toLowerCase() + ((i != MorphType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!morphType.isEnabled()) {
//                sender.sendMessage("  §c§lThis morph isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentMorph != null &&
//                    cp.currentMorph.getType() == morphType) {
//                cp.removeMorph();
//                return;
//            }
//
//            MenuMorphs.equipMorph(morphType, receiver);
//        } else if (type.startsWith("h")) {
//            HatType hat;
//            try {
//                hat = HatType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Hat"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < HatType.enabled().size(); i++)
//                    sb.append(HatType.enabled().get(i).toString().toLowerCase() + ((i != HatType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lMorph Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!hat.isEnabled()) {
//                sender.sendMessage("  §c§lThis hat isn't enabled!");
//                return;
//            }
//
//            UltraPlayer cp = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (cp.currentHat != null &&
//                    cp.currentHat == hat) {
//                cp.removeHat();
//                return;
//            }
//
//            MenuHats.equipHat(hat, receiver);
//        } else if (type.startsWith("s")) {
//            String a;
//            if (args.length < 4) {
//                sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
//                return;
//            } else {
//                a = args[3];
//                if (!a.startsWith("h")
//                        && !a.startsWith("c")
//                        && !a.startsWith("l")
//                        && !a.startsWith("b")) {
//                    sender.sendMessage("§c§lIncorrect usage. /uc toggle suit <suit> helmet/chestplate/leggings/boots");
//                    return;
//                }
//            }
//
//            ArmorSlot armorSlot = null;
//
//            switch (a.charAt(0)) {
//                case 'h':
//                    armorSlot = ArmorSlot.HELMET;
//                    break;
//                case 'c':
//                    armorSlot = ArmorSlot.CHESTPLATE;
//                    break;
//                case 'l':
//                    armorSlot = ArmorSlot.LEGGINGS;
//                    break;
//                case 'b':
//                    armorSlot = ArmorSlot.BOOTS;
//                    break;
//            }
//
//            if (armorSlot == null) return;
//
//            SuitType suitType;
//            try {
//                suitType =  SuitType.valueOf(args[2].toUpperCase());
//            } catch (IllegalArgumentException exc) {
//                sender.sendMessage(MessageManager.getMessage("Invalid-Suit"));
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < SuitType.enabled().size(); i++)
//                    sb.append(SuitType.enabled().get(i).toString().toLowerCase() + ((i != SuitType.enabled().size() - 1) ? "§f§l, §c" : ""));
//                sender.sendMessage("§c§lSuit Types: §c" + sb.toString());
//                return;
//            }
//
//            if (!suitType.isEnabled()) {
//                sender.sendMessage("  §c§lThis suit isn't enabled!");
//                return;
//            }
//
//            UltraPlayer customPlayer = UltraCosmetics.getPlayerManager().getUltraPlayer(receiver);
//            if (customPlayer.getSuit(armorSlot) != null
//                    && customPlayer.getSuit(armorSlot).getType() == suitType) {
//                customPlayer.removeSuit(armorSlot);
//                return;
//            }
//            MenuSuits.equipSuit(suitType, receiver, armorSlot, ultraCosmetics);
//        }
    }
}
