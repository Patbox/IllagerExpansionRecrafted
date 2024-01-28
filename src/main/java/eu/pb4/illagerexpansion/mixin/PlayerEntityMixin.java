package eu.pb4.illagerexpansion.mixin;

import com.google.common.collect.ImmutableSet;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickFirecallerBelt(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        TrinketsApi.getTrinketComponent(player).ifPresent(c -> c.forEach((slotReference, stack) -> {
            if (stack.isOf(ItemRegistry.FIRECALLER_BELT)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 60, 0, true, false, true));
            }
        }));
    }
}
