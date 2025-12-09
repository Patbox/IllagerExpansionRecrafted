package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @ModifyVariable(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), argsOnly = true)
    private float changeDamage(float amount, DamageSource source) {
        if (!source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.INDIRECT_MAGIC)) {
            return amount;
        }

        float mult = 1;

        for (var armor : ArmorType.values()) {
            if (this.getItemBySlot(armor.getSlot()).is(ItemRegistry.MAGIC_DAMAGE_BLOCKING_ARMOR)) {
                mult -= 0.08f;
            }
        }

        return amount * mult;
    }
}
