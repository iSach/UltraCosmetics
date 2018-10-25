package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * Represents an instance of a party popper gadget summoned by a player.
 *
 * @author iSach
 * @since 12-16-2015
 */
public class GadgetPartyPopper extends Gadget {
	
	public GadgetPartyPopper(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, GadgetType.valueOf("partypopper"), ultraCosmetics);
		
		setAsynchronous(true);
	}
	
	@Override
	void onRightClick() {
		for (int i = 0; i < 30; i++) {
			Vector rand = new Vector(Math.random() - 0.5D,
			                         Math.random() - 0.5D, Math.random() - 0.5D);
			Particles.ITEM_CRACK.display(new Particles.ItemData(BlockUtils.getDyeByColor(MathUtils.randomByte(15)),
			                                                    MathUtils.randomByte(15)), getPlayer().getEyeLocation().getDirection().add(rand.multiply(0.2)).multiply(1.2),
			                             0.6f, getPlayer().getEyeLocation(), 128);
		}
		for (int i = 0; i < 3; i++)
			SoundUtil.playSound(getPlayer().getLocation(), Sounds.CHICKEN_EGG_POP, 1.0f, 1.0f);
	}
	
	@Override
	void onLeftClick() {
	}
	
	@Override
	public void onUpdate() {
	}
	
	@Override
	public void onClear() {
	}
}
