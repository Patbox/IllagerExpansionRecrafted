package me.sandbox.item.custom;

import me.sandbox.mixin.CreeperEntityAccessor;
import me.sandbox.poly.PolymerAutoItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlatinumSwordItem extends SwordItem implements PolymerAutoItem {
    public PlatinumSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static void applyEffects(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof PhantomEntity) {
            target.setVelocity(target.getBoundingBox().getCenter().subtract(attacker.getBoundingBox().getCenter()).normalize().multiply(-0.8));
        } else if (target instanceof CreeperEntity creeperEntity) {
            ((CreeperEntityAccessor) creeperEntity).setCurrentFuseTime(Math.max(((CreeperEntityAccessor) creeperEntity).getCurrentFuseTime() - 10, 5));
        }

        for (var s : List.copyOf(target.getStatusEffects())) {
            target.removeStatusEffect(s.getEffectType());
            target.addStatusEffect(new StatusEffectInstance(s.getEffectType(), (int) Math.round(s.getDuration() * 0.8), s.getAmplifier(),
                    s.isAmbient(), s.shouldShowParticles(), s.shouldShowIcon(), null, s.getFactorCalculationData()));
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.NETHERITE_SWORD;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }
}
