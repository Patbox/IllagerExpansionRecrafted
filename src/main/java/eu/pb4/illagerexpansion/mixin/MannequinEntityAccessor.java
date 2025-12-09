package eu.pb4.illagerexpansion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;

@Mixin(Mannequin.class)
public interface MannequinEntityAccessor {
    @Accessor
    static EntityDataAccessor<ResolvableProfile> getDATA_PROFILE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static EntityDataAccessor<Optional<Component>> getDATA_DESCRIPTION() {
        throw new UnsupportedOperationException();
    }
}
