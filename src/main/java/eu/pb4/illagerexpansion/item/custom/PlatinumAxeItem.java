package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PlatinumAxeItem extends AxeItem implements PolymerAutoItem {
    public PlatinumAxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.NETHERITE_AXE;
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlatinumSwordItem.applyEffects(stack, target, attacker);
        return super.postHit(stack, target, attacker);
    }
}
