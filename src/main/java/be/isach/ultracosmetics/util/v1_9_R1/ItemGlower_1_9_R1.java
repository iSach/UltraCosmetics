package be.isach.ultracosmetics.util.v1_9_R1;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Sacha on 6/03/16.
 */
public class ItemGlower_1_9_R1 {

    public static ItemStack glow(ItemStack item) {
        net.minecraft.server.v1_9_R1.ItemStack stack = org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item);
        net.minecraft.server.v1_9_R1.NBTTagCompound nbtTagCompound = null;
        if (!stack.hasTag()) {
            nbtTagCompound = new net.minecraft.server.v1_9_R1.NBTTagCompound();
            stack.setTag(nbtTagCompound);
        }
        if (nbtTagCompound == null) nbtTagCompound = stack.getTag();
        net.minecraft.server.v1_9_R1.NBTTagList enchantment = new net.minecraft.server.v1_9_R1.NBTTagList();
        nbtTagCompound.set("ench", enchantment);
        stack.setTag(nbtTagCompound);
        return org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asCraftMirror(stack);
    }
}
