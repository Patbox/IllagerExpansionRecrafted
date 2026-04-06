package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class PlatinumSpearItem extends Item implements PolymerAutoItem {
    public PlatinumSpearItem(Properties settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_SPEAR;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }
}
