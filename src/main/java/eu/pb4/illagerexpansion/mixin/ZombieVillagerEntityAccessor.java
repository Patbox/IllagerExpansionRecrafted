package eu.pb4.illagerexpansion.mixin;

import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ZombieVillager.class)
public interface ZombieVillagerEntityAccessor {
    @Accessor
    int getVillagerConversionTime();

    @Accessor
    void setVillagerConversionTime(int conversionTimer);
}
