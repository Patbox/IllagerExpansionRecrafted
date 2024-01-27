package eu.pb4.illagerexpansion.mixin;


import eu.pb4.illagerexpansion.entity.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WoodlandMansionGenerator.Piece.class)
public class WoodlandMansionPiecesMixin {
    @Inject(at = @At("HEAD"), cancellable = true, method = "handleMetadata")
    public void handleDataMarker(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo ci) {
        Random randomValue = Random.create();
        int value = randomValue.nextInt(8);
        if (metadata.equals("Provoker")) {
            ProvokerEntity provoker;
            provoker = EntityRegistry.PROVOKER.create(world.toServerWorld());
            provoker.setPersistent();
            provoker.refreshPositionAndAngles(pos, 0.0f, 0.0f);
            provoker.initialize(world, world.getLocalDifficulty(provoker.getBlockPos()), SpawnReason.STRUCTURE, null, null);
            world.spawnEntityAndPassengers(provoker);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        int value2 = randomValue.nextInt(2);
        if (metadata.equals("Warrior") && value2 == 1) {
            BasherEntity basher;
            basher = EntityRegistry.BASHER.create(world.toServerWorld());
            basher.setPersistent();
            basher.refreshPositionAndAngles(pos, 0.0f, 0.0f);
            basher.initialize(world, world.getLocalDifficulty(basher.getBlockPos()), SpawnReason.STRUCTURE, null, null);
            world.spawnEntityAndPassengers(basher);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        if (metadata.equals("Archivist")) {
            ArchivistEntity archivist;
            archivist = EntityRegistry.ARCHIVIST.create(world.toServerWorld());
            archivist.setPersistent();
            archivist.refreshPositionAndAngles(pos, 0.0f, 0.0f);
            archivist.initialize(world, world.getLocalDifficulty(archivist.getBlockPos()), SpawnReason.STRUCTURE, null, null);
            world.spawnEntityAndPassengers(archivist);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        if (metadata.equals("invoker")) {
            InvokerEntity archivist;
            archivist = EntityRegistry.INVOKER.create(world.toServerWorld());
            archivist.setPersistent();
            archivist.refreshPositionAndAngles(pos, 0.0f, 0.0f);
            archivist.initialize(world, world.getLocalDifficulty(archivist.getBlockPos()), SpawnReason.STRUCTURE, null, null);
            world.spawnEntityAndPassengers(archivist);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
    }
}