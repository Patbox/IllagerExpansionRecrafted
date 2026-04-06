package eu.pb4.illagerexpansion.entity;

import com.google.common.collect.Sets;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.poly.Stunnable;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BasherEntity
        extends AbstractIllager implements PlayerPolymerEntity, Stunnable {
    public static final Set<Item> AXES;
    private static final EntityDataAccessor<Boolean> STUNNED = SynchedEntityData.defineId(BasherEntity.class, EntityDataSerializers.BOOLEAN);

    static {
        AXES = Sets.newHashSet(Items.DIAMOND_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
    }

    public int stunTick = 60;
    public boolean isStunned = false;
    public int blockedCount;

    public BasherEntity(EntityType<? extends BasherEntity> entityType, Level world) {
        super(entityType, world);
        this.onCreated(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(4, new BasherEntity.AttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
            boolean bl = ((ServerLevel) level()).isRaided(this.blockPosition());
            ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(bl);
            super.customServerAiStep(world);
        }
        if (!this.isAlive()) {
        }
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        if (this.getStunnedState()) {
            return false;
        }
        return super.hasLineOfSight(entity);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        nbt.putBoolean("Stunned", this.isStunned);
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setStunnedState(nbt.getBooleanOr("Stunned", false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STUNNED, false);
    }

    @Override
    public void applyRaidBuffs(ServerLevel world, int wave, boolean unused) {
        boolean bl;
        ItemStack itemStack = new ItemStack(Items.SHIELD);
        Raid raid = this.getCurrentRaid();
        int i = 1;
        if (wave > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 2;
        }
        boolean bl2 = bl = this.random.nextFloat() <= raid.getEnchantOdds();


        this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
    }

    public boolean getStunnedState() {
        return this.entityData.get(STUNNED);
    }

    public void setStunnedState(boolean isStunned) {
        this.entityData.set(STUNNED, isStunned);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        if (this.getStunnedState()) {
            --stunTick;
            if (stunTick == 0) {
                this.setStunnedState(false);
            }
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getStunnedState();
    }

    public static AttributeSupplier.Builder createBasherAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2D);
    }


    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCelebrating()) {
            return AbstractIllager.IllagerArmPose.CELEBRATING;
        }
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundRegistry.BASHER_CELEBRATE;
    }

    @Override
    public boolean hurtServer(ServerLevel world, final DamageSource source, final float amount) {
        final Entity attacker = source.getEntity();
        final boolean hasShield = this.getMainHandItem().is(Items.SHIELD);
        if (this.isAggressive()) {
            if (attacker instanceof LivingEntity) {
                final ItemStack item = ((LivingEntity) attacker).getMainHandItem();
                final ItemStack basherItem = this.getMainHandItem();
                final boolean isShield = basherItem.is(Items.SHIELD);
                if ((BasherEntity.AXES.contains(item.getItem()) || attacker instanceof IronGolem || this.blockedCount >= 4) && isShield) {
                    this.playSound(SoundEvents.SHIELD_BREAK.value(), 1.0f, 1.0f);
                    this.setStunnedState(true);
                    if (level() instanceof ServerLevel) {
                        ((ServerLevel) level()).sendParticles((ParticleOptions) new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(basherItem)), this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.3, 0.2, 0.3, 0.003);
                        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                    }
                    return super.hurtServer(world, source, amount);
                }
            }
            if (source.getDirectEntity() instanceof AbstractArrow && hasShield) {
                this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
            if (source.getDirectEntity() instanceof LivingEntity && hasShield) {
                this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
        }
        boolean bl2 = super.hurtServer(world, source, amount);
        return bl2;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData entityData2 = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        return entityData2;
    }



    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SHIELD));
        }
    }

    @Override
    public boolean considersEntityAsAlly(Entity other) {
        if (super.considersEntityAsAlly(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
        }
        return false;
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.BASHER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.BASHER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.BASHER_HURT;
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        this.startSeenByPlayer(player);
        this.onTrackingStopped(player);
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        if (this.getVehicle() instanceof Ravager r) {
            return r.isWithinMeleeAttackRange(entity);
        }

        return super.isWithinMeleeAttackRange(entity);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.BASHER;
    }

    class AttackGoal
            extends MeleeAttackGoal {
        public AttackGoal(BasherEntity vindicator) {
            super(vindicator, 1.0, false);

        }
    }
}
