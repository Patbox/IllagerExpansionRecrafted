package eu.pb4.illagerexpansion.entity;

import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.util.spellutil.SpellParticleUtil;
import eu.pb4.illagerexpansion.util.spellutil.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InvokerEntity
        extends SpellcasterIllager implements PlayerPolymerEntity {
    private static final EntityDataAccessor<Boolean> SHIELDED = SynchedEntityData.defineId(InvokerEntity.class, EntityDataSerializers.BOOLEAN);
    private final ServerBossEvent bossBar = (ServerBossEvent) new ServerBossEvent(this.uuid, this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
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
    @Nullable
    private Sheep wololoTarget;
    private AttributeMap attributeContainer;

    public InvokerEntity(EntityType<? extends InvokerEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 80;
        this.onCreated(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Player>(this, Player.class, 8.0f, 0.7, 1.1));
        this.goalSelector.addGoal(5, new AreaDamageGoal());
        this.goalSelector.addGoal(4, new CastTeleportGoal());
        this.goalSelector.addGoal(5, new SummonVexGoal());
        this.goalSelector.addGoal(5, new ConjureAoeFangsGoal());
        this.goalSelector.addGoal(6, new ConjureFangsGoal());
        this.goalSelector.addGoal(6, new WololoGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 5.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createInvokerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!super.doHurtTarget(world, target)) {
            return false;
        }
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0), this);
        }
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHIELDED, false);
    }

    @Override
    public void applyRaidBuffs(ServerLevel world, int wave, boolean unused) {

    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setShieldedState(nbt.getBooleanOr("Invul", false));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        nbt.putBoolean("Invul", this.isShielded);
        super.addAdditionalSaveData(nbt);
    }

    public boolean getShieldedState() {
        return this.entityData.get(SHIELDED);
    }

    public void setShieldedState(boolean isShielded) {
        this.entityData.set(SHIELDED, isShielded);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        --this.tpcooldown;
        --this.cooldown;
        --this.fangaoecooldown;
        super.customServerAiStep(world);
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (isAoeCasting && this.isCastingSpell()) {
            SpellParticleUtil spellParticleUtil = new SpellParticleUtil();
            spellParticleUtil.setSpellParticles(this, this.level(), ParticleTypes.SMOKE, 2, 0.06D);
        }
        if (damagecount >= 2) {
            this.setShieldedState(true);
        }
        if (getShieldedState()) {
            if (world instanceof ServerLevel) {
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.5, this.getZ(), 1, 0.5D, 0.7D, 0.5D, 0.15D);
            }
        }
        Vec3 vec3d = this.getDeltaMovement();
        if (!this.onGround() && vec3d.y < 0.0) {
            this.setDeltaMovement(vec3d.multiply(1.0, 0.6, 1.0));
        }
        if (world instanceof ServerLevel) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.3, this.getZ(), 1, 0.2D, 0.2D, 0.2D, 0.005D);
        }
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
        this.onTrackingStopped(player);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean considersEntityAsAlly(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.considersEntityAsAlly(other)) {
            return true;
        }
        if (other instanceof SurrenderedEntity) {
            return this.isAlliedTo(((SurrenderedEntity) other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
        }
        return false;
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }
        if (this.isCastingSpell()) {
            return IllagerArmPose.SPELLCASTING;
        }
        if (this.isCelebrating()) {
            return IllagerArmPose.CELEBRATING;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if ((source.getDirectEntity()) instanceof AbstractArrow) {
            if (getShieldedState()) {
                return false;
            } else {
                damagecount++;
            }
        }
        if (!((source.getDirectEntity()) instanceof AbstractArrow)) {
            if (getShieldedState()) {
                if ((source.is(DamageTypeTags.IS_FIRE))) {
                    return false;
                } else {
                    if (this.level() instanceof ServerLevel) {
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1, this.getZ(), 30, 0.5D, 0.7D, 0.5D, 0.5D);
                    }
                    this.playSound(SoundRegistry.INVOKER_SHIELD_BREAK, 1.0f, 1.0f);
                    this.setShieldedState(false);
                    damagecount = 0;
                }
            }
        }

        boolean bl2 = super.hurtServer(world, source, amount);
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

    @Nullable
    Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    void setWololoTarget(@Nullable Sheep sheep) {
        this.wololoTarget = sheep;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundRegistry.INVOKER_COMPLETE_CAST;
    }

    @Override
    public Property getSkin() {
        return EntitySkins.INVOKER;
    }

    class LookAtTargetOrWololoTarget
            extends SpellcasterIllager.SpellcasterCastingSpellGoal {
        LookAtTargetOrWololoTarget() {
        }

        @Override
        public void tick() {
            if (InvokerEntity.this.getTarget() != null) {
                InvokerEntity.this.getLookControl().setLookAt(InvokerEntity.this.getTarget(), InvokerEntity.this.getMaxHeadYRot(), InvokerEntity.this.getMaxHeadXRot());
            } else if (InvokerEntity.this.getWololoTarget() != null) {
                InvokerEntity.this.getLookControl().setLookAt(InvokerEntity.this.getWololoTarget(), InvokerEntity.this.getMaxHeadYRot(), InvokerEntity.this.getMaxHeadXRot());
            }
        }
    }

    class SummonVexGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions closeVexPredicate = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();

        SummonVexGoal() {
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            if (inSecondPhase) {
                return false;
            }
            int i = ((ServerLevel) InvokerEntity.this.level()).getNearbyEntities(SurrenderedEntity.class, this.closeVexPredicate, InvokerEntity.this, InvokerEntity.this.getBoundingBox().inflate(20.0)).size();
            return 3 > i;
        }

        @Override
        protected int getCastingTime() {
            return 80;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Override
        protected void performSpellCasting() {
            ServerLevel serverWorld = (ServerLevel) InvokerEntity.this.level();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos = InvokerEntity.this.blockPosition().offset(-2 + InvokerEntity.this.random.nextInt(5), 1, -2 + InvokerEntity.this.random.nextInt(5));
                SurrenderedEntity surrenderedEntity = EntityRegistry.SURRENDERED.create(InvokerEntity.this.level(), EntitySpawnReason.MOB_SUMMONED);
                surrenderedEntity.snapTo(blockPos, 0.0f, 0.0f);
                surrenderedEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(blockPos), EntitySpawnReason.MOB_SUMMONED, null);
                surrenderedEntity.setOwner(InvokerEntity.this);
                surrenderedEntity.setBounds(blockPos);
                surrenderedEntity.setLifeTicks(20 * (30 + InvokerEntity.this.random.nextInt(90)));
                serverWorld.addFreshEntityWithPassengers(surrenderedEntity);
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.INVOKER_SUMMON_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }

    class ConjureFangsGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {
        ConjureFangsGoal() {
        }

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            return !inSecondPhase;
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return 80;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity livingEntity = InvokerEntity.this.getTarget();
            double d = Math.min(livingEntity.getY(), InvokerEntity.this.getY());
            double e = Math.max(livingEntity.getY(), InvokerEntity.this.getY()) + 1.0;
            float f = (float) Mth.atan2(livingEntity.getZ() - InvokerEntity.this.getZ(), livingEntity.getX() - InvokerEntity.this.getX());

            if (InvokerEntity.this.distanceToSqr(livingEntity) < 9.0) {
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float) i * (float) Math.PI * 0.4f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(g) * 1.5, InvokerEntity.this.getZ() + (double) Mth.sin(g) * 1.5, d, e, g, 0);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float) i * (float) Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(g) * 2.5, InvokerEntity.this.getZ() + (double) Mth.sin(g) * 2.5, d, e, g, 3);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float) i * (float) Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(g) * 3.5, InvokerEntity.this.getZ() + (double) Mth.sin(g) * 2.5, d, e, g, 3);
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(f) * h, InvokerEntity.this.getZ() + (double) Mth.sin(f) * h, d, e, f, j);
                }
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(f + 0.4f) * h, InvokerEntity.this.getZ() + (double) Mth.sin(f + 0.3f) * h, d, e, f, j);
                }
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double) (i + 1);
                    int j = i;
                    this.conjureFangs(InvokerEntity.this.getX() + (double) Mth.cos(f - 0.4f) * h, InvokerEntity.this.getZ() + (double) Mth.sin(f - 0.3f) * h, d, e, f, j);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = BlockPos.containing(x, y, z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = InvokerEntity.this.level().getBlockState(blockPos2 = blockPos.below())).isFaceSturdy(InvokerEntity.this.level(), blockPos2, Direction.UP))
                    continue;
                if (!InvokerEntity.this.level().isEmptyBlock(blockPos) && !(voxelShape = (blockState2 = InvokerEntity.this.level().getBlockState(blockPos)).getCollisionShape(InvokerEntity.this.level(), blockPos)).isEmpty()) {
                    d = voxelShape.max(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.below()).getY() >= Mth.floor(maxY) - 1);
            if (bl) {
                InvokerEntity.this.level().addFreshEntity(new InvokerFangsEntity(InvokerEntity.this.level(), x, (double) blockPos.getY() + 0.2 + d, z, yaw, warmup, InvokerEntity.this));
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.INVOKER_FANGS_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

    public class WololoGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions convertibleSheepPredicate = TargetingConditions.forNonCombat().range(16.0).selector((livingEntity, world) -> ((Sheep) livingEntity).getColor() == DyeColor.BLUE);

        @Override
        public boolean canUse() {
            if (InvokerEntity.this.getTarget() != null) {
                return false;
            }
            if (InvokerEntity.this.isCastingSpell()) {
                return false;
            }
            if (InvokerEntity.this.tickCount < this.nextAttackTickCount) {
                return false;
            }
            if (!((ServerLevel) InvokerEntity.this.level()).getGameRules().get(GameRules.MOB_GRIEFING)) {
                return false;
            }
            List<Sheep> list = ((ServerLevel) InvokerEntity.this.level()).getNearbyEntities(Sheep.class, this.convertibleSheepPredicate, InvokerEntity.this, InvokerEntity.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            InvokerEntity.this.setWololoTarget(list.get(InvokerEntity.this.random.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return InvokerEntity.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }

        @Override
        public void stop() {
            super.stop();
            InvokerEntity.this.setWololoTarget(null);
        }

        @Override
        protected void performSpellCasting() {
            Sheep sheepEntity = InvokerEntity.this.getWololoTarget();
            if (sheepEntity != null && sheepEntity.isAlive()) {
                sheepEntity.setColor(DyeColor.RED);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 140;
        }

        @Override
        protected int getCastingInterval() {
            return 600;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.WOLOLO;
        }
    }

    public class AreaDamageGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
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
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), entity -> !(entity instanceof AbstractIllager) && !(entity instanceof SurrenderedEntity) && !(entity instanceof Ravager));
        }

        private void knockBack(Entity entity) {
            double d = entity.getX() - InvokerEntity.this.getX();
            double e = entity.getZ() - InvokerEntity.this.getZ();
            double f = Math.max(d * d + e * e, 0.001);
            entity.push(d / f * 6, 0.65, e / f * 6);
        }

        protected void knockback(LivingEntity target) {
            this.knockBack(target);
            target.hurtMarked = true;
        }


        @Override
        public void stop() {
            isAoeCasting = false;
            super.stop();
        }

        private void buff(LivingEntity entity) {
            this.knockback(entity);
            entity.hurt(damageSources().magic(), 11.0f);
            double x = entity.getX();
            double y = entity.getY() + 1;
            double z = entity.getZ();
            ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, x, y + 1, z, 10, 0.2D, 0.2D, 0.2D, 0.015D);
        }

        @Override
        protected void performSpellCasting() {
            InvokerEntity.this.cooldown = 300;
            getTargets().forEach(this::buff);
            isAoeCasting = false;
            double posx = InvokerEntity.this.getX();
            double posy = InvokerEntity.this.getY();
            double posz = InvokerEntity.this.getZ();
            ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, posx, posy + 1, posz, 350, 1.0D, 0.8D, 1.0D, 0.3D);
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 360;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.INVOKER_BIG_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.BLINDNESS;
        }
    }

    public class CastTeleportGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {
        InvokerEntity sorcerer = InvokerEntity.this;

        @Override
        public boolean canUse() {
            if (InvokerEntity.this.getTarget() == null) {
                return false;
            }
            if (InvokerEntity.this.isCastingSpell()) {
                return false;
            }
            return InvokerEntity.this.tpcooldown < 0 && !(getTargets().isEmpty());
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), entity -> ((entity instanceof Player && !((Player) entity).getAbilities().instabuild)) || (entity instanceof IronGolem));
        }

        @Override
        public boolean canContinueToUse() {
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
        protected void performSpellCasting() {
            TeleportUtil teleportUtil = new TeleportUtil();
            double x = sorcerer.getX();
            double y = sorcerer.getY() + 1;
            double z = sorcerer.getZ();
            if (sorcerer.level() instanceof ServerLevel) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, x, y, z, 30, 0.3D, 0.5D, 0.3D, 0.015D);
            }
            teleportUtil.doRandomTeleport(InvokerEntity.this);
        }

        @Override
        protected int getCastWarmupTime() {
            return 30;
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return 360;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.INVOKER_TELEPORT_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.BLINDNESS;
        }
    }

    public class ConjureAoeFangsGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (InvokerEntity.this.getTarget() == null) {
                return false;
            }
            if (getTargets().isEmpty()) {
                return false;
            }
            if (InvokerEntity.this.isCastingSpell()) {
                return false;
            }
            return InvokerEntity.this.fangaoecooldown < 0;
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(18), entity -> !(entity instanceof Monster));
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = BlockPos.containing(x, y, z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = InvokerEntity.this.level().getBlockState(blockPos2 = blockPos.below())).isFaceSturdy(InvokerEntity.this.level(), blockPos2, Direction.UP))
                    continue;
                if (!InvokerEntity.this.level().isEmptyBlock(blockPos) && !(voxelShape = (blockState2 = InvokerEntity.this.level().getBlockState(blockPos)).getCollisionShape(InvokerEntity.this.level(), blockPos)).isEmpty()) {
                    d = voxelShape.max(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.below()).getY() >= Mth.floor(maxY) - 1);
            if (bl) {
                InvokerEntity.this.level().addFreshEntity(new InvokerFangsEntity(InvokerEntity.this.level(), x, (double) blockPos.getY() + 0.2 + d, z, yaw, warmup + 4, InvokerEntity.this));
            }
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
            for (LivingEntity livingEntity : getTargets()) {
                double d = Math.min(livingEntity.getY(), InvokerEntity.this.getY());
                double e = Math.max(livingEntity.getY(), InvokerEntity.this.getY()) + 1.0;
                float f = (float) Mth.atan2(livingEntity.getZ() - InvokerEntity.this.getZ(), livingEntity.getX() - InvokerEntity.this.getX());
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float) i * (float) Math.PI * 0.4f;
                    this.conjureFangs(livingEntity.getX() + (double) Mth.cos(g) * 1.5, livingEntity.getZ() + (double) Mth.sin(g) * 1.5, d, e, g, 0);
                }
            }
            InvokerEntity.this.fangaoecooldown = 100;
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.INVOKER_FANGS_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }
}

