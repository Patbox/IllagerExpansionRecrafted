package eu.pb4.illagerexpansion.entity;

import com.google.common.collect.Sets;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.poly.Stunnable;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class InquisitorEntity extends IllagerEntity implements PlayerPolymerEntity, Stunnable {
    public static final Set<Item> AXES;
    private static final TrackedData<Boolean> STUNNED = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FINAL_ROAR = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    static {
        AXES = Sets.newHashSet(Items.DIAMOND_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
    }

    public boolean finalRoar;
    public int stunTick;
    public boolean isStunned;
    public int blockedCount;
    private AttributeContainer attributeContainer;

    public InquisitorEntity(final EntityType<? extends InquisitorEntity> entityType, final World world) {
        super(entityType, world);
        this.finalRoar = false;
        this.stunTick = 40;
        this.isStunned = false;
        this.blockedCount = 0;
        this.experiencePoints = 25;
        this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0F);
        this.onCreated(this);
    }

    public static DefaultAttributeContainer.Builder createInquisitorAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 80.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.33)
                .add(EntityAttributes.ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 1.6)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new InquisitorEntity.AttackGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>(this, MerchantEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, true));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    protected void mobTick(ServerWorld world) {
        /*if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
            boolean bl = ((ServerWorld) this.getWorld()).hasRaidAt(this.getBlockPos());
            ((MobNavigation) this.getNavigation()).setCanOpenDoors(bl);
        }*/
        super.mobTick(world);
    }

    public void tickMovement() {
        if (this.horizontalCollision && ((ServerWorld) this.getWorld()).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
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

    @Override
    public void writeCustomData(final WriteView nbt) {
        nbt.putBoolean("Stunned", this.isStunned);
        nbt.putBoolean("FinalRoar", this.finalRoar);
        super.writeCustomData(nbt);
    }

    @Override
    public void readCustomData(final ReadView nbt) {
        super.readCustomData(nbt);
        this.setStunnedState(nbt.getBoolean("Stunned", false));
        this.setFinalRoarState(nbt.getBoolean("FinalRoar", false));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FINAL_ROAR, false);
        builder.add(STUNNED, false);
    }

    public boolean getStunnedState() {
        return this.dataTracker.get(STUNNED);
    }

    public void setStunnedState(final boolean isStunned) {
        this.dataTracker.set(STUNNED, isStunned);
    }

    public boolean getFinalRoarState() {
        return this.dataTracker.get(FINAL_ROAR);
    }

    public void setFinalRoarState(final boolean hasdoneRoar) {
        this.dataTracker.set(FINAL_ROAR, hasdoneRoar);
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
        return new MobNavigation(this, world);
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
        this.knockBack(target);
        target.velocityModified = true;
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
    }

    @Nullable
    public EntityData initialize(final ServerWorldAccess world, final LocalDifficulty difficulty, final SpawnReason spawnReason, @Nullable final EntityData entityData) {
        final EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData);
        this.getNavigation().setCanOpenDoors(true);
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        return entityData2;
    }

    protected void initEquipment(final Random random, LocalDifficulty difficulty) {
        if (this.getRaid() == null) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
    }

    public boolean isInSameTeam(final Entity other) {
        return super.isInSameTeam(other) || (other instanceof LivingEntity && other.getType().isIn(EntityTypeTags.ILLAGER) && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null);
    }

    public boolean damage(ServerWorld world, final DamageSource source, final float amount) {
        final Entity attacker = source.getAttacker();
        final boolean hasShield = this.getOffHandStack().isOf(Items.SHIELD);
        if (this.isAttacking()) {
            if (attacker instanceof LivingEntity) {
                final ItemStack item = ((LivingEntity) attacker).getMainHandStack();
                final ItemStack basherItem = this.getOffHandStack();
                final boolean isShield = basherItem.isOf(Items.SHIELD);
                if ((InquisitorEntity.AXES.contains(item.getItem()) || attacker instanceof IronGolemEntity || this.blockedCount >= 4) && isShield) {
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK.value(), 1.0f, 1.0f);
                    this.setStunnedState(true);
                    if (this.getWorld() instanceof ServerWorld) {
                        ((ServerWorld) this.getWorld()).spawnParticles((ParticleEffect) new ItemStackParticleEffect(ParticleTypes.ITEM, basherItem), this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.3, 0.2, 0.3, 0.003);
                        ((ServerWorld) this.getWorld()).spawnParticles((ParticleEffect) ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.3, 0.3, 0.3, 0.1);
                        this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);
                        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    this.getTargets().forEach(this::knockback);
                    return super.damage(world, source, amount);
                }
            }
            if (source.getSource() instanceof PersistentProjectileEntity && hasShield) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
            if (source.getSource() instanceof LivingEntity && hasShield) {
                ++this.blockedCount;
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0f, 1.0f);
                return false;
            }
        }
        final boolean bl2 = super.damage(world, source, amount);
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

    public void addBonusForWave(ServerWorld world, final int wave, final boolean unused) {
        final ItemStack itemStack = new ItemStack(Items.STONE_SWORD);
        final ItemStack itemstack1 = new ItemStack(Items.SHIELD);
        final Raid raid = this.getRaid();
        int i = 1;
        if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
            i = 2;
        }
        final boolean bl2;
        final boolean bl = bl2 = (this.random.nextFloat() <= raid.getEnchantmentChance());
        /*if (bl) {
            HashMap<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.SHARPNESS, i);
            EnchantmentHelper.set(map, itemStack);
        }*/
        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        this.equipStack(EquipmentSlot.OFFHAND, itemstack1);
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

    class AttackGoal
            extends MeleeAttackGoal {
        public AttackGoal(InquisitorEntity vindicator) {
            super(vindicator, 1.0, false);

        }
    }
}