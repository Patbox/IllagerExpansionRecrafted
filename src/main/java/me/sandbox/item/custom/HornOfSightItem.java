package me.sandbox.item.custom;

import me.sandbox.poly.PolymerAutoItem;
import me.sandbox.sounds.SoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HornOfSightItem extends Item implements PolymerAutoItem {

    public HornOfSightItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i;
        float f;
        if (!(user instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }
        if ((double)(f = BowItem.getPullProgress(i = this.getMaxUseTime(stack) - remainingUseTicks)) < 0.1) {
            return;
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        playerEntity.getItemCooldownManager().set(this, 30);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 60;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPYGLASS;
    }

    private List<LivingEntity> getTargets(PlayerEntity user) {
        return user.getWorld().getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(30), entity -> (entity instanceof LivingEntity) && !(entity instanceof PlayerEntity));
    }
    private void glow(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 400, 0));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        world.playSound(null, user.getX(), user.getEyeY(), user.getZ(), SoundRegistry.HORN_OF_SIGHT, user.getSoundCategory(), 1.0f, 1.0f);
        ItemStack itemStack = user.getStackInHand(hand);
        getTargets(user).forEach(this::glow);
        user.setCurrentHand(hand);
        user.getItemCooldownManager().set(this, 80);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.GOAT_HORN;
    }
}

