package eu.pb4.illagerexpansion.entity;

import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.EnumSet;
import java.util.List;

public class SurrenderedEntity extends Skeleton implements PolymerEntity {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(SurrenderedEntity.class, EntityDataSerializers.BYTE);
    private static final int CHARGING_FLAG = 1;
    @Nullable Mob owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;
    private AttributeMap attributeContainer;


    public SurrenderedEntity(EntityType<? extends SurrenderedEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new SurrenderedEntity.VexMoveControl(this);
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        super.move(movementType, movement);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.hurt(this.damageSources().starve(), 1.0f);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SurrenderedEntity.ChargeTargetGoal());
        this.goalSelector.addGoal(8, new SurrenderedEntity.LookAtTargetGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new SurrenderedEntity.TrackOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createSurrenderedAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.getIntOr("BoundX", Integer.MAX_VALUE) != Integer.MAX_VALUE) {
            this.bounds = new BlockPos(nbt.getIntOr("BoundX", 0), nbt.getIntOr("BoundY", 0), nbt.getIntOr("BoundZ", 0));
        }

        nbt.getInt("LifeTicks").ifPresent(this::setLifeTicks);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }
        if (this.alive) {
            nbt.putInt("LifeTicks", this.lifeTicks);
        }
    }

    @Nullable
    public Mob getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable Mob owner) {
        this.owner = owner;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable BlockPos pos) {
        this.bounds = pos;
    }

    private boolean areFlagsSet(int mask) {
        byte i = this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.entityData.get(VEX_FLAGS);
        i = value ? (i |= mask) : (i &= ~mask);
        this.entityData.set(VEX_FLAGS, (byte) (i & 0xFF));
    }

    public boolean isCharging() {
        return this.areFlagsSet(CHARGING_FLAG);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(CHARGING_FLAG, charging);
    }

    public void setLifeTicks(int lifeTicks) {
        this.alive = true;
        this.lifeTicks = lifeTicks;
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!super.doHurtTarget(world, target)) {
            return false;
        }
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), this);
        }
        return true;
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide()) {
            for (int i = 0; i < 2; ++i) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.WHITE_ASH, this.getX(), this.getY() + 1.2, this.getZ(), 2, 0.2D, 0D, 0.2D, 0.025D);
            }
        }
        super.aiStep();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SURRENDERED_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SURRENDERED_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.SURRENDERED_HURT;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        var found = false;
        for (int i = 0; i < data.size(); i++) {
            var e = data.get(i);

            if (e.id() == Entity.DATA_SHARED_FLAGS_ID.id()) {
                data.set(i, SynchedEntityData.DataValue.create(Entity.DATA_SHARED_FLAGS_ID, (byte) (((byte) e.value()) | 0x1 << 5)));
                found = true;
            }
        }

        if (!found && initial) {
            data.add(SynchedEntityData.DataValue.create(Entity.DATA_SHARED_FLAGS_ID, (byte) (((byte) 0x1 << 5))));
        }
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayer player) {
        return List.of(new Pair<>(EquipmentSlot.HEAD, Items.SKELETON_SKULL.getDefaultInstance()), new Pair<>(EquipmentSlot.CHEST, Items.CHAINMAIL_CHESTPLATE.getDefaultInstance()));
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.STRAY;
    }

    class VexMoveControl extends MoveControl {
        public VexMoveControl(SurrenderedEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 vec3d = new Vec3(this.wantedX - SurrenderedEntity.this.getX(), this.wantedY - SurrenderedEntity.this.getY(), this.wantedZ - SurrenderedEntity.this.getZ());
            double d = vec3d.length();
            if (d < SurrenderedEntity.this.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                SurrenderedEntity.this.setDeltaMovement(SurrenderedEntity.this.getDeltaMovement().scale(0.5));
            } else {
                SurrenderedEntity.this.setDeltaMovement(SurrenderedEntity.this.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05 / d)));
                if (SurrenderedEntity.this.getTarget() == null) {
                    Vec3 vec3d2 = SurrenderedEntity.this.getDeltaMovement();
                    SurrenderedEntity.this.setYRot(-((float) Mth.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                    SurrenderedEntity.this.yBodyRot = SurrenderedEntity.this.getYRot();
                } else {
                    double e = SurrenderedEntity.this.getTarget().getX() - SurrenderedEntity.this.getX();
                    double f = SurrenderedEntity.this.getTarget().getZ() - SurrenderedEntity.this.getZ();
                    SurrenderedEntity.this.setYRot(-((float) Mth.atan2(e, f)) * 57.295776f);
                    SurrenderedEntity.this.yBodyRot = SurrenderedEntity.this.getYRot();
                }
            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (SurrenderedEntity.this.getTarget() != null && !SurrenderedEntity.this.getMoveControl().hasWanted() && SurrenderedEntity.this.random.nextInt(SurrenderedEntity.ChargeTargetGoal.reducedTickDelay(7)) == 0) {
                return SurrenderedEntity.this.distanceToSqr(SurrenderedEntity.this.getTarget()) > 4.0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return SurrenderedEntity.this.getMoveControl().hasWanted() && SurrenderedEntity.this.isCharging() && SurrenderedEntity.this.getTarget() != null && SurrenderedEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = SurrenderedEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3 vec3d = livingEntity.getEyePosition();
                SurrenderedEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
            SurrenderedEntity.this.setCharging(true);
            SurrenderedEntity.this.playSound(SoundRegistry.SURRENDERED_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            SurrenderedEntity.this.setCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = SurrenderedEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (SurrenderedEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                SurrenderedEntity.this.doHurtTarget((ServerLevel) livingEntity.level(), livingEntity);
                SurrenderedEntity.this.setCharging(false);
            } else {
                double d = SurrenderedEntity.this.distanceToSqr(livingEntity);
                if (d < 9.0) {
                    Vec3 vec3d = livingEntity.getEyePosition();
                    SurrenderedEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
                }
            }
        }
    }

    class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !SurrenderedEntity.this.getMoveControl().hasWanted() && SurrenderedEntity.this.random.nextInt(SurrenderedEntity.LookAtTargetGoal.reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockPos = SurrenderedEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = SurrenderedEntity.this.blockPosition();
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.offset(SurrenderedEntity.this.random.nextInt(15) - 7, SurrenderedEntity.this.random.nextInt(11) - 5, SurrenderedEntity.this.random.nextInt(15) - 7);
                if (!SurrenderedEntity.this.level().isEmptyBlock(blockPos2)) continue;
                SurrenderedEntity.this.moveControl.setWantedPosition((double) blockPos2.getX() + 0.5, (double) blockPos2.getY() + 0.5, (double) blockPos2.getZ() + 0.5, 0.25);
                if (SurrenderedEntity.this.getTarget() != null) break;
                SurrenderedEntity.this.getLookControl().setLookAt((double) blockPos2.getX() + 0.5, (double) blockPos2.getY() + 0.5, (double) blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class TrackOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions targetPredicate;

        public TrackOwnerTargetGoal(PathfinderMob mob) {
            super(mob, false);
            this.targetPredicate = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        }

        @Override
        public boolean canUse() {
            return SurrenderedEntity.this.owner != null && SurrenderedEntity.this.owner.getTarget() != null && this.canAttack(SurrenderedEntity.this.owner.getTarget(), this.targetPredicate);
        }

        @Override
        public void start() {
            SurrenderedEntity.this.setTarget(SurrenderedEntity.this.owner.getTarget());
            super.start();
        }
    }
}
