package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.XMaterial;

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
    public TreasureChestDesign(String path) {
        center = getMaterialData(path + ".center-block");
        blocks2 = getMaterialData(path + ".around-center");
        blocks3 = getMaterialData(path + ".third-blocks");
        belowChests = getMaterialData(path + ".below-chests");
        barriers = getMaterialData(path + ".barriers");
        String chestType = UltraCosmeticsData.get().getPlugin().getConfig().getString("TreasureChests.Designs." + path + ".chest-type");
        String effect = UltraCosmeticsData.get().getPlugin().getConfig().getString("TreasureChests.Designs." + path + ".effect");
        try {
            this.chestType = ChestType.valueOf(chestType.toUpperCase());
        } catch (IllegalArgumentException exc) {
            this.chestType = ChestType.NORMAL;
        }
        try {
            this.effect = Particles.valueOf(effect);
        } catch (IllegalArgumentException exc) {
            this.effect = Particles.FLAME;
        }
    }

    @SuppressWarnings("deprecation")
    private MaterialData getMaterialData(String s) {
        XMaterial mat = ItemFactory.getXMaterialFromConfig("TreasureChests.Designs." + s);
        return new MaterialData(mat.parseMaterial(), mat.getData());
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
