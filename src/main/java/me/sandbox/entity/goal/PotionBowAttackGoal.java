package me.sandbox.entity.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PotionBowAttackGoal<T extends HostileEntity> extends Goal {
    private final MobEntity mob;
    private final RangedAttackMob owner;
    private final double mobSpeed;
    private final int minIntervalTicks;
    private final int maxIntervalTicks;
    private final float maxShootRange;
    private final float squaredMaxShootRange;
    private final T actor;
    private final double speed;
    private final float squaredRange;
    @Nullable
    private LivingEntity target;
    private int updateCountdownTicks;
    private int seenTargetTicks;
    private int attackInterval;
    private int cooldown;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks;

    public PotionBowAttackGoal(final T actor, final double speed, final int attackInterval, final float range) {
        this.updateCountdownTicks = -1;
        this.cooldown = -1;
        this.combatTicks = -1;
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.owner = (RangedAttackMob) actor;
        this.mob = actor;
        this.mobSpeed = speed;
        this.minIntervalTicks = attackInterval;
        this.maxIntervalTicks = attackInterval;
        this.maxShootRange = range;
        this.squaredMaxShootRange = this.maxShootRange * this.maxShootRange;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public void setAttackInterval(final int attackInterval) {
        this.attackInterval = attackInterval;
    }

    public boolean canStart() {
        return this.actor.getTarget() != null && this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.actor.isHolding(Items.BOW) || this.actor.isHolding(Items.LINGERING_POTION);
    }

    public boolean shouldContinue() {
        return (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isHoldingBow();
    }

    public void start() {
        super.start();
        this.actor.setAttacking(true);
    }

    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.actor.clearActiveItem();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        final LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        if (this.actor.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.LINGERING_POTION)) {
            final double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            final boolean bl = this.mob.getVisibilityCache().canSee(livingEntity);
            this.seenTargetTicks = (bl ? (++this.seenTargetTicks) : 0);
            if (d > this.squaredMaxShootRange || this.seenTargetTicks < 5) {
                this.mob.getNavigation().startMovingTo(livingEntity, this.mobSpeed);
            } else {
                this.mob.getNavigation().stop();
            }
            this.mob.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
            final int updateCountdownTicks = this.updateCountdownTicks - 1;
            this.updateCountdownTicks = updateCountdownTicks;
            if (updateCountdownTicks == 0) {
                if (!bl) {
                    return;
                }
                final float f = (float) Math.sqrt(d) / this.maxShootRange;
                final float g = MathHelper.clamp(f, 0.1f, 1.0f);
                this.owner.shootAt(livingEntity, g);
                this.updateCountdownTicks = MathHelper.floor(f * (this.maxIntervalTicks - this.minIntervalTicks) + this.minIntervalTicks);
            } else if (this.updateCountdownTicks < 0) {
                this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(d) / this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks));
            }
        }
        if (this.actor.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.BOW)) {
            final double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            final boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
            final boolean bl3;
            final boolean bl2 = bl3 = (this.targetSeeingTicker > 0);
            if (bl != bl2) {
                this.targetSeeingTicker = 0;
            }
            this.targetSeeingTicker = (bl ? (++this.targetSeeingTicker) : (--this.targetSeeingTicker));
            if (d > this.squaredRange || this.targetSeeingTicker < 20) {
                this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
                this.combatTicks = -1;
            } else {
                this.actor.getNavigation().stop();
                ++this.combatTicks;
            }
            if (this.combatTicks >= 20) {
                if (this.actor.getRandom().nextFloat() < 0.3) {
                    boolean movingToLeft = false;
                    if (this.movingToLeft) {
                        movingToLeft = false;
                    }
                    this.movingToLeft = movingToLeft;
                }
                if (this.actor.getRandom().nextFloat() < 0.3) {
                    this.backward = !this.backward;
                }
                this.combatTicks = 0;
            }
            if (this.combatTicks > -1) {
                if (d > this.squaredRange * 0.75f) {
                    this.backward = false;
                } else if (d < this.squaredRange * 0.25f) {
                    this.backward = true;
                }
                this.actor.getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
                this.actor.lookAtEntity(livingEntity, 30.0f, 30.0f);
            } else {
                this.actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
            }
            if (this.actor.isUsingItem()) {
                if (!bl && this.targetSeeingTicker < -60) {
                    this.actor.clearActiveItem();
                } else {
                    final int i;
                    if (bl && (i = this.actor.getItemUseTime()) >= 20) {
                        this.actor.clearActiveItem();
                        ((RangedAttackMob) this.actor).shootAt(livingEntity, BowItem.getPullProgress(i));
                        this.cooldown = this.attackInterval;
                    }
                }
            } else {
                final int cooldown = this.cooldown - 1;
                this.cooldown = cooldown;
                if (cooldown <= 0 && this.targetSeeingTicker >= -60 && this.actor.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.BOW)) {
                    this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
                }
            }
        }
    }
}
