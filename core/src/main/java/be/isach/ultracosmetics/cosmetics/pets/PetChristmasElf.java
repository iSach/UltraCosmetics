package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.TexturedSkullFactory;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Represents an instance of a Christmas Elf pet summoned by a player.
 *
 * @author iSach
 * @since 11-29-2015
 */
public class PetChristmasElf extends Pet {
    private static final List<ItemStack> PRESENTS = Arrays.asList(
            getSkull("f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e"),
            getSkull("6b4cde16a4014de0a7651f6067f12695bb5fed6feaec1e9413ca4271e7c819"),
            getSkull("d08ce7deba56b726a832b61115ca163361359c30434f7d5e3c3faa6fe4052"),
            getSkull("928e692d86e224497915a39583dbe38edffd39cbba457cc95a7ac3ea25d445"),
            getSkull("1b6730de7e5b941efc6e8cbaf5755f9421a20de871759682cd888cc4a81282"),
            getSkull("1ac1163f54dcbb0e8e31ac675696f2409299c5abbf6c3fe73bf1cfe91422e1"),
            getSkull("6cef9aa14e884773eac134a4ee8972063f466de678363cf7b1a21a85b7"),
            getSkull("aa074845885202e17ed5c4be4103733121235c5440ae3a1c49fbd39317b04d"));

    public PetChristmasElf(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("christmaself"));
    }

    @Override
    public void onUpdate() {
        dropItem = PRESENTS.get(RANDOM.nextInt(PRESENTS.size()));
        super.onUpdate();
    }

    private static ItemStack getSkull(String url) {
        return TexturedSkullFactory.createSkull(url);
    }
}
