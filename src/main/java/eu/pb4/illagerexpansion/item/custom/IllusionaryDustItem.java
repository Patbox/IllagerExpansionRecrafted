package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;


public class IllusionaryDustItem extends Item implements PolymerAutoItem {
    public IllusionaryDustItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        double x = playerEntity.getX();
        double y = playerEntity.getY();
        double z = playerEntity.getZ();
        world.playSound(null, x, y, z, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0f, 1.0f);
        if (world instanceof ServerLevel) {
            ((ServerLevel) world).sendParticles(ParticleTypes.CLOUD, x, y + 1, z, 15, 0.5D, 0.5D, 0.5D, 0.15D);
            playerEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
            playerEntity.addEffect(new MobEffectInstance(MobEffects.SPEED, 200));
            playerEntity.getCooldowns().addCooldown(itemStack, 100);
            if (!playerEntity.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.GLOWSTONE_DUST;
    }

    @Override
    public boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayer player) {
        return false;
    }
}
