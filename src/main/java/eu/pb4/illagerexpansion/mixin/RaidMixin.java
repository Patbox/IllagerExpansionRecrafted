package eu.pb4.illagerexpansion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow @Final private int numGroups;

    @Shadow public abstract int getRaidOmenLevel();

    @Shadow public abstract void joinRaid(ServerLevel world, int wave, Raider raider, @Nullable BlockPos pos, boolean existing);

    @Inject(method = "spawnGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;shouldSpawnBonusGroup()Z", shift = At.Shift.AFTER))
    private void spawnTheBoss(ServerLevel world, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) int wave) {
        if (wave == this.numGroups + 1 && this.getRaidOmenLevel() > 4) {
            var raiderEntity = EntityRegistry.INVOKER.create(world, EntitySpawnReason.PATROL);
            this.joinRaid(world, wave, raiderEntity, pos, false);
        }
    }
}
