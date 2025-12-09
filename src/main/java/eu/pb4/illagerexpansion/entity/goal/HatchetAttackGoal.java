package eu.pb4.illagerexpansion.entity.goal;

import eu.pb4.illagerexpansion.entity.MarauderEntity;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class HatchetAttackGoal
        extends RangedAttackGoal {
    private final Monster hostile;
    public static final UniformInt COOLDOWN_RANGE = TimeUtil.rangeOfSeconds(1, 2);
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
    public boolean canUse() {
        return super.canUse() && this.hostile.getMainHandItem().is(ItemRegistry.HATCHET);
    }

    @Override
    public void start() {
        super.start();
        this.hostile.setAggressive(true);
        this.hostile.startUsingItem(InteractionHand.MAIN_HAND);
    }

    @Override
    public void stop() {
        super.stop();
        this.hostile.stopUsingItem();
        this.hostile.setAggressive(false);
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
        boolean canSeeTarget = hostile.getSensing().hasLineOfSight(target);
        boolean bl = ((Mob)this.hostile).getSensing().hasLineOfSight(target);
        this.hostile.getLookControl().setLookAt(target, 30.0f, 30.0f);
        boolean bl4 = bl2 = this.seeingTargetTicker > 0;
        if (bl != bl2) {
            this.seeingTargetTicker = 0;
        }
        this.seeingTargetTicker = bl ? ++this.seeingTargetTicker : --this.seeingTargetTicker;
        double d = ((Entity)this.hostile).distanceToSqr(target);
        boolean bl5 = bl3 = (d > (double)this.squaredRange || this.seeingTargetTicker < 5);
        if (bl3) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                this.hostile.getNavigation().moveTo(target, speed);
                this.cooldown = COOLDOWN_RANGE.sample(this.hostile.getRandom());
            }
        } else {
            this.cooldown = 0;
            ((Mob)this.hostile).getNavigation().stop();
        }
        --chargeTime;
        if (hostile instanceof MarauderEntity) {
            if (chargeTime == -40) {
                ((MarauderEntity)hostile).setCharging(true);
            }
            if (chargeTime == -80) {
                ((MarauderEntity)hostile).performRangedAttack(target, 1.0f);
                ((MarauderEntity)hostile).setCharging(false);
                chargeTime = 0;
            }
        }
    }
}

