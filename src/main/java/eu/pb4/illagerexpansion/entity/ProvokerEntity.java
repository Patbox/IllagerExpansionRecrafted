package eu.pb4.illagerexpansion.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProvokerEntity extends SpellcastingIllagerEntity implements RangedAttackMob, PlayerPolymerEntity {
    @Nullable
    private SheepEntity wololoTarget;
    private int cooldown;
    private AttributeContainer attributeContainer;

    public ProvokerEntity(EntityType<? extends ProvokerEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.onCreated(this);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ProvokerEntity.LookAtTargetOrWololoTarget());
        this.goalSelector.add(3, new BuffAllyGoal());
        this.goalSelector.add(4, new BowAttackGoal<ProvokerEntity>(this, 0.5, 20, 15.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, false));
    }

    public static DefaultAttributeContainer.Builder createProvokerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 23.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.38D);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        var bow = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack itemStack = this.getProjectileType(bow);
        PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, itemStack, pullProgress, bow);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * (double) 0.2f, f, 1.6f, 14 - this.getWorld().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundRegistry.PROVOKER_CELEBRATE;
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
    }

    @Override
    protected void mobTick(ServerWorld world) {
        --cooldown;
        super.mobTick(world);
    }

    @Override
    public boolean isInSameTeam(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isInSameTeam(other)) {
            return true;
        }
        if (other instanceof VexEntity) {
            return this.isTeammate(((VexEntity) other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getType().isIn(EntityTypeTags.ILLAGER)) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.PROVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PROVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.PROVOKER_HURT;
    }

    @Nullable SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    void setWololoTarget(@Nullable SheepEntity sheep) {
        this.wololoTarget = sheep;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {

    }

    @Override
    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return IllagerEntity.State.SPELLCASTING;
        }
        if (this.isAttacking()) {
            return IllagerEntity.State.BOW_AND_ARROW;
        }
        return IllagerEntity.State.CROSSED;
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.PROVOKER;
    }

    class LookAtTargetOrWololoTarget extends SpellcastingIllagerEntity.LookAtTargetGoal {

        @Override
        public void tick() {
            if (ProvokerEntity.this.getTarget() != null) {
                ProvokerEntity.this.getLookControl().lookAt(ProvokerEntity.this.getTarget(), ProvokerEntity.this.getMaxHeadRotation(), ProvokerEntity.this.getMaxLookPitchChange());
            } else if (ProvokerEntity.this.getWololoTarget() != null) {
                ProvokerEntity.this.getLookControl().lookAt(ProvokerEntity.this.getWololoTarget(), ProvokerEntity.this.getMaxHeadRotation(), ProvokerEntity.this.getMaxLookPitchChange());
            }
        }
    }

    public class BuffAllyGoal extends SpellcastingIllagerEntity.CastSpellGoal {

        @Override
        public boolean canStart() {
            if (ProvokerEntity.this.getTarget() == null) {
                return false;
            }
            return ProvokerEntity.this.cooldown < 0;
        }

        private List<LivingEntity> getTargets() {
            return getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(12), entity -> (entity instanceof IllagerEntity));
        }

        @Override
        public void stop() {
            super.stop();
        }

        private void buff(LivingEntity entity) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120, 0));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 120, 0));
            double x = entity.getX();
            double y = entity.getY() + 1;
            double z = entity.getZ();
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 10, 0.4D, 0.4D, 0.4D, 0.15D);

        }

        @Override
        protected void castSpell() {
            ProvokerEntity.this.cooldown = 300;
            getTargets().forEach(this::buff);
        }

        @Override
        protected int getInitialCooldown() {
            return 40;
        }

        @Override
        protected int getSpellTicks() {
            return 60;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return ClassTinkerers.getEnum(Spell.class, "IE_PROVOKE");
        }
    }
}
