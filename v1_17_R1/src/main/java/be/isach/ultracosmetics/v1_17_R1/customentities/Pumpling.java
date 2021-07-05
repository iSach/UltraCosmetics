package be.isach.ultracosmetics.v1_17_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_17_R1.pets.CustomEntityPet;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.entity.Zombie;

/**
 * @author RadBuilder
 */
public class Pumpling extends EntityZombie implements IPetCustomEntity {

    private CustomEntityPet pet = null;

    public Pumpling(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
    }

    public Pumpling(EntityTypes<? extends EntityZombie> entitytypes, World world, CustomEntityPet pet) {
        super(entitytypes, world);
        this.pet = pet;
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    protected SoundEffect getSoundAmbient() { // say (ambient)
        if (isCustomEntity()) {
            playSound(SoundEffects.BLOCK_FIRE_AMBIENT, 0.05f, 2f);
            return null;
        } else return super.getSoundAmbient();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damageSource) { // Hurt
        if (isCustomEntity()) return null;
        else return super.getSoundHurt(damageSource);
    }

    @Override
    protected SoundEffect getSoundDeath() { // Death
        if (isCustomEntity()) return null;
        else return super.getSoundDeath();
    }

    @Override
    protected SoundEffect getSoundStep() { // Step
        if (isCustomEntity()) return null;
        else return super.getSoundStep();
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        if (isCustomEntity()) return;
        super.b(blockposition, iblockdata);
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.Zombie.name");
    }

    @Override
    public void tick() {
        super.tick();
        if (!isCustomEntity()) return;
        fireTicks = 0;
        UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
        UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(getBukkitEntity());
        pet.getFollowTask().follow(pet.getPlayer());
        setInvisible(true);
        setBaby(true);
        setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
    }

    private boolean isCustomEntity() {
        return CustomEntities.customEntities.contains(this);
    }

}
