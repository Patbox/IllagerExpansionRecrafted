package eu.pb4.illagerexpansion.block.custom;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.illagerexpansion.gui.ImbuingTableGui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class ImbuingTableBlock extends Block implements PolymerHeadBlock {
    public ImbuingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (canActivate(pos, world)) {
            new ImbuingTableGui((ServerPlayerEntity) player);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    public boolean canActivate(BlockPos pos, World world) {
        int i = 0;
        BlockPos blockPos = pos.down();
        for (BlockPos blockradius : BlockPos.iterateOutwards(blockPos, 1, 0, 1)) {
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canActivate(pos, world)) {
            world.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY()+0.8, pos.getZ() + 0.5, 3, 0.7D, 0.3D, 0.7D, 0.05);
        }
        world.scheduleBlockTick(pos, this, 5);
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, 5);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY1NjgyODY5NjAzMiwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hYzE4YjFiN2NlM2Y3YmNiZmUxM2UzZWY5NTY1NTk5OWQ5MWY3OTNjMDFmYWFlOTI0ZDI3ZjlmZTY0NjA5MjAxIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0";
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.PLAYER_HEAD.getDefaultState();
    }
}
