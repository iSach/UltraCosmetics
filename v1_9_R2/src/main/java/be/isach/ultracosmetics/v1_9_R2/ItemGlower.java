package be.isach.ultracosmetics.v1_9_R2;

import be.isach.ultracosmetics.version.IItemGlower;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sacha on 6/03/16.
 */
public class ItemGlower implements IItemGlower {

	public ItemStack glow(ItemStack item) {
		net.minecraft.server.v1_9_R2.ItemStack stack = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(item);
		net.minecraft.server.v1_9_R2.NBTTagCompound nbtTagCompound = null;
		if (!stack.hasTag()) {
			nbtTagCompound = new net.minecraft.server.v1_9_R2.NBTTagCompound();
			stack.setTag(nbtTagCompound);
		}
		if (nbtTagCompound == null) nbtTagCompound = stack.getTag();
		net.minecraft.server.v1_9_R2.NBTTagList enchantment = new net.minecraft.server.v1_9_R2.NBTTagList();
		nbtTagCompound.set("ench", enchantment);
		stack.setTag(nbtTagCompound);
		return org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asCraftMirror(stack);
	}
}
