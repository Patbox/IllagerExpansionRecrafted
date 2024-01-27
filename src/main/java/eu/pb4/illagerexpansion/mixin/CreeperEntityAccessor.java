package eu.pb4.illagerexpansion.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccessor {
    @Accessor
    int getCurrentFuseTime();

    @Accessor
    void setCurrentFuseTime(int currentFuseTime);
}
