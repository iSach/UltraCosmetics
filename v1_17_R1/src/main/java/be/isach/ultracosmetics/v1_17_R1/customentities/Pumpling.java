package be.isach.ultracosmetics.v1_17_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.v1_17_R1.pets.CustomEntityPet;

import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author RadBuilder
 */
public class Pumpling extends Zombie {

    private CustomEntityPet pet = null;

    public Pumpling(EntityType<? extends Zombie> entitytypes, Level world) {
        super(entitytypes, world);
    }

    public Pumpling(EntityType<? extends Zombie> entitytypes, Level world, CustomEntityPet pet) {
        super(entitytypes, world);
        this.pet = pet;
    }

    @Override
    protected SoundEvent getAmbientSound() { // say (ambient)
        if (isCustomEntity()) {
            playSound(SoundEvents.FIRE_AMBIENT, 0.05f, 2f);
            return null;
        } else return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { // Hurt
        if (isCustomEntity()) return null;
        else return super.getHurtSound(damageSource);
    }

    @Override
    protected SoundEvent getDeathSound() { // Death
        if (isCustomEntity()) return null;
        else return super.getDeathSound();
    }

    @Override
    protected SoundEvent getStepSound() { // Step
        if (isCustomEntity()) return null;
        else return super.getStepSound();
    }

    @Override
    protected void playStepSound(BlockPos blockposition, BlockState iblockdata) {
        if (isCustomEntity()) return;
        super.playStepSound(blockposition, iblockdata);
    }

    @Override
    public TextComponent getName() {
        return new TextComponent(Language.getInstance().getOrDefault("entity.minecraft.zombie"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!isCustomEntity()) return;
        ((Entity)this).remainingFireTicks = 0;
        Particles.FLAME.display(0.2f, 0.2f, 0.2f, ((org.bukkit.entity.Zombie) getBukkitEntity()).getEyeLocation(), 3);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(getBukkitEntity());
        pet.getFollowTask().follow(pet.getPlayer());
        ((Entity)this).setInvisible(true);
        ((Zombie)this).setBaby(true);
        ((Mob)this).setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
    }

    private boolean isCustomEntity() {
        return CustomEntities.isCustomEntity(this);
    }

}
