package be.isach.ultracosmetics.v1_13_R1.nms;

import net.minecraft.server.v1_13_R1.Entity;

/**
 * @author RadBuilder
 */
public class WrapperEntity extends WrapperBase {
	
	protected Entity handle;
	
	public WrapperEntity(Entity handle) {
		super(handle);
		
		this.handle = handle;
	}
	
	public float getStepHeight() { return handle.P; }
	
	public void setStepHeight(float stepHeight) { handle.P = stepHeight; }
	
	public boolean canPassengerSteer() { return handle.bI(); }
	
	public Entity getHandle() { return handle; }
	
}
