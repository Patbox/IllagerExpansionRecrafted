package me.sandbox.entity;

import com.mojang.authlib.properties.Property;
import me.sandbox.item.ItemRegistry;
import me.sandbox.poly.EntitySkins;
import me.sandbox.poly.PlayerPolymerEntity;
import me.sandbox.poly.Stunnable;
import me.sandbox.sounds.SoundRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import com.google.common.collect.Sets;
import net.minecraft.enchantment.Enchantment;
import java.util.HashMap;

import net.minecraft.entity.mob.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import com.google.common.collect.Maps;
import net.minecraft.world.*;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.util.math.Box;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.Item;
import java.util.Set;

public class InquisitorEntity extends IllagerEntity implements PlayerPolymerEntity, Stunnable
{
    public boolean finalRoar;
    public int stunTick;
    public boolean isStunned;
    public int blockedCount;
    public static final Set<Item> AXES;
    private static final TrackedData<Boolean> STUNNED = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FINAL_ROAR = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private AttributeContainer attributeContainer;
    static {
        AXES = Sets.newHashSet(Items.DIAMOND_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
    }

    public InquisitorEntity(final EntityType<? extends InquisitorEntity> entityType, final World world) {
        super(entityType, world);
        this.finalRoar = false;
        this.stunTick = 40;
        this.isStunned = false;
        this.blockedCount = 0;
        this.experiencePoints = 25;
        this.onCreated(this);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal((MobEntity)this));
        this.goalSelector.add(4, new InquisitorEntity.AttackGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity) this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity) this, MerchantEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity) this, IronGolemEntity.class, true));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal((MobEntity)this, MobEntity.class, 8.0f));
    }

    protected void mobTick() {
        if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
            boolean bl = ((ServerWorld) this.getWorld()).hasRaidAt(this.getBlockPos());
            ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(bl);
        }
        super.mobTick();
    }

    public AttributeContainer getAttributes() {
        if (this.attributeContainer == null) {
            this.attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.33).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.6).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8).build());
        }
        return this.attributeContainer;
    }

    public void tickMovement() {
        if (this.horizontalCollision && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            boolean bl = false;
            final Box box = this.getBoundingBox().expand(1.0);
            for (final BlockPos blockPos : BlockPos.iterate(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ))) {
                final BlockState blockState = this.getWorld().getBlockState(blockPos);
                final Block block = blockState.getBlock();
                if (!(block instanceof LeavesBlock) && !(block instanceof DoorBlock) && !(block instanceof TransparentBlock) && !(block instanceof HayBlock) && !(block instanceof SugarCaneBlock) && !(block instanceof CobwebBlock)) {
                    continue;
                }
                bl = (this.getWorld().breakBlock(blockPos, true, this) || bl);
                if (!(block instanceof DoorBlock)) {
                    continue;
                }
                this.playSound(SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 1.0f);
            }
        }
        super.tickMovement();
    }

    public boolean canSee(final Entity entity) {
        return !this.getStunnedState() && super.canSee(entity);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.getStunnedState();
    }

    public void writeCustomDataToNbt(final NbtCompound nbt) {
        nbt.putBoolean("Stunned", this.isStunned);
        nbt.putBoolean("FinalRoar", this.finalRoar);
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(final NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setStunnedState(nbt.getBoolean("Stunned"));
        this.setFinalRoarState(nbt.getBoolean("FinalRoar"));
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(STUNNED,false);
        this.dataTracker.startTracking(FINAL_ROAR, false);
        super.initDataTracker();
    }

    public void setStunnedState(final boolean isStunned) {
        this.dataTracker.set(STUNNED, isStunned);
    }

    public boolean getStunnedState() {
        return (boolean)this.dataTracker.get(STUNNED);
    }

    public void setFinalRoarState(final boolean hasdoneRoar) {
        this.dataTracker.set(FINAL_ROAR, hasdoneRoar);
    }

    public boolean getFinalRoarState() {
        return (boolean)this.dataTracker.get(FINAL_ROAR);
    }

    public IllagerEntity.State getState() {
        if (this.isCelebrating()) {
            return IllagerEntity.State.CELEBRATING;
        }
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        return IllagerEntity.State.NEUTRAL;
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new InquisitorEntity.Navigation(this, world);
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
        return (this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(8.0), entity -> !(entity instanceof HostileEntity)));
    }

    private void knockBack(final Entity entity) {
        final double d = entity.getX() - this.getX();
        final double e = entity.getZ() - this.getZ();
        final double f = Math.max(d * d + e * e, 0.001);
        entity.addVelocity(d / f * 0.6, 0.4, e / f * 0.6);
    }

    protected void knockback(final LivingEntity target) {
        this.knockBack((Entity)target);
        target.velocityModified = true;
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
    }

    @Nullable
    public EntityData initialize(final ServerWorldAccess world, final LocalDifficulty difficulty, final SpawnReason spawnReason, @Nullable final EntityData entityData, @Nullable final NbtCompound entityNbt) {
        final EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        return entityData2;
    }

    protected void initEquipment(final Random random, LocalDifficulty difficulty) {
        if (this.getRaid() == null) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
    }

    public boolean isTeammate(final Entity other) {
        return super.isTeammate(other) || (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null);
    }

    public boolean damage(final DamageSource source, final float amount) {
        final Entity attacker = source.getAttacker();
        final boolean hasShield = this.getOffHandStack().isOf(Items.SHIELD);
        if (this.isAttacking()) {
            if (attacker instanceof LivingEntity) {
                final ItemStack item = ((LivingEntity) attacker).getMainHandStack();
                final ItemStack basherItem = this.getOffHandStack();
                final boolean isShield = basherItem.isOf(Items.SHIELD);
                if ((InquisitorEntity.AXES.contains(item.getItem()) || attacker instanceof IronGolemEntity || this.blockedCount >= 4) && isShield) {
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
                    this.setStunnedState(true);
                    if (this.getWorld() instanceof ServerWorld) {
                        ((ServerWorld)this.getWorld()).spawnParticles((ParticleEffect)new ItemStackParticleEffect(ParticleTypes.ITEM, basherItem), this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.3, 0.2, 0.3, 0.003);
                        ((ServerWorld)this.getWorld()).spawnParticles((ParticleEffect)ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.3, 0.3, 0.3, 0.1);
                        this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);
                        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    this.getTargets().forEach(this::knockback);
                    return super.damage(source, amount);
                }
            }
            if (source.getSource() instanceof PersistentProjectileEntity && hasShield) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
            if (source.getSource() instanceof LivingEntity && hasShield) {
                ++this.blockedCount;
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                return false;
            }
        }
        final boolean bl2 = super.damage(source, amount);
        return bl2;
    }

    @Override
    public boolean isInAttackRange(LivingEntity entity) {
        if (this.getVehicle() instanceof RavagerEntity r) {
            return r.isInAttackRange(entity);
        }


        return super.isInAttackRange(entity);
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

    public void addBonusForWave(final int wave, final boolean unused) {
        final ItemStack itemStack = new ItemStack((ItemConvertible)Items.STONE_SWORD);
        final ItemStack itemstack1 = new ItemStack((ItemConvertible)Items.SHIELD);
        final Raid raid = this.getRaid();
        int i = 1;
        if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
            i = 2;
        }
        final boolean bl2;
        final boolean bl = bl2 = (this.random.nextFloat() <= raid.getEnchantmentChance());
        if (bl) {
            HashMap<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.set(map, itemStack);
        }
        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        this.equipStack(EquipmentSlot.OFFHAND, itemstack1);
    }
    class AttackGoal
            extends MeleeAttackGoal {
        public AttackGoal(InquisitorEntity vindicator) {
            super(vindicator, 1.0, false);

        }
    }
    class Navigation extends MobNavigation {
        public Navigation(final MobEntity mobEntity, final World world) {
            super(mobEntity, world);
        }

        protected PathNodeNavigator createPathNodeNavigator(final int range) {
            this.nodeMaker = new PathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, range);
        }
    }
    static class PathNodeMaker extends LandPathNodeMaker {
        protected PathNodeType adjustNodeType(final BlockView world, final BlockPos pos, final PathNodeType type) {
            if (type == PathNodeType.LEAVES) {
                return PathNodeType.OPEN;
            }
            return super.adjustNodeType(world, pos, type);
        }
    }





    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.INQUISITOR;
    }
}