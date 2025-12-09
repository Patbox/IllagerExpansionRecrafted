package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.mixin.CreeperEntityAccessor;
import eu.pb4.illagerexpansion.mixin.ZombieVillagerEntityAccessor;
import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class PlatinumSwordItem extends Item implements PolymerAutoItem {
    public PlatinumSwordItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(settings.sword(toolMaterial, attackDamage, attackSpeed));
    }

    public static void applyEffects(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (var s : List.copyOf(target.getActiveEffects())) {
            target.removeEffect(s.getEffect());
            target.addEffect(new MobEffectInstance(s.getEffect(), (int) Math.round(s.getDuration() * 0.8), s.getAmplifier(),
                    s.isAmbient(), s.isVisible(), s.showIcon(), null));
        }

        if (target instanceof Phantom) {
            target.setDeltaMovement(target.getBoundingBox().getCenter().subtract(attacker.getBoundingBox().getCenter()).normalize().scale(-0.8));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40));
        } else if (target instanceof Creeper creeperEntity) {
            ((CreeperEntityAccessor) creeperEntity).setSwell(Math.max(((CreeperEntityAccessor) creeperEntity).getSwell() - 10, 5));
        } else if (target instanceof ZombieVillager villagerEntity && villagerEntity.isConverting()) {
            ((ZombieVillagerEntityAccessor) villagerEntity).setVillagerConversionTime(((ZombieVillagerEntityAccessor) villagerEntity).getVillagerConversionTime() - 5);
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_SWORD;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }
}
