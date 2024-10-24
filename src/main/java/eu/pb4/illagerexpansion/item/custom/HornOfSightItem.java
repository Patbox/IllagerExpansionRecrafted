package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class HornOfSightItem extends Item implements PolymerAutoItem {

    public HornOfSightItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return false;
        }
        if ((double) (BowItem.getPullProgress(this.getMaxUseTime(stack, user) - remainingUseTicks)) < 0.1) {
            return false;
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        playerEntity.getItemCooldownManager().set(stack, 30);
        return false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
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
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        world.playSound(null, user.getX(), user.getEyeY(), user.getZ(), SoundRegistry.HORN_OF_SIGHT, user.getSoundCategory(), 1.0f, 1.0f);
        ItemStack itemStack = user.getStackInHand(hand);
        getTargets(user).forEach(this::glow);
        user.setCurrentHand(hand);
        user.getItemCooldownManager().set(itemStack, 80);
        return ActionResult.CONSUME;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.GOAT_HORN;
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }
}

