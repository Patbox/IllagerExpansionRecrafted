package me.sandbox.entity;

import com.mojang.authlib.properties.Property;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.api.utils.PolymerUtils;
import me.sandbox.poly.EntitySkins;
import me.sandbox.poly.PlayerPolymerEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.AreaEffectCloudEntity;
import java.util.List;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.potion.Potion;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import me.sandbox.entity.goal.PotionBowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.IllagerEntity;

public class AlchemistEntity extends IllagerEntity implements RangedAttackMob, PlayerPolymerEntity
{
    private static final TrackedData<Boolean> POTION;
    private static final TrackedData<Boolean> BOW;
    public boolean inPotionState;
    public boolean inBowState;
    public int potionCooldown;
    private AttributeContainer attributeContainer;

    public AlchemistEntity(final EntityType<? extends AlchemistEntity> entityType, final World world) {
        super(entityType, world);
        this.inPotionState = false;
        this.inBowState = false;
        this.potionCooldown = 160;
        this.experiencePoints = 10;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, (Goal)new SwimGoal((MobEntity)this));
        this.goalSelector.add(4, (Goal)new PotionBowAttackGoal<AlchemistEntity>(this, 0.5, 20, 15.0f));
        this.goalSelector.add(8, (Goal)new WanderAroundGoal((PathAwareEntity)this, 0.6));
        this.goalSelector.add(9, (Goal)new LookAtEntityGoal((MobEntity)this, (Class)PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, (Goal)new LookAtEntityGoal((MobEntity)this, (Class)MobEntity.class, 8.0f));
        this.targetSelector.add(1, (Goal)new RevengeGoal((PathAwareEntity)this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, (Goal)new ActiveTargetGoal((MobEntity)this, (Class)PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (Goal)new ActiveTargetGoal((MobEntity)this, (Class)MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, false));
    }

    public AttributeContainer getAttributes() {
        if (this.attributeContainer == null) {
            this.attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 23.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38).build());
        }
        return this.attributeContainer;
    }

    public EntityData initialize(final ServerWorldAccess world, final LocalDifficulty difficulty, final SpawnReason spawnReason, @Nullable final EntityData entityData, @Nullable final NbtCompound entityNbt) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack((ItemConvertible)Items.BOW));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public void attack(final LivingEntity target, final float pullProgress) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.LINGERING_POTION)) {
            final Potion potion = PotionUtil.getPotion(this.getEquippedStack(EquipmentSlot.MAINHAND));
            final Vec3d vec3d = target.getVelocity();
            final double d = target.getX() + vec3d.x - this.getX();
            final double e = target.getEyeY() - 1.100000023841858 - this.getY();
            final double f = target.getZ() + vec3d.z - this.getZ();
            final double g = Math.sqrt(d * d + f * f);
            final PotionEntity potionEntity = new PotionEntity(this.world, (LivingEntity)this);
            potionEntity.setItem(PotionUtil.setPotion(new ItemStack((ItemConvertible)Items.LINGERING_POTION), potion));
            potionEntity.setPitch(potionEntity.getPitch() + 20.0f);
            potionEntity.setVelocity(d, e + g * 0.2, f, 0.75f, 8.0f);
            if (!this.isSilent()) {
                this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
            }
            this.world.spawnEntity((Entity)potionEntity);
            this.setBowState(true);
            return;
        }
        final ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding((LivingEntity)this, Items.BOW)));
        final PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile((LivingEntity)this, itemStack, pullProgress);
        final double d = target.getX() - this.getX();
        final double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        final double f = target.getZ() - this.getZ();
        final double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity((Entity)persistentProjectileEntity);
    }

    public void writeCustomDataToNbt(final NbtCompound nbt) {
        nbt.putBoolean("BowState", this.inBowState);
        nbt.putBoolean("PotionState", this.inPotionState);
        super.writeCustomDataToNbt(nbt);
    }

    private List<AreaEffectCloudEntity> getNearbyClouds() {
        return this.world.getEntitiesByClass(AreaEffectCloudEntity.class, this.getBoundingBox().expand(30.0), Entity::isAlive);
    }

    private void cancelEffect(final AreaEffectCloudEntity areaEffectCloudEntity, final LivingEntity entity) {
        final Potion potion = areaEffectCloudEntity.getPotion();
        if (potion.getEffects().size() > 0) {
            final StatusEffectInstance statusEffectInstance = potion.getEffects().get(0);
            final StatusEffect statusEffect = statusEffectInstance.getEffectType();
            entity.removeStatusEffect(statusEffect);
        }
    }

    private void removeEffectsinCloud(final AreaEffectCloudEntity cloudEntity) {
        final List<LivingEntity> list = this.world.getEntitiesByClass(LivingEntity.class, cloudEntity.getBoundingBox().expand(0.3), Entity::isAlive);
        for (int i = 0; i < list.size(); ++i) {
            final LivingEntity entity = list.get(i);
            if (entity instanceof IllagerEntity) {
                this.cancelEffect(cloudEntity, entity);
            }
        }
    }

    public void readCustomDataFromNbt(final NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setPotionState(nbt.getBoolean("PotionState"));
        this.setBowState(nbt.getBoolean("BowState"));
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking((TrackedData)AlchemistEntity.POTION, (Object)false);
        this.dataTracker.startTracking((TrackedData)AlchemistEntity.BOW, (Object)true);
        super.initDataTracker();
    }

    public void setPotionState(final boolean potionState) {
        this.dataTracker.set(POTION, potionState);
    }

    public boolean getPotionState() {
        return (boolean)this.dataTracker.get(POTION);
    }

    public void setBowState(final boolean bowState) {
        this.dataTracker.set((TrackedData)AlchemistEntity.BOW, (Object)bowState);
    }

    public boolean getBowState() {
        return (boolean)this.dataTracker.get((TrackedData)AlchemistEntity.BOW);
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    protected void mobTick() {
        if (!this.getNearbyClouds().isEmpty()) {
            this.getNearbyClouds().forEach(this::removeEffectsinCloud);
        }
        --this.potionCooldown;
        if (this.potionCooldown <= 0) {
            this.setPotionState(true);
            this.potionCooldown = 160;
        }
        final ItemStack mainhand = this.getStackInHand(Hand.MAIN_HAND);
        if (this.getBowState() && mainhand.isOf(Items.LINGERING_POTION)) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack((ItemConvertible)Items.BOW));
            this.setPotionState(false);
        }
        if (this.getPotionState() && mainhand.isOf(Items.BOW)) {
            Potion potion = null;
            final int randvalue = this.random.nextInt(3);
            if (randvalue == 0) {
                potion = Potions.POISON;
            }
            if (randvalue == 1) {
                potion = Potions.SLOWNESS;
            }
            if (randvalue == 2) {
                potion = Potions.WEAKNESS;
            }
            this.equipStack(EquipmentSlot.MAINHAND, PotionUtil.setPotion(new ItemStack((ItemConvertible)Items.LINGERING_POTION), potion));
            this.setBowState(false);
        }
        super.mobTick();
    }

    public boolean isTeammate(final Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof VexEntity) {
            return this.isTeammate((Entity)((VexEntity)other).getOwner());
        }
        return other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ILLUSIONER_DEATH;
    }

    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.ENTITY_ILLUSIONER_HURT;
    }

    public void addBonusForWave(final int wave, final boolean unused) {
    }

    public IllagerEntity.State getState() {
        if (this.isAttacking() && this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.BOW)) {
            return IllagerEntity.State.BOW_AND_ARROW;
        }
        if (this.isAttacking() && this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.LINGERING_POTION)) {
            return IllagerEntity.State.ATTACKING;
        }
        return IllagerEntity.State.CROSSED;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return PolymerEntityUtils.createPlayerSpawnPacket(this);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.onTrackingStarted(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.ALCHEMIST;
    }

    static {
        POTION = DataTracker.registerData((Class)AlchemistEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BOW = DataTracker.registerData((Class)AlchemistEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
