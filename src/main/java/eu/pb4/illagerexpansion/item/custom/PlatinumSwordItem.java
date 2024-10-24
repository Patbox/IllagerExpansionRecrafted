package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.mixin.CreeperEntityAccessor;
import eu.pb4.illagerexpansion.mixin.ZombieVillagerEntityAccessor;
import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class PlatinumSwordItem extends SwordItem implements PolymerAutoItem {
    public PlatinumSwordItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static void applyEffects(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (var s : List.copyOf(target.getStatusEffects())) {
            target.removeStatusEffect(s.getEffectType());
            target.addStatusEffect(new StatusEffectInstance(s.getEffectType(), (int) Math.round(s.getDuration() * 0.8), s.getAmplifier(),
                    s.isAmbient(), s.shouldShowParticles(), s.shouldShowIcon(), null));
        }

        if (target instanceof PhantomEntity) {
            target.setVelocity(target.getBoundingBox().getCenter().subtract(attacker.getBoundingBox().getCenter()).normalize().multiply(-0.8));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40));
        } else if (target instanceof CreeperEntity creeperEntity) {
            ((CreeperEntityAccessor) creeperEntity).setCurrentFuseTime(Math.max(((CreeperEntityAccessor) creeperEntity).getCurrentFuseTime() - 10, 5));
        } else if (target instanceof ZombieVillagerEntity villagerEntity && villagerEntity.isConverting()) {
            ((ZombieVillagerEntityAccessor) villagerEntity).setConversionTimer(((ZombieVillagerEntityAccessor) villagerEntity).getConversionTimer() - 5);
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_SWORD;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        return super.postHit(stack, target, attacker);
    }
}
