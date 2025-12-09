package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.util.TrinketsHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickFirecallerBelt(CallbackInfo ci) {
        TrinketsHelper.ifWearing(this, ItemRegistry.FIRECALLER_BELT,
                () -> this.addEffect(new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE, 60, 0, true, false, true
                )));

    }
}
