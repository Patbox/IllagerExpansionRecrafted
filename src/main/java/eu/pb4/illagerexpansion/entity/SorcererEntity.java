package eu.pb4.illagerexpansion.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.util.spellutil.SetMagicFireUtil;
import eu.pb4.illagerexpansion.util.spellutil.TeleportUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SorcererEntity extends SpellcasterIllager implements PlayerPolymerEntity {
    @Nullable
    private Sheep wololoTarget;
    private int cooldown;
    private int flamecooldown;
    private boolean offenseSpell;
    private AttributeMap attributeContainer;

    public SorcererEntity(EntityType<? extends SorcererEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 10;
        this.onCreated(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SorcererEntity.LookAtTargetOrWololoTarget());
        this.goalSelector.addGoal(4, new CastTeleportGoal());
        this.goalSelector.addGoal(3, new ConjureFlamesGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<Player>(this, Player.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createSorcererAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D);
    }


    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundRegistry.SORCERER_CELEBRATE;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        --cooldown;
        --flamecooldown;
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
        if (other instanceof Vex) {
            return this.isAlliedTo(((Vex) other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity) other).getType().is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
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


    @Nullable Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundRegistry.SORCERER_COMPLETE_CAST;
    }

    @Override
    public void applyRaidBuffs(ServerLevel world, int wave, boolean unused) {

    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
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
        return EntitySkins.SORCERER;
    }

    class LookAtTargetOrWololoTarget extends SpellcasterIllager.SpellcasterCastingSpellGoal {

        @Override
        public void tick() {
            if (SorcererEntity.this.getTarget() != null) {
                SorcererEntity.this.getLookControl().setLookAt(SorcererEntity.this.getTarget(), SorcererEntity.this.getMaxHeadYRot(), SorcererEntity.this.getMaxHeadXRot());
            } else if (SorcererEntity.this.getWololoTarget() != null) {
                SorcererEntity.this.getLookControl().setLookAt(SorcererEntity.this.getWololoTarget(), SorcererEntity.this.getMaxHeadYRot(), SorcererEntity.this.getMaxHeadXRot());
            }
        }
    }

    public class CastTeleportGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        SorcererEntity sorcerer = SorcererEntity.this;

        @Override
        public boolean canUse() {
            if (SorcererEntity.this.getTarget() == null) {
                return false;
            }
            if (SorcererEntity.this.isCastingSpell()) {
                return false;
            }
            return SorcererEntity.this.cooldown < 0 && !(getTargets().isEmpty());
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8), entity -> (entity instanceof Player) || (entity instanceof IronGolem));
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
        protected void performSpellCasting() {
            TeleportUtil teleportUtil = new TeleportUtil();
            SorcererEntity.this.cooldown = 220;
            double x = sorcerer.getX();
            double y = sorcerer.getY() + 1;
            double z = sorcerer.getZ();
            if (sorcerer.level() instanceof ServerLevel) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.WITCH, x, y, z, 30, 0.3D, 0.5D, 0.3D, 0.015D);
            }
            teleportUtil.doRandomTeleport(SorcererEntity.this);
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
            return 400;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.SORCERER_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return ClassTinkerers.getEnum(IllagerSpell.class, "IE_CONJURE_TELEPORT");
        }
    }

    public class ConjureFlamesGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (SorcererEntity.this.getTarget() == null) {
                return false;
            }
            if (SorcererEntity.this.isCastingSpell()) {
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
        protected void performSpellCasting() {
            SetMagicFireUtil setMagicFireUtil = new SetMagicFireUtil();
            LivingEntity target = SorcererEntity.this.getTarget();
            setMagicFireUtil.setFire(target, SorcererEntity.this.level());
            SorcererEntity.this.flamecooldown = 100;
            offenseSpell = false;
            target.hurt(SorcererEntity.this.damageSources().magic(), 3.0f);
            if (level() instanceof ServerLevel) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 1, target.getZ(), 30, 0.3D, 0.5D, 0.3D, 0.08D);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 60;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.SORCERER_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return ClassTinkerers.getEnum(IllagerSpell.class, "IE_CONJURE_FLAMES");
        }
    }
}
