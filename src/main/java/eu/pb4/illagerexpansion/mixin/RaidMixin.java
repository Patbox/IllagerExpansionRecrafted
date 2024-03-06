package eu.pb4.illagerexpansion.mixin;

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
    @Shadow protected abstract boolean isSpawningExtraWave();

    @Shadow private int badOmenLevel;

    @Shadow public abstract void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

    @Shadow private int wavesSpawned;

    @Shadow @Final private ServerWorld world;

    @Inject(method = "spawnNextWave", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/Raid;isSpawningExtraWave()Z"))
    private void spawnTheBoss(BlockPos pos, CallbackInfo ci) {
        if (this.isSpawningExtraWave() && this.badOmenLevel > 4) {
            var raiderEntity = EntityRegistry.INVOKER.create(world);
            this.addRaider(this.wavesSpawned + 1, raiderEntity, pos, false);
        }
    }
}
