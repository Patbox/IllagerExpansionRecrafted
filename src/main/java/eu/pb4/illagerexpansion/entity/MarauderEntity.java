package eu.pb4.illagerexpansion.entity;

import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.entity.goal.HatchetAttackGoal;
import eu.pb4.illagerexpansion.entity.projectile.HatchetEntity;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

public class MarauderEntity extends IllagerEntity implements RangedAttackMob, PlayerPolymerEntity {
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(MarauderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private AttributeContainer attributeContainer;

    public MarauderEntity(EntityType<? extends MarauderEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.onCreated(this);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(2, new HatchetAttackGoal(this, 1.0, 100, 8.0f));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, false));
    }

    public static DefaultAttributeContainer.Builder createMarauderAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 21.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.30D);
    }

    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.HATCHET));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {

        HatchetEntity hatchetEntity = new HatchetEntity(this.getEntityWorld(), this, new ItemStack(ItemRegistry.HATCHET));
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - hatchetEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        hatchetEntity.setVelocity(d, e + g * (double) 0.2f, f, 1.2f, 14 - this.getEntityWorld().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ITEM_TRIDENT_THROW.value(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.getEntityWorld().spawnEntity(hatchetEntity);
    }


    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
    }

    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        double e = 0.30D;
        if (this.isCharging()) {
            double d = e * 0.8;
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(d);
        } else {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(e);
        }
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
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {

    }

    @Override
    public IllagerEntity.State getState() {
        if (this.isAttacking()) {
            return State.ATTACKING;
        }
        return State.NEUTRAL;
    }


    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.SAVAGER;
    }
}
