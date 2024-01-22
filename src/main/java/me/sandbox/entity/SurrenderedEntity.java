package me.sandbox.entity;

import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import me.sandbox.sounds.SoundRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class SurrenderedEntity extends SkeletonEntity implements PolymerEntity {
    protected static final TrackedData<Byte> VEX_FLAGS = DataTracker.registerData(SurrenderedEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final int CHARGING_FLAG = 1;
    @Nullable MobEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;
    private AttributeContainer attributeContainer;


    public SurrenderedEntity(EntityType<? extends SurrenderedEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new SurrenderedEntity.VexMoveControl(this);
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(this.getDamageSources().starve(), 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new SurrenderedEntity.ChargeTargetGoal());
        this.goalSelector.add(8, new SurrenderedEntity.LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new SurrenderedEntity.TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 18.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D).build());
        }
        return attributeContainer;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte) 0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }
        if (nbt.contains("LifeTicks")) {
            this.setLifeTicks(nbt.getInt("LifeTicks"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
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
    public MobEntity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable MobEntity owner) {
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
        byte i = this.dataTracker.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.dataTracker.get(VEX_FLAGS);
        i = value ? (i |= mask) : (i &= ~mask);
        this.dataTracker.set(VEX_FLAGS, (byte) (i & 0xFF));
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
    public boolean tryAttack(Entity target) {
        if (!super.tryAttack(target)) {
            return false;
        }
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1), this);
        }
        return true;
    }

    @Override
    public void tickMovement() {
        if (!getWorld().isClient()) {
            for (int i = 0; i < 2; ++i) {
                ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.WHITE_ASH, this.prevX, this.prevY + 1.2, this.prevZ, 2, 0.2D, 0D, 0.2D, 0.025D);
            }
        }
        super.tickMovement();
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
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        for (int i = 0; i < data.size(); i++) {
            var e = data.get(i);

            if (e.id() == Entity.FLAGS.getId()) {
                data.set(i, DataTracker.SerializedEntry.of(Entity.FLAGS, (byte) (((byte) e.value()) | 0x1 << 5)));
            }
        }
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayerEntity player) {
        return List.of(new Pair<>(EquipmentSlot.HEAD, Items.SKELETON_SKULL.getDefaultStack()), new Pair<>(EquipmentSlot.CHEST, Items.CHAINMAIL_CHESTPLATE.getDefaultStack()));
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.STRAY;
    }

    class VexMoveControl extends MoveControl {
        public VexMoveControl(SurrenderedEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            Vec3d vec3d = new Vec3d(this.targetX - SurrenderedEntity.this.getX(), this.targetY - SurrenderedEntity.this.getY(), this.targetZ - SurrenderedEntity.this.getZ());
            double d = vec3d.length();
            if (d < SurrenderedEntity.this.getBoundingBox().getAverageSideLength()) {
                this.state = MoveControl.State.WAIT;
                SurrenderedEntity.this.setVelocity(SurrenderedEntity.this.getVelocity().multiply(0.5));
            } else {
                SurrenderedEntity.this.setVelocity(SurrenderedEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                if (SurrenderedEntity.this.getTarget() == null) {
                    Vec3d vec3d2 = SurrenderedEntity.this.getVelocity();
                    SurrenderedEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                    SurrenderedEntity.this.bodyYaw = SurrenderedEntity.this.getYaw();
                } else {
                    double e = SurrenderedEntity.this.getTarget().getX() - SurrenderedEntity.this.getX();
                    double f = SurrenderedEntity.this.getTarget().getZ() - SurrenderedEntity.this.getZ();
                    SurrenderedEntity.this.setYaw(-((float) MathHelper.atan2(e, f)) * 57.295776f);
                    SurrenderedEntity.this.bodyYaw = SurrenderedEntity.this.getYaw();
                }
            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (SurrenderedEntity.this.getTarget() != null && !SurrenderedEntity.this.getMoveControl().isMoving() && SurrenderedEntity.this.random.nextInt(SurrenderedEntity.ChargeTargetGoal.toGoalTicks(7)) == 0) {
                return SurrenderedEntity.this.squaredDistanceTo(SurrenderedEntity.this.getTarget()) > 4.0;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return SurrenderedEntity.this.getMoveControl().isMoving() && SurrenderedEntity.this.isCharging() && SurrenderedEntity.this.getTarget() != null && SurrenderedEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = SurrenderedEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                SurrenderedEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
            SurrenderedEntity.this.setCharging(true);
            SurrenderedEntity.this.playSound(SoundRegistry.SURRENDERED_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            SurrenderedEntity.this.setCharging(false);
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = SurrenderedEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (SurrenderedEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                SurrenderedEntity.this.tryAttack(livingEntity);
                SurrenderedEntity.this.setCharging(false);
            } else {
                double d = SurrenderedEntity.this.squaredDistanceTo(livingEntity);
                if (d < 9.0) {
                    Vec3d vec3d = livingEntity.getEyePos();
                    SurrenderedEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
                }
            }
        }
    }

    class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !SurrenderedEntity.this.getMoveControl().isMoving() && SurrenderedEntity.this.random.nextInt(SurrenderedEntity.LookAtTargetGoal.toGoalTicks(7)) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockPos = SurrenderedEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = SurrenderedEntity.this.getBlockPos();
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(SurrenderedEntity.this.random.nextInt(15) - 7, SurrenderedEntity.this.random.nextInt(11) - 5, SurrenderedEntity.this.random.nextInt(15) - 7);
                if (!SurrenderedEntity.this.getWorld().isAir(blockPos2)) continue;
                SurrenderedEntity.this.moveControl.moveTo((double) blockPos2.getX() + 0.5, (double) blockPos2.getY() + 0.5, (double) blockPos2.getZ() + 0.5, 0.25);
                if (SurrenderedEntity.this.getTarget() != null) break;
                SurrenderedEntity.this.getLookControl().lookAt((double) blockPos2.getX() + 0.5, (double) blockPos2.getY() + 0.5, (double) blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class TrackOwnerTargetGoal extends TrackTargetGoal {
        private final TargetPredicate targetPredicate;

        public TrackOwnerTargetGoal(PathAwareEntity mob) {
            super(mob, false);
            this.targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
        }

        @Override
        public boolean canStart() {
            return SurrenderedEntity.this.owner != null && SurrenderedEntity.this.owner.getTarget() != null && this.canTrack(SurrenderedEntity.this.owner.getTarget(), this.targetPredicate);
        }

        @Override
        public void start() {
            SurrenderedEntity.this.setTarget(SurrenderedEntity.this.owner.getTarget());
            super.start();
        }
    }
}
