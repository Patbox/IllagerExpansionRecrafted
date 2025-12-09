package eu.pb4.illagerexpansion.entity;


import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.illagerexpansion.entity.projectile.MagmaEntity;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirecallerEntity extends SpellcasterIllager implements PlayerPolymerEntity {
    @Nullable
    private Sheep wololoTarget;
    private int cooldown = 160;
    private int aoecooldown = 300;
    private AttributeMap attributeContainer;

    public FirecallerEntity(final EntityType<? extends FirecallerEntity> entityType, final Level world) {
        super(entityType, world);
        this.xpReward = 15;
        this.onCreated(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.addGoal(4, new ConjureSkullGoal());
        this.goalSelector.addGoal(3, new AreaDamageGoal());
        this.goalSelector.addGoal(5, new AvoidEntityGoal<Player>(this, Player.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, false));

    }

    public static AttributeSupplier.Builder createFirecallerAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 32.0).add(Attributes.MOVEMENT_SPEED, 0.38);
    }

    public void readAdditionalSaveData(final ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    public void addAdditionalSaveData(final ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }

    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        --this.cooldown;
        --this.aoecooldown;
    }

    public boolean hurtServer(ServerLevel world, final DamageSource source, final float amount) {
        final boolean bl2 = super.hurtServer(world, source, amount);
        return bl2;
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
        return SoundRegistry.FIRECALLER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.FIRECALLER_DEATH;
    }

    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundRegistry.FIRECALLER_HURT;
    }

    @Nullable
    Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.FIRECHARGE_USE;
    }

    public void applyRaidBuffs(ServerLevel world, final int wave, final boolean unused) {
    }

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
        return EntitySkins.FIRECALLER;
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayer player) {
        PlayerPolymerEntity.super.getPolymerVisibleEquipment(items, player);
        items.removeIf(x -> x.getFirst() == EquipmentSlot.MAINHAND);
        items.add(new Pair<>(EquipmentSlot.MAINHAND, Items.STICK.getDefaultInstance()));
        return items;
    }

    class LookAtTargetOrWololoTarget
            extends SpellcasterIllager.SpellcasterCastingSpellGoal {

        @Override
        public void tick() {
            if (FirecallerEntity.this.getTarget() != null) {
                FirecallerEntity.this.getLookControl().setLookAt(FirecallerEntity.this.getTarget(), FirecallerEntity.this.getMaxHeadYRot(), FirecallerEntity.this.getMaxHeadXRot());
            } else if (FirecallerEntity.this.getWololoTarget() != null) {
                FirecallerEntity.this.getLookControl().setLookAt(FirecallerEntity.this.getWololoTarget(), FirecallerEntity.this.getMaxHeadYRot(), FirecallerEntity.this.getMaxHeadXRot());
            }
        }
    }

    public class ConjureSkullGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5), entity -> (entity instanceof Player) || (entity instanceof IronGolem));
        }

        @Override
        public boolean canUse() {
            if (FirecallerEntity.this.getTarget() == null) {
                return false;
            }
            if (FirecallerEntity.this.cooldown > 0) {
                return false;
            }
            return FirecallerEntity.this.cooldown < 0 && !FirecallerEntity.this.isCastingSpell() && getTargets().isEmpty();
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        public void tick() {
            if (level() instanceof ServerLevel world) {
                world.sendParticles(ParticleTypes.FLAME, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
                world.sendParticles(ParticleTypes.LARGE_SMOKE, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
            }
            super.tick();
        }

        private void shootSkullAt(LivingEntity target) {
            this.shootSkullAt(target.getX(), target.getY() + (double) target.getEyeHeight() * 0.5, target.getZ());
        }

        private void shootSkullAt(double targetX, double targetY, double targetZ) {
            double d = FirecallerEntity.this.getX();
            double e = FirecallerEntity.this.getY() + 2.5;
            double f = FirecallerEntity.this.getZ();
            double g = targetX - d;
            double h = targetY - e;
            double i = targetZ - f;
            MagmaEntity Magma = new MagmaEntity(FirecallerEntity.this.level(), FirecallerEntity.this, g, h, i);
            Magma.setOwner(FirecallerEntity.this);
            Magma.setPosRaw(d, e, f);
            FirecallerEntity.this.level().addFreshEntity(Magma);
        }

        @Override
        protected void performSpellCasting() {
            this.shootSkullAt(FirecallerEntity.this.getTarget());
            if (level() instanceof ServerLevel) {
                double x = FirecallerEntity.this.getX();
                double y = FirecallerEntity.this.getY() + 2.5;
                double z = FirecallerEntity.this.getZ();
                ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, x, y, z, 40, 0.4D, 0.4D, 0.4D, 0.15D);
            }
            FirecallerEntity.this.cooldown = 160;
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
            return 400;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.FIRECALLER_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.WOLOLO;
        }
    }

    public class AreaDamageGoal
            extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (FirecallerEntity.this.getTarget() == null) {
                return false;
            }
            if (FirecallerEntity.this.isCastingSpell()) {
                return false;
            }
            return FirecallerEntity.this.aoecooldown <= 0;
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), entity -> !(entity instanceof AbstractIllager) && !(entity instanceof SurrenderedEntity) && !(entity instanceof Ravager));
        }


        @Override
        public void stop() {
            super.stop();
        }

        private void buff(LivingEntity entity) {
            entity.push(0.0f, 1.2f, 0.0f);
            entity.hurt(damageSources().magic(), 6.0f);
            entity.setRemainingFireTicks(120);
            double x = entity.getX();
            double y = entity.getY() + 1;
            double z = entity.getZ();
            ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, x, y + 1, z, 10, 0.2D, 0.2D, 0.2D, 0.015D);
            BlockPos blockPos = entity.blockPosition();
            FirecallerEntity.this.level().setBlockAndUpdate(blockPos, Blocks.FIRE.defaultBlockState());
        }

        @Override
        protected void performSpellCasting() {
            getTargets().forEach(this::buff);
            FirecallerEntity.this.aoecooldown = 300;
        }

        @Override
        protected int getCastWarmupTime() {
            return 50;
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 400;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundRegistry.FIRECALLER_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return ClassTinkerers.getEnum(IllagerSpell.class, "IE_PROVOKE");
        }
    }
}

