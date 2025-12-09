package eu.pb4.illagerexpansion.mixin;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface CreeperEntityAccessor {
    @Accessor
    int getSwell();

    @Accessor
    void setSwell(int currentFuseTime);
}
