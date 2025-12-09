package eu.pb4.illagerexpansion.mixin;


import eu.pb4.illagerexpansion.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WoodlandMansionPieces.WoodlandMansionPiece.class)
public class WoodlandMansionPiecesMixin {
    @Inject(at = @At("HEAD"), cancellable = true, method = "handleDataMarker")
    public void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox, CallbackInfo ci) {
        RandomSource randomValue = RandomSource.create();
        int value = randomValue.nextInt(8);
        if (metadata.equals("Provoker")) {
            ProvokerEntity provoker;
            provoker = EntityRegistry.PROVOKER.create(world.getLevel(), EntitySpawnReason.STRUCTURE);
            provoker.setPersistenceRequired();
            provoker.snapTo(pos, 0.0f, 0.0f);
            provoker.finalizeSpawn(world, world.getCurrentDifficultyAt(provoker.blockPosition()), EntitySpawnReason.STRUCTURE, null);
            world.addFreshEntityWithPassengers(provoker);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        int value2 = randomValue.nextInt(2);
        if (metadata.equals("Warrior") && value2 == 1) {
            BasherEntity basher;
            basher = EntityRegistry.BASHER.create(world.getLevel(),  EntitySpawnReason.STRUCTURE);
            basher.setPersistenceRequired();
            basher.snapTo(pos, 0.0f, 0.0f);
            basher.finalizeSpawn(world, world.getCurrentDifficultyAt(basher.blockPosition()), EntitySpawnReason.STRUCTURE, null);
            world.addFreshEntityWithPassengers(basher);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        if (metadata.equals("Archivist")) {
            ArchivistEntity archivist;
            archivist = EntityRegistry.ARCHIVIST.create(world.getLevel(),  EntitySpawnReason.STRUCTURE);
            archivist.setPersistenceRequired();
            archivist.snapTo(pos, 0.0f, 0.0f);
            archivist.finalizeSpawn(world, world.getCurrentDifficultyAt(archivist.blockPosition()), EntitySpawnReason.STRUCTURE, null);
            world.addFreshEntityWithPassengers(archivist);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        if (metadata.equals("invoker")) {
            InvokerEntity archivist;
            archivist = EntityRegistry.INVOKER.create(world.getLevel(),  EntitySpawnReason.STRUCTURE);
            archivist.setPersistenceRequired();
            archivist.snapTo(pos, 0.0f, 0.0f);
            archivist.finalizeSpawn(world, world.getCurrentDifficultyAt(archivist.blockPosition()), EntitySpawnReason.STRUCTURE, null);
            world.addFreshEntityWithPassengers(archivist);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}