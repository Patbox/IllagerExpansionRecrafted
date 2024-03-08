package eu.pb4.illagerexpansion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
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
    @Shadow private int badOmenLevel;

    @Shadow public abstract void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

    @Shadow @Final private ServerWorld world;

    @Shadow @Final private int waveCount;

    @Inject(method = "spawnNextWave", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/Raid;isSpawningExtraWave()Z", shift = At.Shift.AFTER))
    private void spawnTheBoss(BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) int wave) {
        if (wave == this.waveCount + 1 && this.badOmenLevel > 4) {
            var raiderEntity = EntityRegistry.INVOKER.create(world);
            this.addRaider(wave, raiderEntity, pos, false);
        }
    }
}
