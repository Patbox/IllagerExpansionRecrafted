
package me.sandbox.entity;

import java.util.List;

import com.mojang.authlib.properties.Property;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.api.utils.PolymerUtils;
import me.sandbox.poly.EntitySkins;
import me.sandbox.poly.PlayerPolymerEntity;
import me.sandbox.sounds.SoundRegistry;
import me.sandbox.util.spellutil.SpellParticleUtil;
import me.sandbox.util.spellutil.TeleportUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InvokerEntity
        extends SpellcastingIllagerEntity implements SkinOverlayOwner, PlayerPolymerEntity {
    @Nullable
    private SheepEntity wololoTarget;
    private static final TrackedData<Boolean> SHIELDED = DataTracker.registerData(InvokerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean inSecondPhase = false;
    public int cooldown;
    public int tpcooldown;
    public boolean isAoeCasting = false;
    public int fangaoecooldown;
    public boolean isShielded = false;
    public boolean currentlyShielded;
    public int shieldduration;
    public boolean canCastShield;
    public int damagecount;

    public InvokerEntity(EntityType<? extends InvokerEntity> entityType, World world) {
        super((EntityType<? extends SpellcastingIllagerEntity>) entityType, world);
        this.experiencePoints = 80;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(3, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(5, new AreaDamageGoal());
        this.goalSelector.add(4, new CastTeleportGoal());
        this.goalSelector.add(5, new SummonVexGoal());
        this.goalSelector.add(5, new ConjureAoeFangsGoal());
        this.goalSelector.add(6, new ConjureFangsGoal());
        this.goalSelector.add(6, new WololoGoal());
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity) this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity) this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity) this, IronGolemEntity.class, false));
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.getShieldedState();
    }

    private AttributeContainer attributeContainer;

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.36D)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                    .build());
        }
        return attributeContainer;
    }
    @Override
    public boolean tryAttack(Entity target) {
        if (!super.tryAttack(target)) {
            return false;
        }
        if (target instanceof LivingEntity) {
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 0), this);
        }
        return true;
    }

    private final ServerBossBar bossBar = (ServerBossBar) new ServerBossBar(this.getDisplayName(), BossBar.Color.YELLOW, BossBar.Style.PROGRESS).setDarkenSky(true);

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SHIELDED, false);
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setShieldedState(nbt.getBoolean("Invul"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Invul", this.isShielded);
        super.writeCustomDataToNbt(nbt);
    }
    public void setShieldedState(boolean isShielded) {
        this.dataTracker.set(SHIELDED, isShielded);
    }
    public boolean getShieldedState() {
        return this.dataTracker.get(SHIELDED);
    }

    @Override
    protected void mobTick() {
        --this.tpcooldown;
        --this.cooldown;
        --this.fangaoecooldown;
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        if (isAoeCasting && this.isSpellcasting()) {
            SpellParticleUtil spellParticleUtil = new SpellParticleUtil();
            spellParticleUtil.setSpellParticles(this, this.world, ParticleTypes.SMOKE, 2, 0.06D);
        }
        if (damagecount >= 2) {
            this.setShieldedState(true);
        }
        if (getShieldedState()) {
            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.5, this.getZ(), 1, 0.5D, 0.7D, 0.5D, 0.15D);
            }
        }
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.3, this.getZ(), 1, 0.2D, 0.2D, 0.2D, 0.005D);
        }
    }
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
        this.onTrackingStarted(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
        this.onTrackingStopped(player);
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof SurrenderedEntity) {
            return this.isTeammate(((SurrenderedEntity) other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }
    @Override
    public IllagerEntity.State getState() {
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        if (this.isSpellcasting()) {
            return State.SPELLCASTING;
        }
        if (this.isCelebrating()) {
            return State.CELEBRATING;
        }
        return IllagerEntity.State.CROSSED;
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if ((source.getSource()) instanceof PersistentProjectileEntity) {
            if (getShieldedState()) {
                return false;
            } else {
                damagecount++;
            }
        }
        if (!((source.getSource()) instanceof PersistentProjectileEntity)) {
            if (getShieldedState()) {
                if ((source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE)) {
                    return false;
                } else {
                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1, this.getZ(), 30, 0.5D, 0.7D, 0.5D, 0.5D);
                    }
                    this.playSound(SoundRegistry.INVOKER_SHIELD_BREAK, 1.0f, 1.0f);
                    this.setShieldedState(false);
                    damagecount = 0;
                }
            }
        }

        boolean bl2 = super.damage(source, amount);
        return bl2;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.INVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.INVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.INVOKER_HURT;
    }

    void setWololoTarget(@Nullable SheepEntity sheep) {
        this.wololoTarget = sheep;
    }

    @Nullable
    SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundRegistry.INVOKER_COMPLETE_CAST;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }


    class LookAtTargetOrWololoTarget
            extends SpellcastingIllagerEntity.LookAtTargetGoal {
        LookAtTargetOrWololoTarget() {
        }

        @Override
        public void tick() {
            if (InvokerEntity.this.getTarget() != null) {
                InvokerEntity.this.getLookControl().lookAt(InvokerEntity.this.getTarget(), InvokerEntity.this.getMaxHeadRotation(), InvokerEntity.this.getMaxLookPitchChange());
            } else if (InvokerEntity.this.getWololoTarget() != null) {
                InvokerEntity.this.getLookControl().lookAt(InvokerEntity.this.getWololoTarget(), InvokerEntity.this.getMaxHeadRotation(), InvokerEntity.this.getMaxLookPitchChange());
            }
        }
    }

    class SummonVexGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeVexPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility().ignoreDistanceScalingFactor();

        SummonVexGoal() {
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }
            if (inSecondPhase) {
                return false;
            }
            int i = InvokerEntity.this.world.getTargets(SurrenderedEntity.class, this.closeVexPredicate, InvokerEntity.this, InvokerEntity.this.getBoundingBox().expand(20.0)).size();
            return 3 > i;
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 340;
        }

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld) InvokerEntity.this.world;
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos = InvokerEntity.this.getBlockPos().add(-2 + InvokerEntity.this.random.nextInt(5), 1, -2 + InvokerEntity.this.random.nextInt(5));
                SurrenderedEntity surrenderedEntity = EntityRegistry.SURRENDERED.create(InvokerEntity.this.world);
                surrenderedEntity.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
                surrenderedEntity.initialize(serverWorld, InvokerEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
                surrenderedEntity.setOwner(InvokerEntity.this);
                surrenderedEntity.setBounds(blockPos);
                surrenderedEntity.setLifeTicks(20 * (30 + InvokerEntity.this.random.nextInt(90)));
                serverWorld.spawnEntityAndPassengers(surrenderedEntity);
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.INVOKER_SUMMON_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
        }
    }

    class ConjureFangsGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        ConjureFangsGoal() {
        }
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }
            if (inSecondPhase) {
                return false;
            }
            return true;
        }

        @Override
        protected int getSpellTicks() {
            return 40;
        }

        @Override
        protected int startTimeDelay() {
            return 100;
        }

        @Override
        protected void castSpell() {
            LivingEntity livingEntity = InvokerEntity.this.getTarget();
            double d = Math.min(livingEntity.getY(), InvokerEntity.this.getY());
            double e = Math.max(livingEntity.getY(), InvokerEntity.this.getY()) + 1.0;
            float f = (float) MathHelper.atan2(livingEntity.getZ() - InvokerEntity.this.getZ(), livingEntity.getX() - InvokerEntity.this.getX());

            if (InvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float) i * (float) Math.PI * 0.4f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(g) * 1.5, InvokerEntity.this.getZ() + (double) MathHelper.sin(g) * 1.5, d, e, g, 0);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float) i * (float) Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(g) * 2.5, InvokerEntity.this.getZ() + (double) MathHelper.sin(g) * 2.5, d, e, g, 3);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float) i * (float) Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(g) * 3.5, InvokerEntity.this.getZ() + (double) MathHelper.sin(g) * 2.5, d, e, g, 3);
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = 1 * i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(f) * h, InvokerEntity.this.getZ() + (double) MathHelper.sin(f) * h, d, e, f, j);
                }
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = 1 * i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(f+ 0.4f) * h, InvokerEntity.this.getZ() + (double) MathHelper.sin(f + 0.3f) * h, d, e, f, j);
                }
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = 1 * i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) MathHelper.cos(f - 0.4f) * h, InvokerEntity.this.getZ() + (double) MathHelper.sin(f -0.3f) * h, d, e, f, j);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = new BlockPos(x, y, z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = InvokerEntity.this.world.getBlockState(blockPos2 = blockPos.down())).isSideSolidFullSquare(InvokerEntity.this.world, blockPos2, Direction.UP))
                    continue;
                if (!InvokerEntity.this.world.isAir(blockPos) && !(voxelShape = (blockState2 = InvokerEntity.this.world.getBlockState(blockPos)).getCollisionShape(InvokerEntity.this.world, blockPos)).isEmpty()) {
                    d = voxelShape.getMax(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
            if (bl) {
                InvokerEntity.this.world.spawnEntity(new InvokerFangsEntity(InvokerEntity.this.world, x, (double)blockPos.getY()+0.2 + d, z, yaw, warmup, InvokerEntity.this));
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.INVOKER_FANGS_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.FANGS;
        }
    }

    public class WololoGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate convertibleSheepPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).setPredicate(livingEntity -> ((SheepEntity) livingEntity).getColor() == DyeColor.BLUE);

        @Override
        public boolean canStart() {
            if (InvokerEntity.this.getTarget() != null) {
                return false;
            }
            if (InvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (InvokerEntity.this.age < this.startTime) {
                return false;
            }
            if (!InvokerEntity.this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            }
            List<SheepEntity> list = InvokerEntity.this.world.getTargets(SheepEntity.class, this.convertibleSheepPredicate, InvokerEntity.this, InvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            InvokerEntity.this.setWololoTarget(list.get(InvokerEntity.this.random.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return InvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }

        @Override
        public void stop() {
            super.stop();
            InvokerEntity.this.setWololoTarget(null);
        }

        @Override
        protected void castSpell() {
            SheepEntity sheepEntity = InvokerEntity.this.getWololoTarget();
            if (sheepEntity != null && sheepEntity.isAlive()) {
                sheepEntity.setColor(DyeColor.RED);
            }
        }

        @Override
        protected int getInitialCooldown() {
            return 40;
        }

        @Override
        protected int getSpellTicks() {
            return 140;
        }

        @Override
        protected int startTimeDelay() {
            return 600;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.WOLOLO;
        }
    }
    public class AreaDamageGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {

        @Override
        public boolean canStart() {
            if (InvokerEntity.this.getTarget() == null) {
                return false;
            }
            if (InvokerEntity.this.cooldown < 0) {
                isAoeCasting = true;
                return true;
            }
            return false;
        }
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(6), entity -> !(entity instanceof IllagerEntity) && !(entity instanceof SurrenderedEntity) && !(entity instanceof RavagerEntity));
        }
        private void knockBack(Entity entity) {
            double d = entity.getX() - InvokerEntity.this.getX();
            double e = entity.getZ() - InvokerEntity.this.getZ();
            double f = Math.max(d * d + e * e, 0.001);
            entity.addVelocity(d / f * 6, 0.65, e / f * 6);
        }
        protected void knockback(LivingEntity target) {
            this.knockBack(target);
            target.velocityModified = true;
        }


        @Override
        public void stop() {
            isAoeCasting = false;
            super.stop();
        }
        private void buff(LivingEntity entity) {
            this.knockback(entity);
            entity.damage(DamageSource.MAGIC, 11.0f);
            double x = entity.getX();
            double y = entity.getY()+1;
            double z = entity.getZ();
            ((ServerWorld)world).spawnParticles(ParticleTypes.SMOKE,x, y+1,z,10,0.2D, 0.2D,0.2D,0.015D);
        }

        @Override
        protected void castSpell() {
            InvokerEntity.this.cooldown = 300;
            getTargets().forEach(this::buff);
            isAoeCasting = false;
            double posx = InvokerEntity.this.getX();
            double posy = InvokerEntity.this.getY();
            double posz = InvokerEntity.this.getZ();
            ((ServerWorld)world).spawnParticles(ParticleTypes.LARGE_SMOKE,posx, posy+1, posz,350,1.0D, 0.8D,1.0D,0.3D);
        }

        @Override
        protected int getInitialCooldown() {
            return 50;
        }

        @Override
        protected int getSpellTicks() {
            return 50;
        }

        @Override
        protected int startTimeDelay() {
            return 400;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.INVOKER_BIG_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.BLINDNESS;
        }
    }
    public class CastTeleportGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        InvokerEntity sorcerer = InvokerEntity.this;
        @Override
        public boolean canStart() {
            if (InvokerEntity.this.getTarget() == null) {
                return false;
            }
            if (InvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (InvokerEntity.this.tpcooldown < 0 && !(getTargets().isEmpty())) {
                return true;
            }
            return false;
        }
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(6), entity -> ((entity instanceof PlayerEntity && !((PlayerEntity)entity).getAbilities().creativeMode)) || (entity instanceof IronGolemEntity));
        }
        @Override
        public boolean shouldContinue() {
            return !getTargets().isEmpty();
        }
        @Override
        public void stop() {
            super.stop();
        }
        @Override
        public void start() {
            super.start();
            InvokerEntity.this.tpcooldown = 180;
        }

        @Override
        protected void castSpell() {
            TeleportUtil teleportUtil = new TeleportUtil();
            double x = sorcerer.getX();
            double y = sorcerer.getY()+1;
            double z = sorcerer.getZ();
            if (sorcerer.world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, x, y, z, 30, 0.3D, 0.5D, 0.3D, 0.015D);
            }
            teleportUtil.doRandomTeleport(InvokerEntity.this);
        }

        @Override
        protected int getInitialCooldown() {
            return 30;
        }

        @Override
        protected int getSpellTicks() {
            return 30;
        }

        @Override
        protected int startTimeDelay() {
            return 400;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.INVOKER_TELEPORT_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.BLINDNESS;
        }
    }
    public class ConjureAoeFangsGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {

        @Override
        public boolean canStart() {
            if (InvokerEntity.this.getTarget() == null) {
                return false;
            }
            if (getTargets().isEmpty()) {
                return false;
            }
            if (InvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (InvokerEntity.this.fangaoecooldown < 0) {
                return true;
            }
            return false;
        }
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(18), entity -> !(entity instanceof HostileEntity));
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = new BlockPos(x, y, z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = InvokerEntity.this.world.getBlockState(blockPos2 = blockPos.down())).isSideSolidFullSquare(InvokerEntity.this.world, blockPos2, Direction.UP))
                    continue;
                if (!InvokerEntity.this.world.isAir(blockPos) && !(voxelShape = (blockState2 = InvokerEntity.this.world.getBlockState(blockPos)).getCollisionShape(InvokerEntity.this.world, blockPos)).isEmpty()) {
                    d = voxelShape.getMax(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
            if (bl) {
                InvokerEntity.this.world.spawnEntity(new InvokerFangsEntity(InvokerEntity.this.world, x, (double)blockPos.getY()+0.2 + d, z, yaw, warmup + 4, InvokerEntity.this));
            }
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        protected void castSpell() {
            for (LivingEntity livingEntity : getTargets()) {
                double d = Math.min(livingEntity.getY(), InvokerEntity.this.getY());
                double e = Math.max(livingEntity.getY(), InvokerEntity.this.getY()) + 1.0;
                float f = (float) MathHelper.atan2(livingEntity.getZ() - InvokerEntity.this.getZ(), livingEntity.getX() - InvokerEntity.this.getX());
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float) i * (float) Math.PI * 0.4f;
                    this.conjureFangs(livingEntity.getX() + (double) MathHelper.cos(g) * 1.5, livingEntity.getZ() + (double) MathHelper.sin(g) * 1.5, d, e, g, 0);
                }
            }
            InvokerEntity.this.fangaoecooldown = 100;
        }

        @Override
        protected int getSpellTicks() {
            return 40;
        }

        @Override
        protected int startTimeDelay() {
            return 100;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.INVOKER_FANGS_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.FANGS;
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return PolymerEntityUtils.createPlayerSpawnPacket(this);
    }

    @Override
    public Property getSkin() {
        return EntitySkins.INVOKER;
    }
}

