package be.isach.ultracosmetics.v1_9_R2.nms;

import net.minecraft.server.v1_9_R2.EntityHuman;

public class WrapperEntityHuman extends WrapperEntityLiving {
	
	protected EntityHuman handle;
	
	public WrapperEntityHuman(EntityHuman handle) {
		super(handle);
		
		this.handle = handle;
	}
	
	@Override
	public EntityHuman getHandle() { return handle; }
	
}
