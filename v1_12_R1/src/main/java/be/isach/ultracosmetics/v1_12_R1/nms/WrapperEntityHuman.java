package be.isach.ultracosmetics.v1_12_R1.nms;

import net.minecraft.server.v1_12_R1.EntityHuman;

/**
 * @author RadBuilder
 */
public class WrapperEntityHuman extends WrapperEntityLiving {
	
	protected EntityHuman handle;
	
	public WrapperEntityHuman(EntityHuman handle) {
		super(handle);
		
		this.handle = handle;
	}
	
	@Override
	public EntityHuman getHandle() { return handle; }
	
}
