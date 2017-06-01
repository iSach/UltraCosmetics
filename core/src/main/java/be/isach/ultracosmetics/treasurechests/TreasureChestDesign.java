package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.Particles;
import org.bukkit.material.MaterialData;

/**
 * Created by Sacha on 11/11/15.
 */
public class TreasureChestDesign {

    private MaterialData center;
    private MaterialData blocks2;
    private MaterialData blocks3;
    private MaterialData belowChests;
    private MaterialData barriers;
    private ChestType chestType;
    private Particles effect;
    private String path;

    public TreasureChestDesign(String path) {
        this.path = path;
        String center = g(path + ".center-block"),
                blocks2 = g(path + ".around-center"),
                blocks3 = g(path + ".third-blocks"),
                belowChest = g(path + ".below-chests"),
                barriers = g(path + ".barriers"),
                chestType = g(path + ".chest-type"),
                effect = g(path + ".effect");
        this.center = initMData(center);
        this.blocks2 = initMData(blocks2);
        this.blocks3 = initMData(blocks3);
        this.belowChests = initMData(belowChest);
        this.barriers = initMData(barriers);
        try {
            this.chestType = ChestType.valueOf(chestType.toUpperCase());
        } catch (Exception exc) {
            this.chestType = ChestType.NORMAL;
        }
        try {
            this.effect = Particles.valueOf(effect);
        } catch (Exception exc) {
            this.effect = Particles.FLAME;
        }
    }

    private MaterialData initMData(String name) {
        return new MaterialData(Integer.parseInt(name.split(":")[0]),
                (name.split(":").length > 1 ? (byte) Integer.parseInt(name.split(":")[1]) : (byte) 0));
    }

    private String g(String s) {
        return UltraCosmeticsData.get().getPlugin().getConfig().getString("TreasureChests.Designs." + s);
    }

    public ChestType getChestType() {
        return chestType;
    }

    public MaterialData getCenter() {
        return center;
    }

    public MaterialData getBlocks2() {
        return blocks2;
    }

    public MaterialData getBlocks3() {
        return blocks3;
    }

    public MaterialData getBarriers() {
        return barriers;
    }

    public MaterialData getBelowChests() {
        return belowChests;
    }

    public Particles getEffect() {
        return effect;
    }
}
