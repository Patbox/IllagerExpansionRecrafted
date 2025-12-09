package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( PatrolSpawner.class)
public abstract class MarauderPatrolMixin
{
    @Inject(at = { @At("HEAD") }, cancellable = true, method = { "spawnPatrolMember" })
    public void spawnMarauder(ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random, boolean captain, CallbackInfoReturnable<Boolean> cir) {
        final BlockState bs = world.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, bs, bs.getFluidState(), EntityType.PILLAGER)) {
            cir.cancel();
        }
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, world, EntitySpawnReason.PATROL, pos, random)) {
            cir.cancel();
        }
        final int randvalue = random.nextInt(2);
        if (randvalue == 0) {
            final PatrollingMonster marauder = EntityRegistry.MARAUDER.create(world, EntitySpawnReason.PATROL);
            if (marauder != null) {
                if (captain) {
                    marauder.setPatrolLeader(true);
                    marauder.findPatrolTarget();
                }
                marauder.setPos(pos.getX(), pos.getY(), pos.getZ());
                marauder.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.PATROL, null);
                world.addFreshEntityWithPassengers(marauder);
            }
        }
    }
}
