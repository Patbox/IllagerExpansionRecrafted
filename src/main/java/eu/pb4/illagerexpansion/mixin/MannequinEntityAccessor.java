package eu.pb4.illagerexpansion.mixin;

import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(MannequinEntity.class)
public interface MannequinEntityAccessor {
    @Accessor
    static TrackedData<ProfileComponent> getPROFILE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TrackedData<Optional<Text>> getDESCRIPTION() {
        throw new UnsupportedOperationException();
    }
}
