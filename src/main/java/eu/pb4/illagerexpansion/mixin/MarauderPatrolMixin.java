package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.spawner.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( PatrolSpawner.class)
public abstract class MarauderPatrolMixin
{
    @Inject(at = { @At("HEAD") }, cancellable = true, method = { "spawnPillager" })
    public void spawnMarauder(ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random, boolean captain, CallbackInfoReturnable<Boolean> cir) {
        final BlockState bs = world.getBlockState(pos);
        if (!SpawnHelper.isClearForSpawn(world, pos, bs, bs.getFluidState(), EntityType.PILLAGER)) {
            cir.cancel();
        }
        if (!PatrolEntity.canSpawn(EntityType.PILLAGER, world, SpawnReason.PATROL, pos, random)) {
            cir.cancel();
        }
        final int randvalue = random.nextInt(2);
        if (randvalue == 0) {
            final PatrolEntity marauder = EntityRegistry.MARAUDER.create(world);
            if (marauder != null) {
                if (captain) {
                    marauder.setPatrolLeader(true);
                    marauder.setRandomPatrolTarget();
                }
                marauder.setPosition(pos.getX(), pos.getY(), pos.getZ());
                marauder.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, null);
                world.spawnEntityAndPassengers(marauder);
            }
        }
    }
}
