package eu.pb4.illagerexpansion.entity.goal;

import eu.pb4.illagerexpansion.entity.MarauderEntity;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class HatchetAttackGoal
        extends ProjectileAttackGoal {
    private final HostileEntity hostile;
    public static final UniformIntProvider COOLDOWN_RANGE = TimeHelper.betweenSeconds(1, 2);
    private double speed;
    private int attackInterval;
    private final float squaredRange;
    private int seeingTargetTicker;
    private int cooldown = -1;
    private int chargeTime = 0;

    public HatchetAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
        super(mob, mobSpeed, intervalTicks, maxShootRange);
        this.hostile = (MarauderEntity)mob;
        this.speed = mobSpeed;
        this.attackInterval = intervalTicks;
        this.squaredRange = maxShootRange*maxShootRange;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.hostile.getMainHandStack().isOf(ItemRegistry.HATCHET);
    }

    @Override
    public void start() {
        super.start();
        this.hostile.setAttacking(true);
        this.hostile.setCurrentHand(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
        super.stop();
        this.hostile.clearActiveItem();
        this.hostile.setAttacking(false);
        this.seeingTargetTicker = 0;
    }

    @Override
    public void tick() {
        boolean bl3;
        boolean bl2;
        LivingEntity target = hostile.getTarget();
        if (target == null) {
            chargeTime = 0;
            if(hostile instanceof MarauderEntity) {
                ((MarauderEntity) hostile).setCharging(false);
            }
            return;
        }
        boolean canSeeTarget = hostile.getVisibilityCache().canSee(target);
        boolean bl = ((MobEntity)this.hostile).getVisibilityCache().canSee(target);
        this.hostile.getLookControl().lookAt(target, 30.0f, 30.0f);
        boolean bl4 = bl2 = this.seeingTargetTicker > 0;
        if (bl != bl2) {
            this.seeingTargetTicker = 0;
        }
        this.seeingTargetTicker = bl ? ++this.seeingTargetTicker : --this.seeingTargetTicker;
        double d = ((Entity)this.hostile).squaredDistanceTo(target);
        boolean bl5 = bl3 = (d > (double)this.squaredRange || this.seeingTargetTicker < 5);
        if (bl3) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                this.hostile.getNavigation().startMovingTo(target, speed);
                this.cooldown = COOLDOWN_RANGE.get(this.hostile.getRandom());
            }
        } else {
            this.cooldown = 0;
            ((MobEntity)this.hostile).getNavigation().stop();
        }
        --chargeTime;
        if (hostile instanceof MarauderEntity) {
            if (chargeTime == -40) {
                ((MarauderEntity)hostile).setCharging(true);
            }
            if (chargeTime == -80) {
                ((MarauderEntity)hostile).shootAt(target, 1.0f);
                ((MarauderEntity)hostile).setCharging(false);
                chargeTime = 0;
            }
        }
    }
}

