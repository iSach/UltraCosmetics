package be.isach.ultracosmetics.v1_12_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_12_R1.pets.CustomEntityPet;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.Zombie;

/**
 * @author RadBuilder
 */
public class Pumpling extends EntityZombie implements IPetCustomEntity {

    private CustomEntityPet pet = null;

    public Pumpling(World world) {
        super(world);
    }

    public Pumpling(World world, CustomEntityPet pet) {
        super(world);
        this.pet = pet;
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    protected SoundEffect F() { // say
        if (isCustomEntity()) {
            a(SoundEffects.bM, 0.05f, 2f);
            return null;
        } else return super.F();
    }

    @Override
    protected SoundEffect d(DamageSource damageSource) { // Hurt
        if (isCustomEntity()) return null;
        else return super.d(damageSource);
    }

    @Override
    protected SoundEffect cf() { // Death
        if (isCustomEntity()) return null;
        else return super.cf();
    }

    @Override
    protected SoundEffect dm() { // Step
        if (isCustomEntity()) return null;
        else return super.dm();
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (isCustomEntity()) return;
        super.a(blockposition, block);
    }

    @Override
    public String getName() {
        return LocaleI18n.get("entity.Zombie.name");
    }

    @Override
    public void B_() {
        super.B_();
        if (!isCustomEntity()) return;
        fireTicks = 0;
        UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(getBukkitEntity());
        pet.getFollowTask().follow(pet.getPlayer());
        setInvisible(true);
        setBaby(true);
        setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
    }

    private boolean isCustomEntity() {
        return CustomEntities.customEntities.contains(this);
    }

}
