package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.util.TrinketsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickFirecallerBelt(CallbackInfo ci) {
        TrinketsHelper.ifWearing(this, ItemRegistry.FIRECALLER_BELT,
                () -> this.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE, 60, 0, true, false, true
                )));

    }
}
