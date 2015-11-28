package be.isach.ultracosmetics.config;


/**
 * Created by sacha on 03/08/15.
 */
public class MessageManager {
    private static SettingsManager settingsManager;

    /**
     * Set up the messages in the config.
     */
    public MessageManager() {
        this.settingsManager = SettingsManager.getMessages();
        loadMessages();
    }

    /**
     * Set up the messages in the config.
     */
    private void loadMessages() {
        addMessage("Prefix", "&l&oCosmetics >&r");
        addMessage("No-Permission", "%prefix% &c&lYou don't have the permission!");
        addMessage("Cosmetic-Disabled", "%prefix% &c&lThis cosmetic is disabled!");
        addMessage("Invalid-Gadget", "%prefix% &c&lInvalid Gadget!");
        addMessage("Invalid-Pet", "%prefix% &c&lInvalid Pet!");
        addMessage("Invalid-Mount", "%prefix% &c&lInvalid Mount!");
        addMessage("Invalid-Effect", "%prefix% &c&lInvalid Effect!");
        addMessage("Invalid-Morph", "%prefix% &c&lInvalid Morph!");
        addMessage("Invalid-Hat", "%prefix% &c&lInvalid Hat!");
        addMessage("Invalid-Menu", "%prefix% &c&lInvalid Menu!");
        addMessage("Ammo", "&7Your ammo: &e%ammo%");
        addMessage("Purchase", "&a&lPURCHASE");
        addMessage("Cancel", "&c&lCANCEL");
        addMessage("Buy-Ammo-Description", "&e&lBuy %amount% %gadgetname% ammo &e&lfor %price%$");
        addMessage("Right-Click-Buy-Ammo", "&7Right Click to Buy Ammo");
        addMessage("Not-Enough-Money", "%prefix% &c&lYou don't have enough money!");
        addMessage("Successful-Purchase", "%prefix% &a&lSuccessful purchase!");
        addMessage("Buy-Treasure-Key", "&lBuy a Treasure Key");
        addMessage("Treasure-Keys", "§7§lTreasure Keys");
        addMessage("Treasure-Chests", "§6§lTreasure Chests");
        addMessage("Dont-Have-Key", "§4§lYou need a key!");
        addMessage("Click-Open-Chest", "§aClick to open a chest");
        addMessage("Click-Buy-Key", "§aClick to buy a key");
        addMessage("Your-Keys", "§7§oYour keys: §f§l%keys%");
        addMessage("Buy-Treasure-Key-ItemName", "&e&l1 &7&lTreasure Key &e&lfor %price%$");
        addMessage("Found-Legendary", "%prefix% &c&l%name% found Legendary %found%");
        addMessage("Chest-Not-Enough-Space", "%prefix% &c&lThere isn't enough space for a treasure chest!");
        addMessage("Too-Close-To-Other-Chest", "%prefix% &c&lYou are too close to another treasure chest!");
        addMessage("You-Won-Treasure-Chests", "%prefix% &f&lYou won: %name%!");
        addMessage("Clear-Cosmetics", "§c§lClear cosmetics");
        addMessage("Clear-Gadget", "§c§lClear current gadget");
        addMessage("Clear-Pet", "§c§lClear current pet");
        addMessage("Clear-Mount", "§c§lClear current mount");
        addMessage("Clear-Effect", "§c§lClear current effect");
        addMessage("Clear-Morph", "§c§lClear current morph");
        addMessage("Clear-Hat", "§c§lClear current hat");
        addMessage("Rename-Pet-Purchase", "§c§lRename the pet to &f&l%name% &c&lfor &e&l%price%$");

        addMessage("Active-Pet-Needed", "§c§lYou need to spawn a pet to rename it");
        addMessage("Rename-Pet", "§c§lClick to rename: %petname%");

        addMessage("Treasure-Chests-Loot.Gadget", "%ammo% %name% ammo");
        addMessage("Treasure-Chests-Loot.Pet", "%pet% pet");
        addMessage("Treasure-Chests-Loot.Mount", "%mount% mount");
        addMessage("Treasure-Chests-Loot.Effect", "%effect% effect");
        addMessage("Treasure-Chests-Loot.Morph", "%morph% morph");
        addMessage("Treasure-Chests-Loot.Hat", "%hat% hat");
        addMessage("Treasure-Chests-Loot.Money", "&e&l%money%$");

        //Menus
        addMessage("Menus.Main-Menu", "&lMain Menu");
        addMessage("Menus.Pets", "&lPets");
        addMessage("Menus.Gadgets", "&lGadgets");
        addMessage("Menus.Mounts", "&lMounts");
        addMessage("Menus.Morphs", "&lMorphs");
        addMessage("Menus.Hats", "&lHats");
        addMessage("Menus.Particle-Effects", "&lParticle Effects");
        addMessage("Menus.Buy-Ammo", "&lBuy Ammo");
        addMessage("Menus.Rename-Pet", "&lRename Pet");
        addMessage("Disable-Gadgets", "&c&lDisable Gadgets");
        addMessage("Enable-Gadgets", "&a&lEnable Gadgets");
        addMessage("Disable-Third-Person-View", "&c&lDisable Morphs Third Person View");
        addMessage("Enable-Third-Person-View", "&a&lEnable Morphs Third Person View");
        addMessage("Gadgets-Enabled-Needed", "%prefix% &c&lYou need to enable gadgets!");

        // Gadgets
        addMessage("Gadgets.Equip", "%prefix% &9You equipped %gadgetname%");
        addMessage("Gadgets.Unequip", "%prefix% &9You unequipped %gadgetname%");
        addMessage("Gadgets.Countdown-Message", "%prefix% &c&lYou can't use %gadgetname% &c&lfor %time%s!");
        addMessage("Gadgets.PaintballGun.name", "&b&lPaintball Gun");
        addMessage("Gadgets.BatBlaster.name", "&7&lBat Blaster");
        addMessage("Gadgets.MelonThrower.name", "&a&lMelon Thrower");
        addMessage("Gadgets.EtherealPearl.name", "&5&lEthereal Pearl");
        addMessage("Gadgets.FleshHook.name", "&7&lFlesh Hook");
        addMessage("Gadgets.DiscoBall.name", "&d&lDisco Ball");
        addMessage("Gadgets.DiscoBall.Already-Active", "&c&lThere is already a disco ball active!");
        addMessage("Gadgets.DiscoBall.Not-Space-Above", "&c&lThere is not enough space above you!");
        addMessage("Gadgets.ColorBomb.name", "&d&lColor Bomb");
        addMessage("Gadgets.Chickenator.name", "&f&lChickenator");
        addMessage("Gadgets.PortalGun.name", "&c&lPortal &9&lGun");
        addMessage("Gadgets.PortalGun.No-Block-Range", "%prefix% &c&lNo blocks in range!");
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
        addMessage("Gadgets.BlackHole.name", "&1&lBlack Hole");
        addMessage("Gadgets.TNT.name", "&4&lT&f&lN&4&lT");
        addMessage("Gadgets.FunGun.name", "&6&lFun Gun");
        addMessage("Gadgets.Parachute.name", "&f&lParachute");
        addMessage("Gadgets.QuakeGun.name", "&5§lQuake &d&lGun");
        addMessage("Gadgets.GhostParty.name", "&f&lGhost Party");
        addMessage("Gadgets.Firework.name", "&c&lFirework");

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
        addMessage("Mounts.Spawn", "%prefix% &9You spawned %mountname%");
        addMessage("Mounts.Despawn", "%prefix% &9You despawned %mountname%");

        // GADGETS
        addMessage("Particle-Effects.Summon", "%prefix% &9You summoned %effectname%");
        addMessage("Particle-Effects.Unsummon", "%prefix% &9You unsummoned %effectname%");
        addMessage("Particle-Effects.RainCloud.name", "&9&lRain Cloud");
        addMessage("Particle-Effects.SnowCloud.name", "&f&lSnow Cloud");
        addMessage("Particle-Effects.BloodHelix.name", "&4&lBlood Helix");
        addMessage("Particle-Effects.FrostLord.name", "&b&lFrost Lord");
        addMessage("Particle-Effects.FlameRings.name", "&c&lFlame Rings");
        // addMessage("Particle-Effects.AngelWings.name", "&f&lAngel Wings");
        addMessage("Particle-Effects.GreenSparks.name", "&a&lGreen Sparks");
        addMessage("Particle-Effects.InLove.name", "&c&lIn Love");
        addMessage("Particle-Effects.FrozenWalk.name", "&b&lFrozen Walk");
        addMessage("Particle-Effects.Enchanted.name", "&7&lEnchanted");
        addMessage("Particle-Effects.Music.name", "&9&lMusic");
        addMessage("Particle-Effects.Inferno.name", "&4&lInferno");
        addMessage("Particle-Effects.AngelWings.name", "&f&lAngel Wings");
        addMessage("Particle-Effects.SuperHero.name", "&4&lSuper Hero");

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
        addMessage("Pets.Spawn", "%prefix% &9You spawned %petname%");
        addMessage("Pets.Despawn", "%prefix% &9You despawned %petname%");

        addMessage("Morphs.Blaze.name", "&6&lBlaze");
        addMessage("Morphs.Blaze.skill", "&eCrouch§7 to: &aFly");
        addMessage("Morphs.Chicken.name", "&f&lChicken");
        addMessage("Morphs.Chicken.skill", "&eCrouch§7 to: &aLay eggs");
        addMessage("Morphs.Slime.name", "&a&lSlime");
        addMessage("Morphs.Slime.skill", "&eCrouch§7 to: &aBounce");
        addMessage("Morphs.Pig.name", "&d&lPig");
        addMessage("Morphs.Pig.skill", "&eCollide§7 to: &aBounce Players");
        addMessage("Morphs.Enderman.name", "&5&lEnderman");
        addMessage("Morphs.Enderman.skill", "&eDouble Jump§7 to: &aTeleport");
        addMessage("Morphs.Bat.name", "&8&lBat");
        addMessage("Morphs.Bat.skill", "&eDouble Jump§7 to: &aFlap");
        addMessage("Morphs.Creeper.name", "&a&lCreeper");
        addMessage("Morphs.Creeper.skill", "&eCrouch§7 to: &aDetonate");
        addMessage("Morphs.Creeper.charging", "&a&lCharging: %chargelevel%/100");
        addMessage("Morphs.Creeper.release-to-explode", "&f&lRelease to explode!");
        addMessage("Morphs.Morph", "%prefix% &9You morphed into %morphname%");
        addMessage("Morphs.Unmorph", "%prefix% &9You unmorphed from %morphname%");
        addMessage("Morphs.WitherSkeleton.name", "&8&lWither Skeleton");
        addMessage("Morphs.WitherSkeleton.skill", "&eSneak§7 to: &aBone Bomb");

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
        addMessage("Hats.Must-Remove-Hat", "%prefix% &c&lYou must remove your helmet to equip a hat!");


        addMessage("Menu.Gadgets", "&9&lGadgets");
        addMessage("Menu.Particle-Effects", "&b&lParticle Effects");
        addMessage("Menu.Mounts", "&6&lMounts");
        addMessage("Menu.Pets", "&a&lPets");
        addMessage("Menu.Morphs", "&2&lMorphs");
        addMessage("Menu.Hats", "&b&lHats");
        addMessage("Menu.Main-Menu", "&c&lMain Menu");
        addMessage("Menu.Activate", "&b&lActivate");
        addMessage("Menu.Deactivate", "&c&lDeactivate");
        addMessage("Menu.Spawn", "&b&lSpawn");
        addMessage("Menu.Despawn", "&c&lDespawn");
        addMessage("Menu.Summon", "&b&lSummon");
        addMessage("Menu.Unsummon", "&c&lUnsummon");
        addMessage("Menu.Equip", "&b&lEquip");
        addMessage("Menu.Unequip", "&c&lUnequip");
        addMessage("Menu.Morph", "&b&lMorph into");
        addMessage("Menu.Unmorph", "&c&lUnmorph from");
        addMessage("Menu.Previous-Page", "&c&lPrevious Page");
        addMessage("Menu.Next-Page", "&a&lNext Page");

        addMessage("Enabled-SelfMorphView", "%prefix% &9you enabled self view for morphs!");
        addMessage("Disabled-SelfMorphView", "%prefix% &9you disabled self view for morphs!");
        addMessage("Enabled-Gadgets", "%prefix% &9you enabled gadgets!");
        addMessage("Disabled-Gadgets", "%prefix% &9you disabled gadgets!");
    }

    /**
     * Add a message in the messages.yml file.
     *
     * @param path    The config path.
     * @param message The config value.
     */
    public static void addMessage(String path, String message) {
        settingsManager.addDefault(path, message);
    }

    /**
     * Gets a message.
     *
     * @param path The path of the message in the config.
     * @return a message from a config path.
     */
    public static String getMessage(String path) {
        return ((String) settingsManager.get(path)).replace("%prefix%", ((String) settingsManager.get("Prefix"))).replace("&", "§");
    }

}
