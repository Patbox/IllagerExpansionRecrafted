package eu.pb4.illagerexpansion.entity;

import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.entity.goal.PotionBowAttackGoal;
import eu.pb4.illagerexpansion.mixin.AreaEffectCloudEntityAccessor;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemistEntity extends IllagerEntity implements RangedAttackMob, PlayerPolymerEntity {
    private static final TrackedData<Boolean> POTION;
    private static final TrackedData<Boolean> BOW;

    static {
        POTION = DataTracker.registerData(AlchemistEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BOW = DataTracker.registerData(AlchemistEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

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
        this.onCreated(this);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new PotionBowAttackGoal<>(this, 0.5, 20, 15.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, false));
    }

    public AttributeContainer getAttributes() {
        if (this.attributeContainer == null) {
            this.attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 23.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38).build());
        }
        return this.attributeContainer;
    }

    public EntityData initialize(final ServerWorldAccess world, final LocalDifficulty difficulty, final SpawnReason spawnReason, @Nullable final EntityData entityData) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    public void shootAt(final LivingEntity target, final float pullProgress) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.LINGERING_POTION)) {
            var potion = this.getEquippedStack(EquipmentSlot.MAINHAND).getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            final Vec3d vec3d = target.getVelocity();
            final double d = target.getX() + vec3d.x - this.getX();
            final double e = target.getEyeY() - 1.100000023841858 - this.getY();
            final double f = target.getZ() + vec3d.z - this.getZ();
            final double g = Math.sqrt(d * d + f * f);
            final PotionEntity potionEntity = new PotionEntity(getWorld(), this);
            var throwed = new ItemStack(Items.LINGERING_POTION);
            throwed.set(DataComponentTypes.POTION_CONTENTS, potion);
            potionEntity.setItem(throwed);
            potionEntity.setPitch(potionEntity.getPitch() + 20.0f);
            potionEntity.setVelocity(d, e + g * 0.2, f, 0.75f, 8.0f);
            if (!this.isSilent()) {
                getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
            }
            getWorld().spawnEntity(potionEntity);
            this.setBowState(true);
            return;
        }
        final ItemStack itemStack = this.getProjectileType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        final PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, itemStack, pullProgress);
        final double d = target.getX() - this.getX();
        final double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        final double f = target.getZ() - this.getZ();
        final double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6f, (float) (14 - getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        getWorld().spawnEntity(persistentProjectileEntity);
    }

    public void writeCustomDataToNbt(final NbtCompound nbt) {
        nbt.putBoolean("BowState", this.inBowState);
        nbt.putBoolean("PotionState", this.inPotionState);
        super.writeCustomDataToNbt(nbt);
    }

    private List<AreaEffectCloudEntity> getNearbyClouds() {
        return getWorld().getEntitiesByClass(AreaEffectCloudEntity.class, this.getBoundingBox().expand(30.0), Entity::isAlive);
    }

    private void cancelEffect(final AreaEffectCloudEntity areaEffectCloudEntity, final LivingEntity entity) {
        var potion = ((AreaEffectCloudEntityAccessor) areaEffectCloudEntity).getPotionContentsComponent();
        for (var effect : potion.getEffects()) {
            entity.removeStatusEffect(effect.getEffectType());
        }
    }

    private void removeEffectsinCloud(final AreaEffectCloudEntity cloudEntity) {
        final List<LivingEntity> list = getWorld().getEntitiesByClass(LivingEntity.class, cloudEntity.getBoundingBox().expand(0.3), Entity::isAlive);
        for (final LivingEntity entity : list) {
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

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(AlchemistEntity.POTION, false);
        builder.add(AlchemistEntity.BOW, true);
    }

    public boolean getPotionState() {
        return this.dataTracker.get(POTION);
    }

    public void setPotionState(final boolean potionState) {
        this.dataTracker.set(POTION, potionState);
    }

    public boolean getBowState() {
        return (boolean) this.dataTracker.get(AlchemistEntity.BOW);
    }

    public void setBowState(final boolean bowState) {
        this.dataTracker.set(AlchemistEntity.BOW, bowState);
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
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            this.setPotionState(false);
        }
        if (this.getPotionState() && mainhand.isOf(Items.BOW)) {
            RegistryEntry<Potion> potion = Potions.POISON;
            final int randvalue = this.random.nextInt(3);
            if (randvalue == 1) {
                potion = Potions.SLOWNESS;
            }
            if (randvalue == 2) {
                potion = Potions.WEAKNESS;
            }
            var throwed = new ItemStack(Items.LINGERING_POTION);
            throwed.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT.with(potion));
            this.equipStack(EquipmentSlot.MAINHAND, throwed);
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
            return this.isTeammate(((VexEntity) other).getOwner());
        }
        return other instanceof LivingEntity && ((LivingEntity) other).getType().isIn(EntityTypeTags.ILLAGER) && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
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
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.ALCHEMIST;
    }
}
