package eu.pb4.illagerexpansion.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.illagerexpansion.poly.EntitySkins;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.util.spellutil.EnchantToolUtil;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArchivistEntity extends SpellcasterIllager implements PlayerPolymerEntity {
    public float bookanimation;
    public float bookanimation1;
    @Nullable
    private Sheep wololoTarget;
    private AbstractIllager enchantTarget;
    private int cooldown;
    private final int damagedelay = 60;
    private boolean isLevitating;
    private int buffcooldown;
    private AttributeMap attributeContainer;

    public ArchivistEntity(EntityType<? extends ArchivistEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 10;
        this.onCreated(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ArchivistEntity.LookAtTargetOrWololoTarget());
        this.goalSelector.addGoal(4, new LevitateTargetsGoal());
        this.goalSelector.addGoal(3, new EnchantAllyGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<Player>(this, Player.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, false));
    }

    public static AttributeSupplier.Builder createArchivistAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 22.0).add(Attributes.MOVEMENT_SPEED, 0.36);
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundRegistry.ARCHIVIST_AMBIENT;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }


    @Override
    protected void customServerAiStep(ServerLevel world) {
        --cooldown;
        --buffcooldown;
        super.customServerAiStep(world);
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
        if (other instanceof LivingEntity && ((LivingEntity) other).is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.ARCHIVIST_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.ARCHIVIST_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.ARCHIVIST_HURT;
    }

    @Nullable Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    void setWololoTarget(@Nullable Sheep sheep) {
        this.wololoTarget = sheep;
    }

    @Nullable AbstractIllager getEnchantTarget() {
        return this.enchantTarget;
    }

    void setEnchantTarget(@Nullable AbstractIllager entity) {
        this.enchantTarget = entity;
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
        return EntitySkins.ARCHIVIST;
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayer player) {
        PlayerPolymerEntity.super.getPolymerVisibleEquipment(items, player);
        items.removeIf(x -> x.getFirst() == EquipmentSlot.MAINHAND);
        items.add(new Pair<>(EquipmentSlot.MAINHAND, Items.WRITTEN_BOOK.getDefaultInstance()));
        return items;
    }

    class LookAtTargetOrWololoTarget extends SpellcasterIllager.SpellcasterCastingSpellGoal {

        @Override
        public void tick() {
            if (ArchivistEntity.this.getTarget() != null) {
                ArchivistEntity.this.getLookControl().setLookAt(ArchivistEntity.this.getTarget(), ArchivistEntity.this.getMaxHeadYRot(), ArchivistEntity.this.getMaxHeadXRot());
            } else if (ArchivistEntity.this.getWololoTarget() != null) {
                ArchivistEntity.this.getLookControl().setLookAt(ArchivistEntity.this.getWololoTarget(), ArchivistEntity.this.getMaxHeadYRot(), ArchivistEntity.this.getMaxHeadXRot());
            }
        }
    }

    public class LevitateTargetsGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (ArchivistEntity.this.getTarget() == null) {
                return false;
            }
            return ArchivistEntity.this.cooldown < 0 && getTargets().stream().anyMatch(entity -> !(entity instanceof Monster));
        }

        private void knockBack(Entity entity) {
            double d = entity.getX() - ArchivistEntity.this.getX();
            double e = entity.getZ() - ArchivistEntity.this.getZ();
            double f = Math.max(d * d + e * e, 0.001);
            entity.push(d / f * 5.0, 0.6, e / f * 5.0);
        }

        protected void knockback(LivingEntity target) {
            this.knockBack(target);
            target.hurtMarked = true;
        }

        private List<LivingEntity> getTargets() {
            return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), entity -> !(entity instanceof Monster));
        }

        @Override
        public void stop() {
            super.stop();
        }

        private void buff(LivingEntity entity) {
            knockback(entity);
            entity.hurt(damageSources().magic(), 4.0f);
        }

        @Override
        protected void performSpellCasting() {
            ArchivistEntity.this.cooldown = 220;
            getTargets().forEach(this::buff);
            double x = ArchivistEntity.this.getX();
            double y = ArchivistEntity.this.getY() + 1;
            double z = ArchivistEntity.this.getZ();
            ((ServerLevel) level()).sendParticles(ParticleTypes.ENCHANT, x, y, z, 150, 3.0D, 3.0D, 3.0D, 0.1D);
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
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }

    public class EnchantAllyGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions closeEnchantableMobPredicate = TargetingConditions.forNonCombat().range(16.0).selector((livingEntity, world) -> !(livingEntity instanceof ArchivistEntity));
        EnchantToolUtil enchantToolUtil = new EnchantToolUtil();
        private int targetId;

        public boolean canEnchant() {
            AbstractIllager hostileEntity = ArchivistEntity.this.getEnchantTarget();
            if (hostileEntity == null) {
                return false;
            }
            return enchantToolUtil.eligibleForEnchant(hostileEntity);
        }

        @Override
        public boolean canUse() {
            if (ArchivistEntity.this.getTarget() == null) {
                return false;
            }
            if (ArchivistEntity.this.buffcooldown >= 0) {
                return false;
            }
            if (ArchivistEntity.this.isCastingSpell()) {
                return false;
            }
            List<AbstractIllager> list = ((ServerLevel) ArchivistEntity.this.level()).getNearbyEntities(AbstractIllager.class, this.closeEnchantableMobPredicate, ArchivistEntity.this, ArchivistEntity.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            ArchivistEntity.this.setEnchantTarget(list.get(ArchivistEntity.this.random.nextInt(list.size())));
            AbstractIllager hostileEntity = ArchivistEntity.this.getEnchantTarget();
            if (hostileEntity.getId() == this.targetId) {
                return false;
            }
            return this.canEnchant();
        }

        @Override
        public void stop() {
            super.stop();
            ArchivistEntity.this.setEnchantTarget(null);
        }

        @Override
        protected void performSpellCasting() {
            Monster hostileEntity = ArchivistEntity.this.getEnchantTarget();
            if (hostileEntity != null) {
                this.targetId = hostileEntity.getId();
            }
            enchantToolUtil.enchant(hostileEntity);
            double x = hostileEntity.getX();
            double y = hostileEntity.getY() + 1.5;
            double z = hostileEntity.getZ();
            if (level() instanceof ServerLevel) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.ENCHANT, x, y, z, 50, 1.0D, 2.0D, 1.0D, 0.1D);
            }
            ArchivistEntity.this.buffcooldown = 300;
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
            return SoundRegistry.SORCERER_CAST;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return ClassTinkerers.getEnum(IllagerSpell.class, "IE_ENCHANT");
        }
    }
}

