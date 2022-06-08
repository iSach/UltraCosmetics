package be.isach.ultracosmetics.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import be.isach.ultracosmetics.cosmetics.Category;

/**
 * Message manager.
 *
 * @author iSach
 * @since 03-08-2015
 */
public class MessageManager {
    private static final SettingsManager messagesConfig;
    // should be set to true by the time anybody else can read this
    private static boolean success = false;

    /**
     * Load the messages config
     */
    static {
        messagesConfig = new SettingsManager("messages");
        if (messagesConfig.success()) {
            loadMessages();
            messagesConfig.save();
            success = true;
        }
    }

    public static boolean success() {
        return success;
    }

    /**
     * Set up the messages in the config.
     */
    private static void loadMessages() {
        ConfigurationSection menuBlock = messagesConfig.getConfigurationSection("Menu");
        if (menuBlock != null && menuBlock.isString("Gadgets")) {
            upgradeCategoryStrings(menuBlock);
        }

        addMessage("Prefix", "&l&oCosmetics >&r");
        addMessage("No-Permission", "%prefix% &c&lYou don't have the permission!");
        addMessage("Cosmetic-Disabled", "%prefix% &c&lThis cosmetic is disabled!");
        addMessage("Invalid-Gadget", "%prefix% &c&lInvalid Gadget!");
        addMessage("Invalid-Pet", "%prefix% &c&lInvalid Pet!");
        addMessage("Invalid-Mount", "%prefix% &c&lInvalid Mount!");
        addMessage("Invalid-Effect", "%prefix% &c&lInvalid Effect!");
        addMessage("Invalid-Morph", "%prefix% &c&lInvalid Morph!");
        addMessage("Invalid-Hat", "%prefix% &c&lInvalid Hat!");
        addMessage("Invalid-Suit", "%prefix% &c&lInvalid Suit!");
        addMessage("Invalid-Emote", "%prefix% &c&lInvalid Emote!");
        addMessage("Invalid-Menu", "%prefix% &c&lInvalid Menu!");
        addMessage("Ammo", "&7Your ammo: &e%ammo%");
        addMessage("Purchase", "&a&lPURCHASE");
        addMessage("Cancel", "&c&lCANCEL");
        addMessage("Buy-Ammo-Description", "&e&lBuy %amount% %gadgetname% ammo &e&lfor $%price%");
        addMessage("Buy-Cosmetic-Description", "&e&lBuy %gadgetname% &e&lfor $%price%");
        addMessage("Right-Click-Buy-Ammo", "&7Right Click to Buy Ammo");
        addMessage("Right-Click-Purchase", "&eRight Click to purchase for $%price%");
        addMessage("Not-Enough-Money", "%prefix% &c&lYou don't have enough money!");
        addMessage("Successful-Purchase", "%prefix% &a&lSuccessful purchase!");
        addMessage("Buy-Treasure-Key", "&lBuy a Treasure Key");
        addMessage("Treasure-Keys", "&7&lTreasure Keys");
        addMessage("Treasure-Chests", "&6&lTreasure Chests");
        addMessage("Dont-Have-Key", "&4&lYou need a key!");
        addMessage("Click-Open-Chest", "&aClick to open a chest");
        addMessage("Click-Buy-Key", "&aClick to buy a key");
        addMessage("Your-Keys", "&7&oYour keys: &f&l%keys%");
        addMessage("Buy-Treasure-Key-ItemName", "&e&l1 &7&lTreasure Key &e&lfor $%price%");
        addMessage("Found-Legendary", "%prefix% &c&l%name% found Legendary %found%");
        addMessage("You-Won-Treasure-Chests", "%prefix% &f&lYou won: %name%!");
        addMessage("Treasure-Chest-Occupied", "%prefix% &c&lAll treasure locations are full, please wait and try again");

        addMessage("Clear.Cosmetics", "&c&lClear cosmetics");
        addMessage("Clear." + Category.GADGETS.getConfigPath(), "&c&lClear current gadget");
        addMessage("Clear." + Category.PETS.getConfigPath(), "&c&lClear current pet");
        addMessage("Clear." + Category.MOUNTS.getConfigPath(), "&c&lClear current mount");
        addMessage("Clear." + Category.EFFECTS.getConfigPath(), "&c&lClear current effect");
        addMessage("Clear." + Category.MORPHS.getConfigPath(), "&c&lClear current morph");
        addMessage("Clear." + Category.HATS.getConfigPath(), "&c&lClear current hat");
        addMessage("Clear." + Category.SUITS.getConfigPath(), "&c&lClear current suit");
        addMessage("Clear." + Category.EMOTES.getConfigPath(), "&c&lClear current emote");
        addMessage("Menu.Purchase-Rename.Button.Showcase", "&c&lRename the pet to &f&l%name% &c&lfor &e&l$%price%");
        addMessage("Menu.Purchase-Rename.Title", "&lRename Pet");

        addMessage("Chest-Location.Not-Enough-Space", "%prefix% &c&lThere isn't enough space for a treasure chest!");
        addMessage("Chest-Location.Too-Close", "%prefix% &c&lYou are too close to another treasure chest!");
        addMessage("Chest-Location.Region-Disabled", "%prefix% &c&lYou can't open a chest here!");
        addMessage("Chest-Location.Invalid", "%prefix% &c&lThat's not a valid location.");
        addMessage("Chest-Location.In-Air", "%prefix% &c&lThat location is not on the ground!");
        addMessage("Chest-Location.In-Ground", "%prefix% &c&lThat location is in the ground!");
        addMessage("Chest-Location.Suggestion", "%prefix% &aMaybe you meant %location%?");

        addMessage("Active-Pet-Needed", "&c&lYou need to spawn a pet to rename it");
        addMessage("Menu.Rename-Pet.Button.Name", "&c&lClick to rename: %petname%");
        addMessage("Menu.Rename-Pet.Placeholder", "Pet Name");
        addMessage("Menu.Rename-Pet.Title", "Rename pet");

        addMessage("Treasure-Chests-Loot.Ammo", "%ammo% %name% ammo");
        addMessage("Treasure-Chests-Loot.Pet", "%pet% pet");
        addMessage("Treasure-Chests-Loot.Mount", "%mount% mount");
        addMessage("Treasure-Chests-Loot.Effect", "%effect% effect");
        addMessage("Treasure-Chests-Loot.Morph", "%morph% morph");
        addMessage("Treasure-Chests-Loot.Hat", "%hat% hat");
        addMessage("Treasure-Chests-Loot.Money", "&e&l%money%$");
        String oldGadgetKey = "Treasure-Chests-Loot.gadget";
        String newGadgetKey = "Treasure-Chests-Loot.Gadget";
        String gadgetMessage = messagesConfig.getString(oldGadgetKey);
        if (gadgetMessage != null) {
            messagesConfig.set(newGadgetKey, gadgetMessage);
            messagesConfig.set(oldGadgetKey, null);
        }
        addMessage("Treasure-Chests-Loot.Gadget", "%gadget% gadget");
        addMessage("Treasure-Chests-Loot.Suit", "%suit%");
        addMessage("Treasure-Chests-Loot.Emote", "%emote% emote");
        addMessage("Treasure-Chests-Loot.Nothing", "&c&lNothing");

        // MENUS
        addMessage("Menu.Main.Title", "&lMain Menu");
        addMessage("Menu.Pets.Title", "&lPets");
        addMessage("Menu.Gadgets.Title", "&lGadgets");
        addMessage("Menu.Mounts.Title", "&lMounts");
        addMessage("Menu.Morphs.Title", "&lMorphs");
        addMessage("Menu.Hats.Title", "&lHats");
        addMessage("Menu.Particle-Effects.Title", "&lParticle Effects");
        addMessage("Menu.Suits.Title", "&lSuits");
        addMessage("Menu.Emotes.Title", "&lEmotes");
        addMessage("Menu.Buy-Ammo.Title", "&lBuy Ammo");
        addMessage("Menu.Rename-Pet.Title", "&lRename Pet");
        addMessage("Disable-Gadgets", "&c&lDisable Gadgets");
        addMessage("Enable-Gadgets", "&a&lEnable Gadgets");
        addMessage("Disable-Third-Person-View", "&c&lDisable Morphs Third Person View");
        addMessage("Enable-Third-Person-View", "&a&lEnable Morphs Third Person View");
        addMessage("Disable-Filter-By-Owned", "&c&lChange to Show All");
        addMessage("Enable-Filter-By-Owned", "&c&lChange to Filter By Owned");
        addMessage("Disable-Treasure-Notification", "&c&lTreasure notifications disabled");
        addMessage("Enable-Treasure-Notification", "&c&lTreasure notifications enabled");
        addMessage("Gadgets-Enabled-Needed", "%prefix% &c&lYou need to enable gadgets!");

        // GADGETS
        addMessage("Gadgets.Equip", "%prefix% &9You equipped %gadgetname%");
        addMessage("Gadgets.Unequip", "%prefix% &9You unequipped %gadgetname%");
        addMessage("Gadgets.Countdown-Message", "%prefix% &c&lYou can't use %gadgetname% &c&lfor %time%s!");
        addMessage("Gadgets.PaintballGun.name", "&b&lPaintball Gun");
        addMessage("Gadgets.BatBlaster.name", "&7&lBat Blaster");
        addMessage("Gadgets.MelonThrower.name", "&a&lMelon Thrower");
        addMessage("Gadgets.MelonThrower.Wait-For-Finish", "%prefix% &c&lWait for your previous melon to finish exploding!");
        addMessage("Gadgets.EtherealPearl.name", "&5&lEthereal Pearl");
        addMessage("Gadgets.FleshHook.name", "&7&lFlesh Hook");
        addMessage("Gadgets.DiscoBall.name", "&d&lDisco Ball");
        addMessage("Gadgets.DiscoBall.Already-Active", "&c&lThere is already a disco ball active!");
        addMessage("Gadgets.DiscoBall.Not-Space-Above", "&c&lThere is not enough space above you!");
        addMessage("Gadgets.ColorBomb.name", "&d&lColor Bomb");
        addMessage("Gadgets.Chickenator.name", "&f&lChickenator");
        addMessage("Gadgets.PortalGun.name", "&c&lPortal &9&lGun");
        addMessage("Gadgets.PortalGun.No-Block-Range", "%prefix% &c&lNo BLOCKS in range!");
        addMessage("Gadgets.PortalGun.Different-Worlds", "%prefix% &c&lPortals must be in the same world!");
        addMessage("Gadgets.BlizzardBlaster.name", "&b&lBlizzard Blaster");
        addMessage("Gadgets.ThorHammer.name", "&f&lThor's Hammer");
        addMessage("Gadgets.SmashDown.name", "&c&lSmashDown");
        addMessage("Gadgets.ExplosiveSheep.name", "&4&lExplosive Sheep");
        addMessage("Gadgets.ExplosiveSheep.Already-Active", "&c&lThere is already a an explosive sheep active!");
        addMessage("Gadgets.AntiGravity.name", "&d&lAnti Gravity");
        addMessage("Gadgets.Tsunami.name", "&9&lTsunami");
        addMessage("Gadgets.Rocket.name", "&4&lRocket");
        addMessage("Gadgets.Rocket.Not-Enough-Space", "%prefix% &c&lNot enough space around and above you!");
        addMessage("Gadgets.Rocket.Not-On-Ground", "%prefix% &c&lYou must be on the ground!");
        addMessage("Gadgets.Rocket.Takeoff", "&4&lTAKEOFF!!");
        addMessage("Gadgets.Rocket.LaunchAborted", "&c&lLaunch aborted!");
        addMessage("Gadgets.BlackHole.name", "&1&lBlack Hole");
        addMessage("Gadgets.TNT.name", "&4&lT&f&lN&4&lT");
        addMessage("Gadgets.FunGun.name", "&6&lFun Gun");
        addMessage("Gadgets.Parachute.name", "&f&lParachute");
        addMessage("Gadgets.QuakeGun.name", "&5&lQuake &d&lGun");
        addMessage("Gadgets.GhostParty.name", "&f&lGhost Party");
        addMessage("Gadgets.Firework.name", "&c&lFirework");
        addMessage("Gadgets.ChristmasTree.name", "&2&lChristmas Tree");
        addMessage("Gadgets.ChristmasTree.Click-On-Block", "%prefix% &c&lPlease click on a block!");
        addMessage("Gadgets.FreezeCannon.name", "&b&lFreeze Cannon");
        addMessage("Gadgets.Snowball.name", "&f&lSnowball");
        addMessage("Gadgets.PartyPopper.name", "&e&lParty Popper");
        addMessage("Gadgets.Trampoline.name", "&9&lTrampoline");
        addMessage("Gadgets.Gadget-Ready-ActionBar", "%gadgetname% &f&lis ready!");
        addMessage("Gadgets.Lore", "&9Gadget");

        // MOUNTS
        addMessage("Mounts.DruggedHorse.menu-name", "&2&lDrugged Horse");
        addMessage("Mounts.DruggedHorse.entity-displayname", "&l%playername%'s drugged horse");
        addMessage("Mounts.InfernalHorror.menu-name", "&4&lInfernal Horror");
        addMessage("Mounts.InfernalHorror.entity-displayname", "&l%playername%'s infernal horror");
        addMessage("Mounts.GlacialSteed.menu-name", "&b&lGlacial Steed");
        addMessage("Mounts.GlacialSteed.entity-displayname", "&l%playername%'s glacial steed");
        addMessage("Mounts.WalkingDead.menu-name", "&2&lWalking Dead");
        addMessage("Mounts.WalkingDead.entity-displayname", "&l%playername%'s walking dead");
        addMessage("Mounts.MountOfFire.menu-name", "&c&lMount of Fire");
        addMessage("Mounts.MountOfFire.entity-displayname", "&l%playername%'s mount of fire");
        addMessage("Mounts.MountOfWater.menu-name", "&9&lMount of Water");
        addMessage("Mounts.MountOfWater.entity-displayname", "&l%playername%'s mount of water");
        addMessage("Mounts.EcologistHorse.menu-name", "&a&lEcologist Horse");
        addMessage("Mounts.EcologistHorse.entity-displayname", "&l%playername%'s ecologist horse");
        addMessage("Mounts.NyanSheep.menu-name", "&4&lNy&6&la&e&ln &a&lSh&b&lee&d&lp");
        addMessage("Mounts.NyanSheep.entity-displayname", "&l%playername%'s nyan sheep");
        addMessage("Mounts.Snake.menu-name", "&6&lSnake");
        addMessage("Mounts.Snake.entity-displayname", "&l%playername%'s snake");
        addMessage("Mounts.Dragon.menu-name", "&5&lDragon");
        addMessage("Mounts.Dragon.entity-displayname", "&l%playername%'s dragon");
        addMessage("Mounts.SkySquid.menu-name", "&9&lSky Squid");
        addMessage("Mounts.SkySquid.entity-displayname", "&l%playername%'s sky squid");
        addMessage("Mounts.HypeCart.menu-name", "&7&lHypeCart");
        addMessage("Mounts.HypeCart.entity-displayname", "&l%playername%'s hypecart");
        addMessage("Mounts.Slime.menu-name", "&a&lSlime");
        addMessage("Mounts.Slime.entity-displayname", "&l%playername%'s slime");
        addMessage("Mounts.Spider.menu-name", "&8&lSpider");
        addMessage("Mounts.Spider.entity-displayname", "&l%playername%'s spider");
        addMessage("Mounts.Rudolph.menu-name", "&f&lRudolp&4&lh");
        addMessage("Mounts.Rudolph.entity-displayname", "&l%playername%'s rudolph");
        addMessage("Mounts.MoltenSnake.menu-name", "&c&lMolten Snake");
        addMessage("Mounts.MoltenSnake.entity-displayname", "&l%playername%'s Molten Snake");
        addMessage("Mounts.FlyingShip.menu-name", "&a&lFlying Ship");
        addMessage("Mounts.FlyingShip.entity-displayname", "&l%playername%'s Flying Ship");
        addMessage("Mounts.Equip", "%prefix% &9You spawned %mountname%");
        addMessage("Mounts.Unequip", "%prefix% &9You despawned %mountname%");
        addMessage("Mounts.Cant-Spawn", "%prefix% &c&lMonsters can't spawn here!");
        addMessage("Mounts.Not-Enough-Room", "%prefix% &c&lNot enough room for a mount here!");

        // PARTICLE-EFFECTS
        addMessage("Particle-Effects.Equip", "%prefix% &9You summoned %effectname%");
        addMessage("Particle-Effects.Unequip", "%prefix% &9You unsummoned %effectname%");
        addMessage("Particle-Effects.RainCloud.name", "&9&lRain Cloud");
        addMessage("Particle-Effects.SnowCloud.name", "&f&lSnow Cloud");
        addMessage("Particle-Effects.BloodHelix.name", "&4&lBlood Helix");
        addMessage("Particle-Effects.FrostLord.name", "&b&lFrost Lord");
        addMessage("Particle-Effects.FlameRings.name", "&c&lFlame Rings");
        addMessage("Particle-Effects.GreenSparks.name", "&a&lGreen Sparks");
        addMessage("Particle-Effects.InLove.name", "&c&lIn Love");
        addMessage("Particle-Effects.FrozenWalk.name", "&b&lFrozen Walk");
        addMessage("Particle-Effects.Enchanted.name", "&7&lEnchanted");
        addMessage("Particle-Effects.Music.name", "&9&lMusic");
        addMessage("Particle-Effects.Inferno.name", "&4&lInferno");
        addMessage("Particle-Effects.AngelWings.name", "&f&lAngel Wings");
        addMessage("Particle-Effects.SuperHero.name", "&4&lSuper Hero");
        addMessage("Particle-Effects.SantaHat.name", "&4&lSanta &f&lHat");
        addMessage("Particle-Effects.CrushedCandyCane.name", "&4&lCrushed &f&lCandy &4&lCane");
        addMessage("Particle-Effects.EnderAura.name", "&d&lEnder Aura");
        addMessage("Particle-Effects.FlameFairy.name", "&6&lFlame Fairy");
        addMessage("Particle-Effects.MagicalRods.name", "&8&lMagical Rods");
        addMessage("Particle-Effects.FireWaves.name", "&e&lFire Waves");

        // PETS
        addMessage("Pets.Piggy.menu-name", "&d&lPiggy");
        addMessage("Pets.Piggy.entity-displayname", "&l%playername%'s piggy");
        addMessage("Pets.Sheep.menu-name", "&f&lSheep");
        addMessage("Pets.Sheep.entity-displayname", "&l%playername%'s sheep");
        addMessage("Pets.EasterBunny.menu-name", "&6&lEaster Bunny");
        addMessage("Pets.EasterBunny.entity-displayname", "&l%playername%'s easter bunny");
        addMessage("Pets.Cow.menu-name", "&c&lCow");
        addMessage("Pets.Cow.entity-displayname", "&l%playername%'s cow");
        addMessage("Pets.Kitty.menu-name", "&9&lKitty");
        addMessage("Pets.Kitty.entity-displayname", "&l%playername%'s kitty");
        addMessage("Pets.Dog.menu-name", "&7&lDog");
        addMessage("Pets.Dog.entity-displayname", "&l%playername%'s dog");
        addMessage("Pets.Chick.menu-name", "&e&lChick");
        addMessage("Pets.Chick.entity-displayname", "&l%playername%'s chick");
        addMessage("Pets.Wither.menu-name", "&8&lWither");
        addMessage("Pets.Wither.entity-displayname", "&l%playername%'s wither");
        addMessage("Pets.Pumpling.menu-name", "&6&lPumpling");
        addMessage("Pets.Pumpling.entity-displayname", "&l%playername%'s pumpling");
        addMessage("Pets.CompanionCube.menu-name", "&d&lCompanion Cube");
        addMessage("Pets.CompanionCube.entity-displayname", "&l%playername%'s cube");
        addMessage("Pets.ChristmasElf.menu-name", "&a&lChristmas Elf");
        addMessage("Pets.ChristmasElf.entity-displayname", "&l%playername%'s Christmas elf");
        addMessage("Pets.Mooshroom.menu-name", "&4&lMooshroom");
        addMessage("Pets.Mooshroom.entity-displayname", "&l%playername%'s Mooshroom");
        addMessage("Pets.IronGolem.menu-name", "&7&lIronGolem");
        addMessage("Pets.IronGolem.entity-displayname", "&l%playername%'s IronGolem");
        addMessage("Pets.Snowman.menu-name", "&f&lSnowman");
        addMessage("Pets.Snowman.entity-displayname", "&l%playername%'s Snowman");
        addMessage("Pets.Villager.menu-name", "&a&lVillager");
        addMessage("Pets.Villager.entity-displayname", "&l%playername%'s Villager");
        addMessage("Pets.Bat.menu-name", "&8&lBat");
        addMessage("Pets.Bat.entity-displayname", "&l%playername%'s Bat");
        addMessage("Pets.PolarBear.menu-name", "&b&lPolarBear");
        addMessage("Pets.PolarBear.entity-displayname", "&l%playername%'s PolarBear");
        addMessage("Pets.Llama.menu-name", "&7&lLlama");
        addMessage("Pets.Llama.entity-displayname", "&l%playername%'s Llama");
        addMessage("Pets.Parrot.menu-name", "&a&lParrot");
        addMessage("Pets.Parrot.entity-displayname", "&l%playername%'s Parrot");
        addMessage("Pets.Vex.menu-name", "&7&lVex");
        addMessage("Pets.Vex.entity-displayname", "&l%playername%'s Vex");
        addMessage("Pets.Panda.menu-name", "&a&lPanda");
        addMessage("Pets.Panda.entity-displayname", "&l%playername%'s Panda");
        addMessage("Pets.Fox.menu-name", "&d&lFox");
        addMessage("Pets.Fox.entity-displayname", "&l%playername%'s Fox");
        addMessage("Pets.Axolotl.menu-name", "&5&lAxolotl");
        addMessage("Pets.Axolotl.entity-displayname", "&l%playername%'s Axolotl");
        addMessage("Pets.Piglin.menu-name", "&6&lGold Dealer");
        addMessage("Pets.Piglin.entity-displayname", "&l%playername%'s Gold Dealer");
        addMessage("Pets.Slime.menu-name", "&a&lSlime");
        addMessage("Pets.Slime.entity-displayname", "&l%playername%'s Slime");
        addMessage("Pets.Bee.menu-name", "&e&lBee");
        addMessage("Pets.Bee.entity-displayname", "&l%playername%'s Bee");
        addMessage("Pets.Goat.menu-name", "&f&lGoat");
        addMessage("Pets.Goat.entity-displayname", "&l%playername%'s Goat");
        addMessage("Pets.Silverfish.menu-name", "&7&lSilverfish");
        addMessage("Pets.Silverfish.entity-displayname", "&l%playername%'s Silverfish");
        addMessage("Pets.Horse.menu-name", "&a&lHorse");
        addMessage("Pets.Horse.entity-displayname", "&l%playername%'s Horse");
        addMessage("Pets.Blaze.menu-name", "&c&lBlaze");
        addMessage("Pets.Blaze.entity-displayname", "&l%playername%'s Blaze");
        addMessage("Pets.Creeper.menu-name", "&a&lCreeper");
        addMessage("Pets.Creeper.entity-displayname", "&l%playername%'s Creeper");
        addMessage("Pets.Enderman.menu-name", "&5&lEnderman");
        addMessage("Pets.Enderman.entity-displayname", "&l%playername%'s Enderman");
        addMessage("Pets.Skeleton.menu-name", "&f&lSkeleton");
        addMessage("Pets.Skeleton.entity-displayname", "&l%playername%'s Skeleton");
        addMessage("Pets.Zombie.menu-name", "&2&lZombie");
        addMessage("Pets.Zombie.entity-displayname", "&l%playername%'s Zombie");
        addMessage("Pets.Frog.menu-name", "&2&lFrog");
        addMessage("Pets.Frog.entity-displayname", "&l%playername%'s Frog");
        addMessage("Pets.Warden.menu-name", "&3&lWarden");
        addMessage("Pets.Warden.entity-displayname", "&l%playername%'s Warden");
        addMessage("Pets.Equip", "%prefix% &9You spawned %petname%");
        addMessage("Pets.Unequip", "%prefix% &9You despawned %petname%");

        // MORPHS
        addMessage("Morphs.Blaze.name", "&6&lBlaze");
        addMessage("Morphs.Blaze.skill", "&eCrouch&7 to: &aFly");
        addMessage("Morphs.Chicken.name", "&f&lChicken");
        addMessage("Morphs.Chicken.skill", "&eCrouch&7 to: &aLay eggs");
        addMessage("Morphs.Slime.name", "&a&lSlime");
        addMessage("Morphs.Slime.skill", "&eCrouch&7 to: &aBounce");
        addMessage("Morphs.Pig.name", "&d&lPig");
        addMessage("Morphs.Pig.skill", "&eCollide&7 to: &aBounce Players");
        addMessage("Morphs.Enderman.name", "&5&lEnderman");
        addMessage("Morphs.Enderman.skill", "&eDouble Jump&7 to: &aTeleport");
        addMessage("Morphs.Bat.name", "&8&lBat");
        addMessage("Morphs.Bat.skill", "&eDouble Jump&7 to: &aFlap");
        addMessage("Morphs.Creeper.name", "&a&lCreeper");
        addMessage("Morphs.Creeper.skill", "&eCrouch&7 to: &aDetonate");
        addMessage("Morphs.Creeper.charging", "&a&lCharging: %chargelevel%/100");
        addMessage("Morphs.Creeper.release-to-explode", "&f&lRelease to explode!");
        addMessage("Morphs.Snowman.name", "&f&lSnowman");
        addMessage("Morphs.Snowman.skill", "&eLeft Click&7 to: &aThrow Snowball");
        addMessage("Morphs.Equip", "%prefix% &9You morphed into %morphname%");
        addMessage("Morphs.Unequip", "%prefix% &9You unmorphed from %morphname%");
        addMessage("Morphs.WitherSkeleton.name", "&8&lWither Skeleton");
        addMessage("Morphs.WitherSkeleton.skill", "&eSneak&7 to: &aBone Bomb");
        addMessage("Morphs.ElderGuardian.name", "&8&lElder Guardian");
        addMessage("Morphs.ElderGuardian.skill", "&eLeft-Click&7 to: &aLaser");
        addMessage("Morphs.Cow.name", "&8&lCow");
        addMessage("Morphs.Cow.skill", "&eLeft-Click&7 to: &aMoo");
        addMessage("Morphs.Sheep.name", "&f&lSheep");
        addMessage("Morphs.Sheep.skill", "&eLeft-Click&7 to: &aChange Colors");
        addMessage("Morphs.Mooshroom.name", "&c&lMooshroom");
        addMessage("Morphs.Mooshroom.skill", "&eCrouch&7 to: &aSoup Throw");
        addMessage("Morphs.Villager.name", "&a&lVillager");
        addMessage("Morphs.Villager.skill", "&eLeft-Click&7 to: &aEmerald Throw");
        addMessage("Morphs.Witch.name", "&5&lWitch");
        addMessage("Morphs.Witch.skill", "&eLEft-Click&7 to: &aPotion Throw");
        addMessage("Morphs.PolarBear.name", "&f&lPolarBear");
        addMessage("Morphs.PolarBear.skill", "&eLeft-Click&7 to: &aBlizzard");
        addMessage("Morphs.Llama.name", "&3&lLlama");
        addMessage("Morphs.Llama.skill", "&eLeft-Click&7 to: &aSpit");
        addMessage("Morphs.Parrot.name", "&a&lParrot");
        addMessage("Morphs.Parrot.skill", "&eDouble Jump&7 to: &aFlap");

        // HATS
        addMessage("Hats.Equip", "%prefix% &9You equipped %hatname% &9hat!");
        addMessage("Hats.Unequip", "%prefix% &9You unequipped %hatname% &9hat!");
        addMessage("Hats.Astronaut.Name", "&7&lAstronaut");
        addMessage("Hats.Scared.Name", "&4&lScared");
        addMessage("Hats.Angel.Name", "&f&lAngel");
        addMessage("Hats.Embarassed.Name", "&7&lEmbarassed");
        addMessage("Hats.Kissy.Name", "&c&lKissy");
        addMessage("Hats.Sad.Name", "&9&lSad");
        addMessage("Hats.Cool.Name", "&6&lCool");
        addMessage("Hats.Surprised.Name", "&4&lSurprised");
        addMessage("Hats.Dead.Name", "&8&lDead");
        addMessage("Hats.Crying.Name", "&9&lCrying");
        addMessage("Hats.BigSmile.Name", "&6&lBigSmile");
        addMessage("Hats.Wink.Name", "&e&lWink");
        addMessage("Hats.Derp.Name", "&3&lDerp");
        addMessage("Hats.Smile.Name", "&e&lSmile");
        addMessage("Hats.Iron.Name", "&7&lIron");
        addMessage("Hats.Gold.Name", "&e&lGold");
        addMessage("Hats.Diamond.Name", "&b&lDiamond");
        addMessage("Hats.CommandBlock.Name", "&7&lCommand Block");
        addMessage("Hats.Music.Name", "&9&lMusic");
        addMessage("Hats.Squid.Name", "&3&lSquid");
        addMessage("Hats.Chicken.Name", "&f&lChicken");
        addMessage("Hats.Blaze.Name", "&6&lBlaze");
        addMessage("Hats.Piston.Name", "&7&lPiston");
        addMessage("Hats.Sheep.Name", "&f&lSheep");
        addMessage("Hats.Pig.Name", "&d&lPig");
        addMessage("Hats.Golem.Name", "&7&lGolem");
        addMessage("Hats.Enderman.Name", "&5&lEnderman");
        addMessage("Hats.Mario.Name", "&4&lMario");
        addMessage("Hats.Luigi.Name", "&2&lLuigi");
        addMessage("Hats.Batman.Name", "&8&lBatman");
        addMessage("Hats.Chest.Name", "&6&lChest");
        addMessage("Hats.Skull.Name", "&f&lSkull");
        addMessage("Hats.JackOLantern.Name", "&6&lJack-O'-Lantern");
        addMessage("Hats.Ghost.Name", "&f&lGhost");
        addMessage("Hats.ScaryClown.Name", "&4&lScary Clown");
        addMessage("Hats.Santa.Name", "&4&lSanta");
        addMessage("Hats.Snowman.Name", "&f&lSnowman");
        addMessage("Hats.Present.Name", "&c&lPresent");
        addMessage("Hats.Elf.Name", "&a&lElf");
        addMessage("Hats.Bedrock.Name", "&8&lBedrock");
        addMessage("Hats.Bread.Name", "&f&lBread");
        addMessage("Hats.Cheese.Name", "&e&lCheese");
        addMessage("Hats.Pancakes.Name", "&f&lPancakes");
        addMessage("Hats.Cake.Name", "&c&lCake");
        addMessage("Hats.Cookie.Name", "&6&lCookie");
        addMessage("Hats.CandyCane.Name", "&c&lCandy &f&lCane");
        addMessage("Hats.Chocolate.Name", "&a&lChocolate");
        addMessage("Hats.WhiteChocolate.Name", "&f&lWhite Chocolate");
        addMessage("Hats.Apple.Name", "&4&lApple");
        addMessage("Hats.Melon.Name", "&a&lMelon");
        addMessage("Hats.CarvedPumpkin.Name", "&6&lCarved Pumpkin");
        addMessage("Hats.Strawberry.Name", "&c&lStrawberry");
        addMessage("Hats.Coconut.Name", "&f&lCoconut");
        addMessage("Hats.Taco.Name", "&e&lTaco");
        addMessage("Hats.Bacon.Name", "&f&lBacon");
        addMessage("Hats.Fries.Name", "&6&lFries");
        addMessage("Hats.Hamburger.Name", "&6&lHamburger");
        addMessage("Hats.Popcorn.Name", "&6&lPopcorn");
        addMessage("Hats.WhiteDonut.Name", "&f&lWhite Donut");
        addMessage("Hats.PinkDonut.Name", "&d&lPink Donut");
        addMessage("Hats.ChocolateDonut.Name", "&a&lChocolate Donut");
        addMessage("Hats.Pie.Name", "&4&lPie");
        addMessage("Hats.A.Name", "&6&lA");
        addMessage("Hats.B.Name", "&4&lB");
        addMessage("Hats.C.Name", "&2&lC");
        addMessage("Hats.D.Name", "&b&lD");
        addMessage("Hats.E.Name", "&e&lE");
        addMessage("Hats.F.Name", "&5&lF");
        addMessage("Hats.G.Name", "&d&lG");
        addMessage("Hats.H.Name", "&a&lH");
        addMessage("Hats.I.Name", "&6&lI");
        addMessage("Hats.J.Name", "&4&lJ");
        addMessage("Hats.K.Name", "&2&lK");
        addMessage("Hats.L.Name", "&b&lL");
        addMessage("Hats.M.Name", "&e&lM");
        addMessage("Hats.N.Name", "&5&lN");
        addMessage("Hats.O.Name", "&d&lO");
        addMessage("Hats.P.Name", "&a&lP");
        addMessage("Hats.Q.Name", "&6&lQ");
        addMessage("Hats.R.Name", "&4&lR");
        addMessage("Hats.S.Name", "&2&lS");
        addMessage("Hats.T.Name", "&b&lT");
        addMessage("Hats.U.Name", "&e&lU");
        addMessage("Hats.V.Name", "&5&lV");
        addMessage("Hats.W.Name", "&d&lW");
        addMessage("Hats.X.Name", "&a&lX");
        addMessage("Hats.Y.Name", "&6&lY");
        addMessage("Hats.Z.Name", "&4&lZ");
        addMessage("Hats.Must-Remove-Hat", "%prefix% &c&lYou must remove your helmet to equip a hat!");

        // SUITS
        addMessage("Suits.Rave.whole-equip", "&f&lwhole &b&lR&a&la&e&lv&6&le &f&lsuit");
        addMessage("Suits.Rave.helmet-name", "&b&lR&a&la&e&lv&6&le &f&lHelmet");
        addMessage("Suits.Rave.chestplate-name", "&b&lR&a&la&e&lv&6&le &f&lChestplate");
        addMessage("Suits.Rave.leggings-name", "&b&lR&a&la&e&lv&6&le &f&lLeggings");
        addMessage("Suits.Rave.boots-name", "&b&lR&a&la&e&lv&6&le &f&lBoots");
        addMessage("Suits.Astronaut.whole-equip", "&f&lwhole &f&lAstronaut &f&lsuit");
        addMessage("Suits.Astronaut.helmet-name", "&f&lAstronaut &f&lHelmet");
        addMessage("Suits.Astronaut.chestplate-name", "&f&lAstronaut &f&lChestplate");
        addMessage("Suits.Astronaut.leggings-name", "&f&lAstronaut &f&lLeggings");
        addMessage("Suits.Astronaut.boots-name", "&f&lAstronaut &f&lBoots");
        addMessage("Suits.Diamond.whole-equip", "&f&lwhole &b&lDiamond &f&lsuit");
        addMessage("Suits.Diamond.helmet-name", "&b&lDiamond &f&lHelmet");
        addMessage("Suits.Diamond.chestplate-name", "&b&lDiamond &f&lChestplate");
        addMessage("Suits.Diamond.leggings-name", "&b&lDiamond &f&lLeggings");
        addMessage("Suits.Diamond.boots-name", "&b&lDiamond &f&lBoots");
        addMessage("Suits.Santa.whole-equip", "&f&lwhole &4&lSanta &f&lsuit");
        addMessage("Suits.Santa.helmet-name", "&4&lSanta &f&lHelmet");
        addMessage("Suits.Santa.chestplate-name", "&4&lSanta &f&lChestplate");
        addMessage("Suits.Santa.leggings-name", "&4&lSanta &f&lLeggings");
        addMessage("Suits.Santa.boots-name", "&4&lSanta &f&lBoots");
        addMessage("Suits.Frozen.whole-equip", "&f&lwhole &b&lFrozen &f&lsuit");
        addMessage("Suits.Frozen.helmet-name", "&b&lFrozen &f&lHelmet");
        addMessage("Suits.Frozen.chestplate-name", "&b&lFrozen &f&lChestplate");
        addMessage("Suits.Frozen.leggings-name", "&b&lFrozen &f&lLeggings");
        addMessage("Suits.Frozen.boots-name", "&b&lFrozen &f&lBoots");
        addMessage("Suits.Equip", "%prefix% &9You equipped %suitname%!");
        addMessage("Suits.Unequip", "%prefix% &9You unequipped %suitname%!");
        addMessage("Suits.Suit-Part-Lore", "&9Suits Part");
        addMessage("Suits.Whole-Equip-Lore", "&7&oEquip the whole suit!");
        addMessage("Suits.Must-Remove.HELMET", "%prefix% &c&lYou must remove your helmet to equip a suit helmet!");
        addMessage("Suits.Must-Remove.CHESTPLATE", "%prefix% &c&lYou must remove your chestplate to equip a suit chestplate!");
        addMessage("Suits.Must-Remove.LEGGINGS", "%prefix% &c&lYou must remove your leggings to equip suit leggings!");
        addMessage("Suits.Must-Remove.BOOTS", "%prefix% &c&lYou must remove your boots to equip suit boots!");

        // EMOTES
        addMessage("Emotes.Equip", "%prefix% &9You equipped %emotename%");
        addMessage("Emotes.Unequip", "%prefix% &9You unequipped %emotename%");
        addMessage("Emotes.Cry.Name", "&9&lCry");
        addMessage("Emotes.Angry.Name", "&c&lAngry");
        addMessage("Emotes.Happy.Name", "&e&lHappy");
        addMessage("Emotes.Cheeky.Name", "&6&lCheeky");
        addMessage("Emotes.Love.Name", "&4&lLove");
        addMessage("Emotes.DealWithIt.Name", "&7&lDeal With it");
        addMessage("Emotes.Cool.Name", "&9&lCool");
        addMessage("Emotes.Surprised.Name", "&6&lSurprised");
        addMessage("Emotes.Wink.Name", "&a&lWink");
        addMessage("Emotes.Must-Remove-Helmet", "%prefix% &c&lYou must remove your helmet to equip an emote!");

        // MENU
        addMessage("Menu.Gadgets.Button.Name", "&9&lGadgets");
        addMessage("Menu.Gadgets.Button.Tooltip-Equip", "&b&lActivate");
        addMessage("Menu.Gadgets.Button.Tooltip-Unequip", "&c&lDeactivate");

        addMessage("Menu.Particle-Effects.Button.Name", "&b&lParticle Effects");
        addMessage("Menu.Particle-Effects.Button.Tooltip-Equip", "&b&lSummon");
        addMessage("Menu.Particle-Effects.Button.Tooltip-Unequip", "&c&lUnsummon");

        addMessage("Menu.Mounts.Button.Name", "&6&lMounts");
        addMessage("Menu.Mounts.Button.Tooltip-Equip", "&b&lSpawn");
        addMessage("Menu.Mounts.Button.Tooltip-Unequip", "&c&lDespawn");

        addMessage("Menu.Pets.Button.Name", "&a&lPets");
        addMessage("Menu.Pets.Button.Tooltip-Equip", "&b&lSpawn");
        addMessage("Menu.Pets.Button.Tooltip-Unequip", "&c&lDespawn");

        addMessage("Menu.Morphs.Button.Name", "&2&lMorphs");
        addMessage("Menu.Morphs.Button.Tooltip-Equip", "&b&lMorph into");
        addMessage("Menu.Morphs.Button.Tooltip-Unequip", "&c&lUnmorph from");

        addMessage("Menu.Hats.Button.Name", "&b&lHats");
        addMessage("Menu.Hats.Button.Tooltip-Equip", "&b&lEquip");
        addMessage("Menu.Hats.Button.Tooltip-Unequip", "&c&lUnequip");

        addMessage("Menu.Suits.Button.Name", "&c&lSuits");
        addMessage("Menu.Suits.Button.Tooltip-Equip", "&b&lEquip");
        addMessage("Menu.Suits.Button.Tooltip-Unequip", "&c&lUnequip");

        addMessage("Menu.Emotes.Button.Name", "&e&lEmotes");
        addMessage("Menu.Emotes.Button.Tooltip-Equip", "&b&lEquip");
        addMessage("Menu.Emotes.Button.Tooltip-Unequip", "&c&lUnequip");

        addMessage("Menu.Main.Button.Name", "&c&lMain Menu");
        addMessage("Menu.Misc.Button.Previous-Page", "&c&lPrevious Page");
        addMessage("Menu.Misc.Button.Next-Page", "&a&lNext Page");

        // Misc messages
        addMessage("Enabled-SelfMorphView", "%prefix% &9you enabled self view for morphs!");
        addMessage("Disabled-SelfMorphView", "%prefix% &9you disabled self view for morphs!");
        addMessage("Enabled-Gadgets", "%prefix% &9you enabled gadgets!");
        addMessage("Disabled-Gadgets", "%prefix% &9you disabled gadgets!");
        addMessage("Not-Allowed-From-Console", "&c&lThis can't be executed from console!");
        addMessage("World-Disabled", "%prefix% &c&lCosmetics are disabled in this world!");
        addMessage("Region-Disabled", "%prefix% &c&lCosmetics are disabled in this area!");
        addMessage("Region-Disabled-Category", "%prefix% &c&l%category% cosmetics are disabled in this area!");
        addMessage("Disabled-Command-Message", "%prefix% &c&lYou can't use this command while cosmetics are equipped!");
        addMessage("Not-Allowed-In-Vanish", "%prefix% &c&lYou can't use cosmetics while in vanish!");
    }

    /**
     * Add a message in the messages.yml file.
     *
     * @param path    The config path.
     * @param message The config value.
     */
    public static void addMessage(String path, String message) {
        messagesConfig.addDefault(path, message);
    }

    public static void save() {
        messagesConfig.save();
    }

    /**
     * Gets a message.
     *
     * @param messagePath The path of the message in the config.
     * @return a message from a config path.
     */
    public static String getMessage(String messagePath) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(messagePath).replace("%prefix%", messagesConfig.getString("Prefix")));
    }

    private MessageManager() {
    }

    private static void upgradeCategoryStrings(ConfigurationSection menuBlock) {
        messagesConfig.set("Menu", null);
        Map<Category,Map<String,String>> buttons = new HashMap<>();
        Map<Category,String> menuNames = new HashMap<>();
        for (Category cat : Category.values()) {
            Map<String,String> catSection = new HashMap<>();
            catSection.put("Name", menuBlock.getString(cat.getConfigPath()));
            buttons.put(cat, catSection);
            menuNames.put(cat, messagesConfig.getString("Menus." + cat.getConfigPath()));
        }
        addButton(buttons, menuBlock, Category.PETS, "Spawn", "Despawn");
        addButton(buttons, menuBlock, Category.GADGETS, "Activate", "Deactivate");
        addButton(buttons, menuBlock, Category.EFFECTS, "Summon", "Unsummon");
        addButton(buttons, menuBlock, Category.MOUNTS, "Spawn", "Despawn");
        addButton(buttons, menuBlock, Category.MORPHS, "Morph", "Unmorph");
        addButton(buttons, menuBlock, Category.HATS, "Equip", "Unequip");
        addButton(buttons, menuBlock, Category.SUITS, "Equip", "Unequip");
        addButton(buttons, menuBlock, Category.EMOTES, "Equip", "Unequip");
        for (Entry<Category,Map<String,String>> catMap : buttons.entrySet()) {
            messagesConfig.set("Menu." + catMap.getKey().getConfigPath() + ".Title", menuNames.get(catMap.getKey()));
            for (Entry<String,String> translation : catMap.getValue().entrySet()) {
                messagesConfig.set("Menu." + catMap.getKey().getConfigPath() + ".Button." + translation.getKey(), translation.getValue());
            }
        }
        messagesConfig.set("Menu.Main.Button.Name", menuBlock.getString("Main-Menu"));
        migrateKey("Menus.Main-Menu", "Menu.Main.Title");
        migrateKey("Menus.Buy-Ammo", "Menu.Buy-Ammo.Title");
        migrateKey("Menus.Rename-Pet", "Menu.Purchase-Rename.Title");
        migrateKey("Rename-Pet-Purchase", "Menu.Purchase-Rename.Button.Showcase");
        migrateKey("Rename-Pet", "Menu.Rename-Pet.Button.Name");
        migrateKey("Rename-Pet-Placeholder", "Menu.Rename-Pet.Placeholder");
        migrateKey("Rename-Pet-Title", "Menu.Rename-Pet.Title");

        migrateMiscButton(menuBlock, "Previous-Page");
        migrateMiscButton(menuBlock, "Next-Page");
        messagesConfig.set("Menus", null);

        // Only categories that don't already use "Equip" path
        migrateActivateMsg(Category.PETS, "Spawn", "Despawn");
        migrateActivateMsg(Category.EFFECTS, "Summon", "Unsummon");
        migrateActivateMsg(Category.MOUNTS, "Spawn", "Despawn");
        migrateActivateMsg(Category.MORPHS, "Morph", "Unmorph");

        migrateClearMsg("Cosmetics", "Cosmetics");
        for (Category cat : Category.values()) {
            String configName = cat.getConfigName();
            migrateClearMsg(cat.getConfigPath(), configName.substring(0, configName.length() - 1));
        }
    }

    private static void addButton(Map<Category,Map<String,String>> buttons, ConfigurationSection menuBlock, Category cat, String oldEquipKey, String oldUnequipKey) {
        buttons.get(cat).put("Tooltip-Equip", menuBlock.getString(oldEquipKey));
        buttons.get(cat).put("Tooltip-Unequip", menuBlock.getString(oldUnequipKey));
    }

    private static void migrateMiscButton(ConfigurationSection section, String key) {
        messagesConfig.set("Menu.Misc.Button." + key, section.getString(key));
    }

    private static void migrateActivateMsg(Category cat, String oldEquipKey, String oldUnequipKey) {
        migrateKey(cat.getConfigPath() + "." + oldEquipKey, cat.getConfigPath() + ".Equip");
        migrateKey(cat.getConfigPath() + "." + oldUnequipKey, cat.getConfigPath() + ".Unequip");
    }

    private static void migrateClearMsg(String newKey, String oldKey) {
        migrateKey("Clear-" + oldKey, "Clear." + newKey);
    }

    private static void migrateKey(String oldKey, String newKey) {
        messagesConfig.set(newKey, messagesConfig.getString(oldKey));
        messagesConfig.set(oldKey, null);
    }

    public static void reload() {
        messagesConfig.reload();
    }
}
