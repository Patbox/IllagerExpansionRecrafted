package eu.pb4.illagerexpansion.mixin;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ZombieVillagerEntity.class)
public interface ZombieVillagerEntityAccessor {
    @Accessor
    int getConversionTimer();

    @Accessor
    void setConversionTimer(int conversionTimer);
}
