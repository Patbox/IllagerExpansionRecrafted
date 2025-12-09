package eu.pb4.illagerexpansion.entity;

import com.google.common.collect.Sets;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.poly.Stunnable;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.BlockPos;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class InquisitorEntity extends AbstractIllager implements PlayerPolymerEntity, Stunnable {
    public static final Set<Item> AXES;
    private static final EntityDataAccessor<Boolean> STUNNED = SynchedEntityData.defineId(InquisitorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FINAL_ROAR = SynchedEntityData.defineId(InquisitorEntity.class, EntityDataSerializers.BOOLEAN);

    static {
        AXES = Sets.newHashSet(Items.DIAMOND_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
    }

    public boolean finalRoar;
    public int stunTick;
    public boolean isStunned;
    public int blockedCount;
    private AttributeMap attributeContainer;

    public InquisitorEntity(final EntityType<? extends InquisitorEntity> entityType, final Level world) {
        super(entityType, world);
        this.finalRoar = false;
        this.stunTick = 40;
        this.isStunned = false;
        this.blockedCount = 0;
        this.xpReward = 25;
        this.setPathfindingMalus(PathType.LEAVES, 0.0F);
        this.onCreated(this);
    }

    public static AttributeSupplier.Builder createInquisitorAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.33)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.6)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new InquisitorEntity.AttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    protected void customServerAiStep(ServerLevel world) {
        /*if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
            boolean bl = ((ServerWorld) this.getEntityWorld()).hasRaidAt(this.getBlockPos());
            ((MobNavigation) this.getNavigation()).setCanOpenDoors(bl);
        }*/
        super.customServerAiStep(world);
    }

    public void aiStep() {
        if (this.horizontalCollision && ((ServerLevel) this.level()).getGameRules().get(GameRules.MOB_GRIEFING)) {
            boolean bl = false;
            final AABB box = this.getBoundingBox().inflate(1.0);
            for (final BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ))) {
                final BlockState blockState = this.level().getBlockState(blockPos);
                final Block block = blockState.getBlock();
                if (!(block instanceof LeavesBlock) && !(block instanceof DoorBlock) && !(block instanceof TransparentBlock) && !(block instanceof HayBlock) && !(block instanceof SugarCaneBlock) && !(block instanceof WebBlock)) {
                    continue;
                }
                bl = (this.level().destroyBlock(blockPos, true, this) || bl);
                if (!(block instanceof DoorBlock)) {
                    continue;
                }
                this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);
            }
        }
        super.aiStep();
    }

    public boolean hasLineOfSight(final Entity entity) {
        return !this.getStunnedState() && super.hasLineOfSight(entity);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.getStunnedState();
    }

    @Override
    public void addAdditionalSaveData(final ValueOutput nbt) {
        nbt.putBoolean("Stunned", this.isStunned);
        nbt.putBoolean("FinalRoar", this.finalRoar);
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(final ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setStunnedState(nbt.getBooleanOr("Stunned", false));
        this.setFinalRoarState(nbt.getBooleanOr("FinalRoar", false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FINAL_ROAR, false);
        builder.define(STUNNED, false);
    }

    public boolean getStunnedState() {
        return this.entityData.get(STUNNED);
    }

    public void setStunnedState(final boolean isStunned) {
        this.entityData.set(STUNNED, isStunned);
    }

    public boolean getFinalRoarState() {
        return this.entityData.get(FINAL_ROAR);
    }

    public void setFinalRoarState(final boolean hasdoneRoar) {
        this.entityData.set(FINAL_ROAR, hasdoneRoar);
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCelebrating()) {
            return AbstractIllager.IllagerArmPose.CELEBRATING;
        }
        if (this.isAggressive()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }
        return AbstractIllager.IllagerArmPose.NEUTRAL;
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
    }

    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        if (this.getStunnedState()) {
            --this.stunTick;
            if (this.stunTick <= 0) {
                this.setStunnedState(false);
                this.stunTick = 40;
            }
        }
    }

    private List<LivingEntity> getTargets() {
        return (this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0), entity -> !(entity instanceof Monster)));
    }

    private void knockBack(final Entity entity) {
        final double d = entity.getX() - this.getX();
        final double e = entity.getZ() - this.getZ();
        final double f = Math.max(d * d + e * e, 0.001);
        entity.push(d / f * 0.6, 0.4, e / f * 0.6);
    }

    protected void blockedByItem(final LivingEntity target) {
        this.knockBack(target);
        target.hurtMarked = true;
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(final ServerLevelAccessor world, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable final SpawnGroupData entityData) {
        final SpawnGroupData entityData2 = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.getNavigation().setCanOpenDoors(true);
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        return entityData2;
    }

    protected void populateDefaultEquipmentSlots(final RandomSource random, DifficultyInstance difficulty) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
    }

    public boolean considersEntityAsAlly(final Entity other) {
        return super.considersEntityAsAlly(other) || (other instanceof LivingEntity && other.getType().is(EntityTypeTags.ILLAGER) && this.getTeam() == null && other.getTeam() == null);
    }

    public boolean hurtServer(ServerLevel world, final DamageSource source, final float amount) {
        final Entity attacker = source.getEntity();
        final boolean hasShield = this.getOffhandItem().is(Items.SHIELD);
        if (this.isAggressive()) {
            if (attacker instanceof LivingEntity) {
                final ItemStack item = ((LivingEntity) attacker).getMainHandItem();
                final ItemStack basherItem = this.getOffhandItem();
                final boolean isShield = basherItem.is(Items.SHIELD);
                if ((InquisitorEntity.AXES.contains(item.getItem()) || attacker instanceof IronGolem || this.blockedCount >= 4) && isShield) {
                    this.playSound(SoundEvents.SHIELD_BREAK.value(), 1.0f, 1.0f);
                    this.setStunnedState(true);
                    if (this.level() instanceof ServerLevel) {
                        ((ServerLevel) this.level()).sendParticles((ParticleOptions) new ItemParticleOption(ParticleTypes.ITEM, basherItem), this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.3, 0.2, 0.3, 0.003);
                        ((ServerLevel) this.level()).sendParticles((ParticleOptions) ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.3, 0.3, 0.3, 0.1);
                        this.playSound(SoundEvents.RAVAGER_ROAR, 1.0f, 1.0f);
                        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    this.getTargets().forEach(this::blockedByItem);
                    return super.hurtServer(world, source, amount);
                }
            }
            if (source.getDirectEntity() instanceof AbstractArrow && hasShield) {
                this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
            if (source.getDirectEntity() instanceof LivingEntity && hasShield) {
                ++this.blockedCount;
                this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0f, 1.0f);
                return false;
            }
        }
        final boolean bl2 = super.hurtServer(world, source, amount);
        return bl2;
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        if (this.getVehicle() instanceof Ravager r) {
            return r.isWithinMeleeAttackRange(entity);
        }


        return super.isWithinMeleeAttackRange(entity);
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.ILLAGER_BRUTE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.ILLAGER_BRUTE_DEATH;
    }

    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundRegistry.ILLAGER_BRUTE_HURT;
    }

    public void applyRaidBuffs(ServerLevel world, final int wave, final boolean unused) {
        final ItemStack itemStack = new ItemStack(Items.STONE_SWORD);
        final ItemStack itemstack1 = new ItemStack(Items.SHIELD);
        final Raid raid = this.getCurrentRaid();
        int i = 1;
        if (wave > raid.getNumGroups(Difficulty.NORMAL)) {
            i = 2;
        }
        final boolean bl2;
        final boolean bl = bl2 = (this.random.nextFloat() <= raid.getEnchantOdds());
        /*if (bl) {
            HashMap<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.set(map, itemStack);
        }*/
        this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
        this.setItemSlot(EquipmentSlot.OFFHAND, itemstack1);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        this.startSeenByPlayer(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.INQUISITOR;
    }

    class AttackGoal
            extends MeleeAttackGoal {
        public AttackGoal(InquisitorEntity vindicator) {
            super(vindicator, 1.0, false);

        }
    }
}