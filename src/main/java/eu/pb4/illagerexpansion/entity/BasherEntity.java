package eu.pb4.illagerexpansion.entity;

import com.google.common.collect.Sets;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.poly.Stunnable;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
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
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BasherEntity
        extends IllagerEntity implements PlayerPolymerEntity, Stunnable {
    public static final Set<Item> AXES;
    private static final TrackedData<Boolean> STUNNED = DataTracker.registerData(BasherEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    static {
        AXES = Sets.newHashSet(Items.DIAMOND_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
    }

    public int stunTick = 60;
    public boolean isStunned = false;
    public int blockedCount;
    private AttributeContainer attributeContainer;

    public BasherEntity(EntityType<? extends BasherEntity> entityType, World world) {
        super(entityType, world);
        this.onCreated(this);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new IllagerEntity.LongDoorInteractGoal(this));
        this.goalSelector.add(4, new BasherEntity.AttackGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>(this, MerchantEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, true));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    @Override
    protected void mobTick() {
        if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
            boolean bl = ((ServerWorld) getWorld()).hasRaidAt(this.getBlockPos());
            ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(bl);
            super.mobTick();
        }
        if (!this.isAlive()) {
        }
    }

    @Override
    public boolean canSee(Entity entity) {
        if (this.getStunnedState()) {
            return false;
        }
        return super.canSee(entity);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Stunned", this.isStunned);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setStunnedState(nbt.getBoolean("Stunned"));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STUNNED, false);
    }

    @Override
    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
        boolean bl;
        ItemStack itemStack = new ItemStack(Items.SHIELD);
        Raid raid = this.getRaid();
        int i = 1;
        if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
            i = 2;
        }
        boolean bl2 = bl = this.random.nextFloat() <= raid.getEnchantmentChance();


        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    public boolean getStunnedState() {
        return this.dataTracker.get(STUNNED);
    }

    public void setStunnedState(boolean isStunned) {
        this.dataTracker.set(STUNNED, isStunned);
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

    public static DefaultAttributeContainer.Builder createBasherAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.31D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.2D);
    }


    @Override
    public IllagerEntity.State getState() {
        if (this.isCelebrating()) {
            return IllagerEntity.State.CELEBRATING;
        }
        if (this.isAttacking()) {
            return State.ATTACKING;
        }
        return IllagerEntity.State.CROSSED;
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundRegistry.BASHER_CELEBRATE;
    }

    @Override
    public boolean damage(final DamageSource source, final float amount) {
        final Entity attacker = source.getAttacker();
        final boolean hasShield = this.getMainHandStack().isOf(Items.SHIELD);
        if (this.isAttacking()) {
            if (attacker instanceof LivingEntity) {
                final ItemStack item = ((LivingEntity) attacker).getMainHandStack();
                final ItemStack basherItem = this.getMainHandStack();
                final boolean isShield = basherItem.isOf(Items.SHIELD);
                if ((BasherEntity.AXES.contains(item.getItem()) || attacker instanceof IronGolemEntity || this.blockedCount >= 4) && isShield) {
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
                    this.setStunnedState(true);
                    if (getWorld() instanceof ServerWorld) {
                        ((ServerWorld) getWorld()).spawnParticles((ParticleEffect) new ItemStackParticleEffect(ParticleTypes.ITEM, basherItem), this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.3, 0.2, 0.3, 0.003);
                        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                    }
                    return super.damage(source, amount);
                }
            }
            if (source.getSource() instanceof PersistentProjectileEntity && hasShield) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
            if (source.getSource() instanceof LivingEntity && hasShield) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                ++this.blockedCount;
                return false;
            }
        }
        boolean bl2 = super.damage(source, amount);
        return bl2;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData);
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        return entityData2;
    }



    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        if (this.getRaid() == null) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SHIELD));
        }
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getType().isIn(EntityTypeTags.ILLAGER)) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
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
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        this.onStartedTrackingBy(player);
        this.onTrackingStopped(player);
    }

    @Override
    public boolean isInAttackRange(LivingEntity entity) {
        if (this.getVehicle() instanceof RavagerEntity r) {
            return r.isInAttackRange(entity);
        }

        return super.isInAttackRange(entity);
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
