package eu.pb4.illagerexpansion.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProvokerEntity extends SpellcasterIllager implements RangedAttackMob, PlayerPolymerEntity {
    @Nullable
    private Sheep wololoTarget;
    private int cooldown;
    private AttributeMap attributeContainer;

    public ProvokerEntity(EntityType<? extends ProvokerEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 10;
        this.onCreated(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ProvokerEntity.LookAtTargetOrWololoTarget());
        this.goalSelector.addGoal(3, new BuffAllyGoal());
        this.goalSelector.addGoal(4, new RangedBowAttackGoal<ProvokerEntity>(this, 0.5, 20, 15.0f));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createProvokerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 23.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        var bow = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        ItemStack itemStack = this.getProjectile(bow);
        AbstractArrow persistentProjectileEntity = ProjectileUtil.getMobArrow(this, itemStack, pullProgress, bow);
        double d = target.getX() - this.getX();
        double e = target.getY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.shoot(d, e + g * (double) 0.2f, f, 1.6f, 14 - this.level().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level().addFreshEntity(persistentProjectileEntity);
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundRegistry.PROVOKER_CELEBRATE;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        --cooldown;
        super.customServerAiStep(world);
    }

    @Override
    public boolean considersEntityAsAlly(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.considersEntityAsAlly(other)) {
            return true;
        }
        if (other instanceof Vex) {
            return this.isAlliedTo(((Vex) other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getType().is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
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

    @Nullable Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    void setWololoTarget(@Nullable Sheep sheep) {
        this.wololoTarget = sheep;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(ServerLevel world, int wave, boolean unused) {

    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }
        if (this.isAggressive()) {
            return AbstractIllager.IllagerArmPose.BOW_AND_ARROW;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        this.startSeenByPlayer(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.PROVOKER;
    }

    class LookAtTargetOrWololoTarget extends SpellcasterIllager.SpellcasterCastingSpellGoal {

        @Override
        public void tick() {
            if (ProvokerEntity.this.getTarget() != null) {
                ProvokerEntity.this.getLookControl().setLookAt(ProvokerEntity.this.getTarget(), ProvokerEntity.this.getMaxHeadYRot(), ProvokerEntity.this.getMaxHeadXRot());
            } else if (ProvokerEntity.this.getWololoTarget() != null) {
                ProvokerEntity.this.getLookControl().setLookAt(ProvokerEntity.this.getWololoTarget(), ProvokerEntity.this.getMaxHeadYRot(), ProvokerEntity.this.getMaxHeadXRot());
            }
        }
    }

    public class BuffAllyGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (ProvokerEntity.this.getTarget() == null) {
                return false;
            }
            return ProvokerEntity.this.cooldown < 0;
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(12), entity -> (entity instanceof AbstractIllager));
        }

        @Override
        public void stop() {
            super.stop();
        }

        private void buff(LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 120, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 0));
            double x = entity.getX();
            double y = entity.getY() + 1;
            double z = entity.getZ();
            ((ServerLevel) level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 10, 0.4D, 0.4D, 0.4D, 0.15D);

        }

        @Override
        protected void performSpellCasting() {
            ProvokerEntity.this.cooldown = 300;
            getTargets().forEach(this::buff);
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return ClassTinkerers.getEnum(IllagerSpell.class, "IE_PROVOKE");
        }
    }
}
