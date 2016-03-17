package be.isach.ultracosmetics.util.v1_8_R3;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Sacha on 6/03/16.
 */
public class ItemGlower_1_8_R3 {

    public static ItemStack glow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack asNMSCopy = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(item);
        net.minecraft.server.v1_8_R3.NBTTagCompound tagCompound = null;
        if (!asNMSCopy.hasTag()) {
            tagCompound = new net.minecraft.server.v1_8_R3.NBTTagCompound();
            asNMSCopy.setTag(tagCompound);
        }
        if (tagCompound == null) tagCompound = asNMSCopy.getTag();
        net.minecraft.server.v1_8_R3.NBTTagList enchantmen = new net.minecraft.server.v1_8_R3.NBTTagList();
        tagCompound.set("ench", enchantmen);
        asNMSCopy.setTag(tagCompound);
        return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asCraftMirror(asNMSCopy);
    }

}
