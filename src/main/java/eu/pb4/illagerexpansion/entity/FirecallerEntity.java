package eu.pb4.illagerexpansion.entity;


import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.illagerexpansion.entity.projectile.MagmaEntity;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirecallerEntity extends SpellcastingIllagerEntity implements PlayerPolymerEntity {
    @Nullable
    private SheepEntity wololoTarget;
    private int cooldown = 160;
    private int aoecooldown = 300;
    private AttributeContainer attributeContainer;

    public FirecallerEntity(final EntityType<? extends FirecallerEntity> entityType, final World world) {
        super(entityType, world);
        this.experiencePoints = 15;
        this.onCreated(this);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(4, new ConjureSkullGoal());
        this.goalSelector.add(3, new AreaDamageGoal());
        this.goalSelector.add(5, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>(this, IronGolemEntity.class, false));

    }

    public static DefaultAttributeContainer.Builder createFirecallerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 32.0).add(EntityAttributes.MOVEMENT_SPEED, 0.38);
    }

    public void readCustomData(final ReadView nbt) {
        super.readCustomData(nbt);
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    public void writeCustomData(final WriteView nbt) {
        super.writeCustomData(nbt);
    }

    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        --this.cooldown;
        --this.aoecooldown;
    }

    public boolean damage(ServerWorld world, final DamageSource source, final float amount) {
        final boolean bl2 = super.damage(world, source, amount);
        return bl2;
    }

    public boolean isInSameTeam(final Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isInSameTeam(other)) {
            return true;
        }
        if (other instanceof VexEntity) {
            return this.isTeammate(((VexEntity) other).getOwner());
        }
        return other instanceof LivingEntity && ((LivingEntity) other).getType().isIn(EntityTypeTags.ILLAGER) && this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
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

    public void addBonusForWave(ServerWorld world, final int wave, final boolean unused) {
    }

    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return IllagerEntity.State.SPELLCASTING;
        }
        return IllagerEntity.State.CROSSED;
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
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayerEntity player) {
        PlayerPolymerEntity.super.getPolymerVisibleEquipment(items, player);
        items.removeIf(x -> x.getFirst() == EquipmentSlot.MAINHAND);
        items.add(new Pair<>(EquipmentSlot.MAINHAND, Items.STICK.getDefaultStack()));
        return items;
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
            return getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(5), entity -> (entity instanceof PlayerEntity) || (entity instanceof IronGolemEntity));
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
            if (getWorld() instanceof ServerWorld world) {
                world.spawnParticles(ParticleTypes.FLAME, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
                world.spawnParticles(ParticleTypes.LARGE_SMOKE, FirecallerEntity.this.getX(), FirecallerEntity.this.getY() + 2.5, FirecallerEntity.this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.05D);
            }
            super.tick();
        }

        private void shootSkullAt(LivingEntity target) {
            this.shootSkullAt(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
        }

        private void shootSkullAt(double targetX, double targetY, double targetZ) {
            double d = FirecallerEntity.this.getX();
            double e = FirecallerEntity.this.getY() + 2.5;
            double f = FirecallerEntity.this.getZ();
            double g = targetX - d;
            double h = targetY - e;
            double i = targetZ - f;
            MagmaEntity Magma = new MagmaEntity(FirecallerEntity.this.getWorld(), FirecallerEntity.this, g, h, i);
            Magma.setOwner(FirecallerEntity.this);
            Magma.setPos(d, e, f);
            FirecallerEntity.this.getWorld().spawnEntity(Magma);
        }

        @Override
        protected void castSpell() {
            this.shootSkullAt(FirecallerEntity.this.getTarget());
            if (getWorld() instanceof ServerWorld) {
                double x = FirecallerEntity.this.getX();
                double y = FirecallerEntity.this.getY() + 2.5;
                double z = FirecallerEntity.this.getZ();
                ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.SMOKE, x, y, z, 40, 0.4D, 0.4D, 0.4D, 0.15D);
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
            return FirecallerEntity.this.aoecooldown <= 0;
        }

        private List<LivingEntity> getTargets() {
            return getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(6), entity -> !(entity instanceof IllagerEntity) && !(entity instanceof SurrenderedEntity) && !(entity instanceof RavagerEntity));
        }


        @Override
        public void stop() {
            super.stop();
        }

        private void buff(LivingEntity entity) {
            entity.addVelocity(0.0f, 1.2f, 0.0f);
            entity.serverDamage(getDamageSources().magic(), 6.0f);
            entity.setFireTicks(120);
            double x = entity.getX();
            double y = entity.getY() + 1;
            double z = entity.getZ();
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.SMOKE, x, y + 1, z, 10, 0.2D, 0.2D, 0.2D, 0.015D);
            BlockPos blockPos = entity.getBlockPos();
            FirecallerEntity.this.getWorld().setBlockState(blockPos, Blocks.FIRE.getDefaultState());
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
            return ClassTinkerers.getEnum(Spell.class, "IE_PROVOKE");
        }
    }
}

