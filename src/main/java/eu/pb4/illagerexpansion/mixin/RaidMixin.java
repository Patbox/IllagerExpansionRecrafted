package eu.pb4.illagerexpansion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow @Final private int waveCount;

    @Shadow public abstract int getBadOmenLevel();

    @Shadow public abstract void addRaider(ServerWorld world, int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

    @Inject(method = "spawnNextWave", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/Raid;isSpawningExtraWave()Z", shift = At.Shift.AFTER))
    private void spawnTheBoss(ServerWorld world, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) int wave) {
        if (wave == this.waveCount + 1 && this.getBadOmenLevel() > 4) {
            var raiderEntity = EntityRegistry.INVOKER.create(world, SpawnReason.PATROL);
            this.addRaider(world, wave, raiderEntity, pos, false);
        }
    }
}
