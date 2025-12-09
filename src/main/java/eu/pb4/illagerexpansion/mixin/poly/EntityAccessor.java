package eu.pb4.illagerexpansion.mixin.poly;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    static EntityDataAccessor<Pose> getDATA_POSE() {
        throw new UnsupportedOperationException();
    }
}
