package be.isach.ultracosmetics.v1_10_R1.customentities;

        import be.isach.ultracosmetics.UltraCosmetics;
        import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
        import be.isach.ultracosmetics.util.Particles;
        import be.isach.ultracosmetics.util.UtilParticles;
        import net.minecraft.server.v1_10_R1.*;
        import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
        import org.bukkit.entity.Zombie;
        import be.isach.ultracosmetics.util.ItemFactory;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by ataranlen on 6/26/2016.
 */
public class CompanionCube extends EntityZombie implements IPetCustomEntity {

    public CompanionCube(World world) {
        super(world);

        final CompanionCube instance = this;
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    protected SoundEffect G() { // say
        if (isCustomEntity()) {
            a(SoundEffects.bM, 0.05f, 2f);
            return null;
        } else return super.G();
    }

    @Override
    protected SoundEffect bV() { // Hurt
        if (isCustomEntity()) return null;
        else return super.bV();
    }

    @Override
    protected SoundEffect bW() { // Death
        if (isCustomEntity()) return null;
        else return super.bW();
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (isCustomEntity()) return;
        super.a(blockposition, block);
    }

    @Override
    public void m() {
        super.m();
        if (!isCustomEntity()) return;
        fireTicks = 0;
        UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
        UltraCosmetics.getInstance().getPathfinderUtil().removePathFinders(getBukkitEntity());
        setInvisible(true);
        setBaby(true);
        setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE3NWJkZjQ3YWVhMWE0YmYxZDM0OWJlNmI3ZmE0YWIzN2Y0Nzk2NzJmNGM0M2FjYTU3NTExYjQyN2FiNCJ9fX0=", "§8§oHat")));

    }

    private boolean isCustomEntity() {
        return CustomEntities.customEntities.contains(this);
    }

}
