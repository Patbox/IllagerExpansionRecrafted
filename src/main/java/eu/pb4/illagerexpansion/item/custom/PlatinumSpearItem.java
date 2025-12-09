package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import xyz.nucleoid.packettweaker.PacketContext;

public class PlatinumSpearItem extends Item implements PolymerAutoItem {
    public PlatinumSpearItem(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_SPEAR;
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        super.postHit(stack, target, attacker);
    }
}
