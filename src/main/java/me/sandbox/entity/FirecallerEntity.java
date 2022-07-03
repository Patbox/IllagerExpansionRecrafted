package me.sandbox.entity;


import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import me.sandbox.entity.projectile.MagmaEntity;
import me.sandbox.poly.EntitySkins;
import me.sandbox.poly.PlayerPolymerEntity;
import me.sandbox.sounds.SoundRegistry;
import net.minecraft.block.Blocks;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FirecallerEntity extends SpellcastingIllagerEntity implements PlayerPolymerEntity
{
    @Nullable
    private SheepEntity wololoTarget;
    private int cooldown = 160;
    private int aoecooldown = 300;
    private AttributeContainer attributeContainer;

    public FirecallerEntity(final EntityType<? extends FirecallerEntity> entityType, final World world) {
        super(entityType, world);
        this.experiencePoints = 15;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal((MobEntity)this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(4, new ConjureSkullGoal());
        this.goalSelector.add(3, new AreaDamageGoal());
        this.goalSelector.add(5, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(8, new WanderAroundGoal((PathAwareEntity)this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity)this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, false));

    }

    public AttributeContainer getAttributes() {
        if (this.attributeContainer == null) {
            this.attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38).build());
        }
        return this.attributeContainer;
    }

    protected void initDataTracker() {
        super.initDataTracker();
    }


    public void readCustomDataFromNbt(final NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    public void writeCustomDataToNbt(final NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    protected void mobTick() {
        super.mobTick();
        --this.cooldown;
        --this.aoecooldown;
    }

    public boolean damage(final DamageSource source, final float amount) {
        final boolean bl2 = super.damage(source, amount);
        return bl2;
    }

    public boolean isTeammate(final Entity other) {
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
            return this.isTeammate((Entity)((VexEntity)other).getOwner());
        }
        return other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
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
    SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ITEM_FIRECHARGE_USE;
    }

    public void addBonusForWave(final int wave, final boolean unused) {
    }

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
            if (FirecallerEntity.this.getTarget() != null) {
                FirecallerEntity.this.getLookControl().lookAt(FirecallerEntity.this.getTarget(), FirecallerEntity.this.getMaxHeadRotation(), FirecallerEntity.this.getMaxLookPitchChange());
            } else if (FirecallerEntity.this.getWololoTarget() != null) {
                FirecallerEntity.this.getLookControl().lookAt(FirecallerEntity.this.getWololoTarget(), FirecallerEntity.this.getMaxHeadRotation(), FirecallerEntity.this.getMaxLookPitchChange());
            }
        }
    }

    public class ConjureSkullGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(5), entity -> (entity instanceof PlayerEntity) || (entity instanceof IronGolemEntity));
        }

        @Override
        public boolean canStart() {
            if (FirecallerEntity.this.getTarget() == null) {
                return false;
            }
            if (FirecallerEntity.this.cooldown > 0) {
                return false;
            }
            return FirecallerEntity.this.cooldown < 0 && !FirecallerEntity.this.isSpellcasting() && getTargets().isEmpty();
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        public void tick() {
            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
                ((ServerWorld) world).spawnParticles(ParticleTypes.LARGE_SMOKE, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
            }
            super.tick();
        }

        private void shootSkullAt(LivingEntity target) {
            this.shootSkullAt(target.getX(), target.getY() + (double)target.getStandingEyeHeight() * 0.5, target.getZ());
        }

        private void shootSkullAt(double targetX, double targetY, double targetZ) {
            double d = FirecallerEntity.this.getX();
            double e = FirecallerEntity.this.getY()+2.5;
            double f = FirecallerEntity.this.getZ();
            double g = targetX - d;
            double h = targetY - e;
            double i = targetZ - f;
            MagmaEntity Magma = new MagmaEntity(FirecallerEntity.this.world, FirecallerEntity.this, g, h, i);
            Magma.setOwner(FirecallerEntity.this);
            Magma.setPos(d, e, f);
            FirecallerEntity.this.world.spawnEntity(Magma);
        }

        @Override
        protected void castSpell() {
            this.shootSkullAt(FirecallerEntity.this.getTarget());
            if (world instanceof ServerWorld) {
                double x = FirecallerEntity.this.getX();
                double y = FirecallerEntity.this.getY()+2.5;
                double z = FirecallerEntity.this.getZ();
                ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, x, y, z, 40, 0.4D, 0.4D, 0.4D, 0.15D);
            }
            FirecallerEntity.this.cooldown = 160;
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
            return 400;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundRegistry.FIRECALLER_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.WOLOLO;
        }
    }
    public class AreaDamageGoal
            extends SpellcastingIllagerEntity.CastSpellGoal {

        @Override
        public boolean canStart() {
            if (FirecallerEntity.this.getTarget() == null) {
                return false;
            }
            if (FirecallerEntity.this.isSpellcasting()) {
                return false;
            }
            if (FirecallerEntity.this.aoecooldown <= 0) {
                return true;
            }
            return false;
        }
        private List<LivingEntity> getTargets() {
            return world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(6), entity -> !(entity instanceof IllagerEntity) && !(entity instanceof SurrenderedEntity) && !(entity instanceof RavagerEntity));
        }


        @Override
        public void stop() {
            super.stop();
        }
        private void buff(LivingEntity entity) {
            entity.addVelocity(0.0f, 1.2f, 0.0f);
            entity.damage(DamageSource.MAGIC, 6.0f);
            entity.setFireTicks(120);
            double x = entity.getX();
            double y = entity.getY()+1;
            double z = entity.getZ();
            ((ServerWorld)world).spawnParticles(ParticleTypes.SMOKE,x, y+1,z,10,0.2D, 0.2D,0.2D,0.015D);
            BlockPos blockPos = entity.getBlockPos();
            FirecallerEntity.this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
        }

        @Override
        protected void castSpell() {
            getTargets().forEach(this::buff);
            FirecallerEntity.this.aoecooldown = 300;
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
            return SoundRegistry.FIRECALLER_CAST;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return ClassTinkerers.getEnum(Spell.class, "PROVOKE");
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
        return EntitySkins.FIRECALLER;
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items) {
        items.removeIf(x -> x.getFirst() == EquipmentSlot.MAINHAND);
        items.add(new Pair<>(EquipmentSlot.MAINHAND, Items.STICK.getDefaultStack()));
        return items;
    }
}

