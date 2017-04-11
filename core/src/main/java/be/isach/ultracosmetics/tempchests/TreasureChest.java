//package be.isach.ultracosmetics.tempchests;
//
//import be.isach.ultracosmetics.UltraCosmetics;
//import be.isach.ultracosmetics.player.UltraPlayer;
//import org.apache.commons.lang.Validate;
//import org.bukkit.event.Listener;
//
//public class TreasureChest implements Listener {
//    private TreasurePlacer treasurePlacer;
//
//    public TreasureChest(UltraPlayer owner, final TreasureChestDesign design, UltraCosmetics ultraCosmetics) {
//        Validate.notNull(owner);
//        Validate.notNull(design);
//        Validate.notNull(ultraCosmetics);
//
//        this.treasurePlacer = new TreasurePlacer(new TreasureRandomizer(owner, owner.getBukkitPlayer().getLocation(), ultraCosmetics), owner, design, ultraCosmetics, this);
//        treasurePlacer.start();
//    }
//
//    public TreasurePlacer getTreasurePlacer() {
//    	return treasurePlacer;
//    }
//}
