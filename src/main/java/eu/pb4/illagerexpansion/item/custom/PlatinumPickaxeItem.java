package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class PlatinumPickaxeItem extends Item implements PolymerAutoItem {
    public PlatinumPickaxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties settings) {
        super(settings.pickaxe(material, attackDamage, attackSpeed));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_PICKAXE;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
    }
}
