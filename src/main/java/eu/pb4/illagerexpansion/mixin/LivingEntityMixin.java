package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @ModifyVariable(method = "applyArmorToDamage", at = @At("HEAD"), argsOnly = true)
    private float changeDamage(float amount, DamageSource source) {
        if (!source.isOf(DamageTypes.MAGIC) && !source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return amount;
        }

        float mult = 1;

        for (var armor : ArmorItem.Type.values()) {
            if (this.getEquippedStack(armor.getEquipmentSlot()).isIn(ItemRegistry.MAGIC_DAMAGE_BLOCKING_ARMOR)) {
                mult -= 0.08f;
            }
        }

        return amount * mult;
    }
}
