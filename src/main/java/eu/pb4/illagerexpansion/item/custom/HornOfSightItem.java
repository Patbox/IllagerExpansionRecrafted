package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HornOfSightItem extends Item implements PolymerAutoItem {

    public HornOfSightItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player playerEntity)) {
            return false;
        }
        ItemStack itemStack = playerEntity.getProjectile(stack);
        if (itemStack.isEmpty()) {
            return false;
        }
        if ((double) (BowItem.getPowerForTime(this.getUseDuration(stack, user) - remainingUseTicks)) < 0.1) {
            return false;
        }
        playerEntity.awardStat(Stats.ITEM_USED.get(this));
        playerEntity.getCooldowns().addCooldown(stack, 30);
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 60;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }

    private List<LivingEntity> getTargets(Player user) {
        return user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(30), entity -> (entity instanceof LivingEntity) && !(entity instanceof Player));
    }

    private void glow(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400, 0));
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        world.playSound(null, user.getX(), user.getEyeY(), user.getZ(), SoundRegistry.HORN_OF_SIGHT, user.getSoundSource(), 1.0f, 1.0f);
        ItemStack itemStack = user.getItemInHand(hand);
        getTargets(user).forEach(this::glow);
        user.startUsingItem(hand);
        user.getCooldowns().addCooldown(itemStack, 80);
        return InteractionResult.CONSUME;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.GOAT_HORN;
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayer player) {
        return false;
    }
}

