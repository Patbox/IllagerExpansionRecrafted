package me.sandbox.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import me.sandbox.poly.EntitySkins;
import me.sandbox.poly.PlayerPolymerEntity;
import me.sandbox.sounds.SoundRegistry;
import me.sandbox.util.spellutil.SetMagicFireUtil;
import me.sandbox.util.spellutil.TeleportUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SorcererEntity
        extends SpellcastingIllagerEntity implements PlayerPolymerEntity {
    @Nullable
    private SheepEntity wololoTarget;
    private int cooldown;
    private int flamecooldown;
    private boolean offenseSpell;

    public SorcererEntity(EntityType<? extends SorcererEntity> entityType, World world) {
        super((EntityType<? extends SpellcastingIllagerEntity>)entityType, world);
        this.experiencePoints = 10;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SorcererEntity.LookAtTargetOrWololoTarget());
        this.goalSelector.add(4, new CastTeleportGoal());
        this.goalSelector.add(3, new ConjureFlamesGoal());
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(5, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity)this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, false));
    }
    private AttributeContainer attributeContainer;
    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 26.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38D)
                    .build());
        }
        return attributeContainer;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundRegistry.SORCERER_CELEBRATE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        --cooldown;
        --flamecooldown;
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
        if (other instanceof VexEntity) {
            return this.isTeammate(((VexEntity)other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SORCERER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SORCERER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.SORCERER_HURT;
    }


    @Nullable
    SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundRegistry.SORCERER_COMPLETE_CAST;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }
    @Override
    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return IllagerEntity.State.SPELLCASTING;
        }
        return IllagerEntity.State.CROSSED;
    }

    class LookAtTargetOrWololoTarget
            extends SpellcastingIllagerEntity.LookAtTargetGoal {

        @Override
        public void tick() {
            if (SorcererEntity.this.getTarget() != null) {
                SorcererEntity.this.getLookControl().lookAt(SorcererEntity.this.getTarget(), SorcererEntity.this.getMaxHeadRotation(), SorcererEntity.this.getMaxLookPitchChange());
            } else if (SorcererEntity.this.getWololoTarget() != null) {
                SorcererEntity.this.getLookControl().lookAt(SorcererEntity.this.getWololoTarget(), SorcererEntity.this.getMaxHeadRotation(), SorcererEntity.this.getMaxLookPitchChange());
            }
        }
    }

    public class CastTeleportGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        SorcererEntity sorcerer = SorcererEntity.this;
        @Override
        public boolean canStart() {
            if (SorcererEntity.this.getTarget() == null) {
                return false;
            }
            if (SorcererEntity.this.isSpellcasting()) {
                return false;
            }
            if (SorcererEntity.this.cooldown < 0 && !(getTargets().isEmpty())) {
                return true;
            }
            return false;
        }
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(8), entity -> (entity instanceof PlayerEntity) || (entity instanceof IronGolemEntity));
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
        protected void castSpell() {
            TeleportUtil teleportUtil = new TeleportUtil();
            SorcererEntity.this.cooldown = 220;
            double x = sorcerer.getX();
            double y = sorcerer.getY()+1;
            double z = sorcerer.getZ();
            if (sorcerer.world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.WITCH, x, y, z, 30, 0.3D, 0.5D, 0.3D, 0.015D);
            }
            teleportUtil.doRandomTeleport(SorcererEntity.this);
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
            return SoundRegistry.SORCERER_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return ClassTinkerers.getEnum(Spell.class, "CONJURE_TELEPORT");
        }
    }
    public class ConjureFlamesGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {

        @Override
        public boolean canStart() {
            if (SorcererEntity.this.getTarget() == null) {
                return false;
            }
            if (SorcererEntity.this.isSpellcasting()) {
                return false;
            }
            if (SorcererEntity.this.flamecooldown < 0) {
                offenseSpell = true;
                return true;
            }
            return false;
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        protected void castSpell() {
            SetMagicFireUtil setMagicFireUtil = new SetMagicFireUtil();
            LivingEntity target = SorcererEntity.this.getTarget();
            setMagicFireUtil.setFire(target, SorcererEntity.this.world);
            SorcererEntity.this.flamecooldown = 100;
            offenseSpell = false;
            target.damage(DamageSource.MAGIC, 3.0f);
            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, target.getX(), target.getY()+1, target.getZ(), 30, 0.3D, 0.5D, 0.3D, 0.08D);
            }
        }

        @Override
        protected int getInitialCooldown() {
            return 60;
        }

        @Override
        protected int getSpellTicks() {
            return 60;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.SORCERER_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return ClassTinkerers.getEnum(Spell.class, "CONJURE_FLAMES");
        }
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
        return EntitySkins.SORCERER;
    }
}
