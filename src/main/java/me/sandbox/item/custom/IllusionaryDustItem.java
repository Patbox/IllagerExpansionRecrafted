package me.sandbox.item.custom;

import me.sandbox.poly.PolymerAutoItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class IllusionaryDustItem extends Item implements PolymerAutoItem {
    public IllusionaryDustItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        double x = playerEntity.getX();
        double y = playerEntity.getY();
        double z = playerEntity.getZ();
        world.playSound(x, y, z, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticles(ParticleTypes.CLOUD, x, y + 1, z, 15, 0.5D, 0.5D, 0.5D, 0.15D);
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200));
            playerEntity.getItemCooldownManager().set(this, 100);
            if (!playerEntity.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
        }
        return TypedActionResult.success(itemStack);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.GLOWSTONE_DUST;
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }
}
