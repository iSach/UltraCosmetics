package be.isach.ultracosmetics.v1_11_R1;

import be.isach.ultracosmetics.version.IItemGlower;
import org.bukkit.inventory.ItemStack;

/**
 * @author RadBuilder
 */
public class ItemGlower implements IItemGlower{

    public ItemStack glow(ItemStack item) {
        net.minecraft.server.v1_11_R1.ItemStack stack = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(item);
        net.minecraft.server.v1_11_R1.NBTTagCompound nbtTagCompound = null;
        if (!stack.hasTag()) {
            nbtTagCompound = new net.minecraft.server.v1_11_R1.NBTTagCompound();
            stack.setTag(nbtTagCompound);
        }
        if (nbtTagCompound == null) nbtTagCompound = stack.getTag();
        net.minecraft.server.v1_11_R1.NBTTagList enchantment = new net.minecraft.server.v1_11_R1.NBTTagList();
        nbtTagCompound.set("ench", enchantment);
        stack.setTag(nbtTagCompound);
        return org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asCraftMirror(stack);
    }
}
