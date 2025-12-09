package eu.pb4.illagerexpansion.entity;

import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.entity.goal.PotionBowAttackGoal;
import eu.pb4.illagerexpansion.mixin.AreaEffectCloudEntityAccessor;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemistEntity extends AbstractIllager implements RangedAttackMob, PlayerPolymerEntity {
    private static final EntityDataAccessor<Boolean> POTION;
    private static final EntityDataAccessor<Boolean> BOW;

    static {
        POTION = SynchedEntityData.defineId(AlchemistEntity.class, EntityDataSerializers.BOOLEAN);
        BOW = SynchedEntityData.defineId(AlchemistEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public boolean inPotionState;
    public boolean inBowState;
    public int potionCooldown;
    private AttributeMap attributeContainer;

    public AlchemistEntity(final EntityType<? extends AlchemistEntity> entityType, final Level world) {
        super(entityType, world);
        this.inPotionState = false;
        this.inBowState = false;
        this.potionCooldown = 160;
        this.xpReward = 10;
        this.onCreated(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new PotionBowAttackGoal<>(this, 0.5, 20, 15.0f));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createAlchemistAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 23.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38);
    }

    public SpawnGroupData finalizeSpawn(final ServerLevelAccessor world, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable final SpawnGroupData entityData) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    public void performRangedAttack(final LivingEntity target, final float pullProgress) {
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.LINGERING_POTION)) {
            var potion = this.getItemBySlot(EquipmentSlot.MAINHAND).getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            final Vec3 vec3d = target.getDeltaMovement();
            final double d = target.getX() + vec3d.x - this.getX();
            final double e = target.getEyeY() - 1.100000023841858 - this.getY();
            final double f = target.getZ() + vec3d.z - this.getZ();
            final double g = Math.sqrt(d * d + f * f);
            var throwed = new ItemStack(Items.LINGERING_POTION);
            throwed.set(DataComponents.POTION_CONTENTS, potion);
            final AbstractThrownPotion potionEntity = new ThrownLingeringPotion(level(), this, throwed);
            potionEntity.setXRot(potionEntity.getXRot() + 20.0f);
            potionEntity.shoot(d, e + g * 0.2, f, 0.75f, 8.0f);
            if (!this.isSilent()) {
                level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
            }
            level().addFreshEntity(potionEntity);
            this.setBowState(true);
            return;
        }
        var bow = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        final ItemStack itemStack = this.getProjectile(bow);
        final AbstractArrow persistentProjectileEntity = ProjectileUtil.getMobArrow(this, itemStack, pullProgress, bow);
        final double d = target.getX() - this.getX();
        final double e = target.getY(0.3333333333333333) - persistentProjectileEntity.getY();
        final double f = target.getZ() - this.getZ();
        final double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.shoot(d, e + g * 0.20000000298023224, f, 1.6f, (float) (14 - level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        level().addFreshEntity(persistentProjectileEntity);
    }

    public void addAdditionalSaveData(final ValueOutput nbt) {
        nbt.putBoolean("BowState", this.inBowState);
        nbt.putBoolean("PotionState", this.inPotionState);
        super.addAdditionalSaveData(nbt);
    }

    private List<AreaEffectCloud> getNearbyClouds() {
        return level().getEntitiesOfClass(AreaEffectCloud.class, this.getBoundingBox().inflate(30.0), Entity::isAlive);
    }

    private void cancelEffect(final AreaEffectCloud areaEffectCloudEntity, final LivingEntity entity) {
        var potion = ((AreaEffectCloudEntityAccessor) areaEffectCloudEntity).getPotionContents();
        for (var effect : potion.getAllEffects()) {
            entity.removeEffect(effect.getEffect());
        }
    }

    private void removeEffectsinCloud(final AreaEffectCloud cloudEntity) {
        final List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, cloudEntity.getBoundingBox().inflate(0.3), Entity::isAlive);
        for (final LivingEntity entity : list) {
            if (entity instanceof AbstractIllager) {
                this.cancelEffect(cloudEntity, entity);
            }
        }
    }

    public void readAdditionalSaveData(final ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setPotionState(nbt.getBooleanOr("PotionState", false));
        this.setBowState(nbt.getBooleanOr("BowState", false));
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AlchemistEntity.POTION, false);
        builder.define(AlchemistEntity.BOW, true);
    }

    public boolean getPotionState() {
        return this.entityData.get(POTION);
    }

    public void setPotionState(final boolean potionState) {
        this.entityData.set(POTION, potionState);
    }

    public boolean getBowState() {
        return (boolean) this.entityData.get(AlchemistEntity.BOW);
    }

    public void setBowState(final boolean bowState) {
        this.entityData.set(AlchemistEntity.BOW, bowState);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    protected void customServerAiStep(ServerLevel world) {
        if (!this.getNearbyClouds().isEmpty()) {
            this.getNearbyClouds().forEach(this::removeEffectsinCloud);
        }
        --this.potionCooldown;
        if (this.potionCooldown <= 0) {
            this.setPotionState(true);
            this.potionCooldown = 160;
        }
        final ItemStack mainhand = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (this.getBowState() && mainhand.is(Items.LINGERING_POTION)) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            this.setPotionState(false);
        }
        if (this.getPotionState() && mainhand.is(Items.BOW)) {
            Holder<Potion> potion = Potions.POISON;
            final int randvalue = this.random.nextInt(3);
            if (randvalue == 1) {
                potion = Potions.SLOWNESS;
            }
            if (randvalue == 2) {
                potion = Potions.WEAKNESS;
            }
            var throwed = new ItemStack(Items.LINGERING_POTION);
            throwed.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(potion));
            this.setItemSlot(EquipmentSlot.MAINHAND, throwed);
            this.setBowState(false);
        }
        super.customServerAiStep(world);
    }

    public boolean considersEntityAsAlly(final Entity other) {
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
        return other instanceof LivingEntity && ((LivingEntity) other).getType().is(EntityTypeTags.ILLAGER) && this.getTeam() == null && other.getTeam() == null;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ILLUSIONER_DEATH;
    }

    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.ILLUSIONER_HURT;
    }

    @Override
    public void applyRaidBuffs(ServerLevel world, int wave, boolean unused) {

    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.BOW)) {
            return AbstractIllager.IllagerArmPose.BOW_AND_ARROW;
        }
        if (this.isAggressive() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.LINGERING_POTION)) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
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
        return EntitySkins.ALCHEMIST;
    }
}
