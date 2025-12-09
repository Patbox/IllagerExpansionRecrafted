package eu.pb4.illagerexpansion.mixin.poly;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Avatar.class)
public interface PlayerLikeEntityAccessor {
    @Accessor
    static EntityDataAccessor<Byte> getDATA_PLAYER_MODE_CUSTOMISATION() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static EntityDataAccessor<Byte> getDATA_PLAYER_MAIN_HAND() {
        throw new UnsupportedOperationException();
    }
}
