package eu.pb4.illagerexpansion.mixin;


import eu.pb4.illagerexpansion.entity.SurrenderedEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonEntityModel.class)
public class SurrenderedChargeMixin<T extends MobEntity> extends BipedEntityModel<T> {

    public SurrenderedChargeMixin(ModelPart root) {
        super(root);
    }

    @Inject(at = @At("TAIL"), cancellable = true, method = "setAngles(Lnet/minecraft/entity/mob/MobEntity;FFFFF)V")
    public void chargingAnimation(T mobEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (mobEntity instanceof SurrenderedEntity) {
            if (((SurrenderedEntity)mobEntity).isCharging()) {
                this.rightArm.pitch = 3.7699115f;
                this.leftArm.pitch = 3.7699115f;
            }
        }
    }
}
