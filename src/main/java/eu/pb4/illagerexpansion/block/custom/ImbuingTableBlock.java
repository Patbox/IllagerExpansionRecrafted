package eu.pb4.illagerexpansion.block.custom;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import eu.pb4.illagerexpansion.gui.ImbuingTableGui;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class ImbuingTableBlock extends Block implements PolymerHeadBlock {
    public ImbuingTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (canActivate(pos, world)) {
            new ImbuingTableGui((ServerPlayer) player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    public boolean canActivate(BlockPos pos, Level world) {
        int i = 0;
        BlockPos blockPos = pos.below();
        for (BlockPos blockradius : BlockPos.withinManhattan(blockPos, 1, 0, 1)) {
            Block blockInRadius = world.getBlockState(blockradius).getBlock();
            if (goodBlock(blockInRadius)) {
                i++;
            }
            if (i == 9) {
                return true;
            }
        }
        return false;
    }
        private boolean goodBlock(Block block) {
            return block == Blocks.COPPER_BLOCK || block == Blocks.CUT_COPPER || block == Blocks.WAXED_COPPER_BLOCK || block == Blocks.WAXED_CUT_COPPER;
        }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (canActivate(pos, world)) {
            world.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY()+0.8, pos.getZ() + 0.5, 3, 0.7D, 0.3D, 0.7D, 0.05);
        }
        world.scheduleTick(pos, this, 5);
    }
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        world.scheduleTick(pos, this, 5);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY1NjgyODY5NjAzMiwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hYzE4YjFiN2NlM2Y3YmNiZmUxM2UzZWY5NTY1NTk5OWQ5MWY3OTNjMDFmYWFlOTI0ZDI3ZjlmZTY0NjA5MjAxIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0";
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.PLAYER_HEAD.defaultBlockState();
    }
}
