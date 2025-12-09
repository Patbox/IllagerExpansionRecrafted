package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.entity.projectile.HatchetEntity;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class HatchetItem extends Item implements PolymerAutoItem {
    public HatchetItem(Item.Properties settings) {
        super(settings.repairable(ItemRegistry.PLATINUM_CHUNK)
                .enchantable(1)
                .component(DataComponents.WEAPON, new Weapon(1, 2))
                .component(DataComponents.TOOL, new Tool(List.of(), 1, 2, false))
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -1.9f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build())

        );
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player playerEntity)) {
            return false;
        }
        int i = this.getUseDuration(stack, user) - remainingUseTicks;
        if (i < 10) {
            return false;
        }
        stack.hurtAndBreak(1, playerEntity, EquipmentSlot.MAINHAND);
        HatchetEntity hatchetentity = new HatchetEntity(world, playerEntity, stack);
        hatchetentity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0f, 1.0f + 0.5f, 1.0f);
        if (playerEntity.getAbilities().instabuild) {
            hatchetentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        world.addFreshEntity(hatchetentity);
        world.playSound(null, hatchetentity, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        if (!playerEntity.getAbilities().instabuild) {
            playerEntity.getInventory().removeItem(stack);
        }
        playerEntity.awardStat(Stats.ITEM_USED.get(this));
        return false;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
            return InteractionResult.FAIL;
        }
        if (EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH) && !user.isInWaterOrRain()) {
            return InteractionResult.FAIL;
        }
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        if ((double) state.getDestroySpeed(world, pos) != 0.0) {
            stack.hurtAndBreak(2, miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext  context) {
        return Items.IRON_AXE;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        var out = PolymerAutoItem.super.getPolymerItemStack(itemStack, tooltipType, context);
        if (!out.has(DataComponents.CONSUMABLE)) {
            out.set(DataComponents.CONSUMABLE, new Consumable(
                    99999999999999999f, getUseAnimation(itemStack), BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), false, List.of()
                    ));
        }
        return out;
    }
}
