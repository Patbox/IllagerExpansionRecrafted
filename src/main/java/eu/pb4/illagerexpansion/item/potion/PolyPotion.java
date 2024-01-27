package eu.pb4.illagerexpansion.item.potion;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class PolyPotion extends Potion implements PolymerSyncedObject<Potion> {

    public PolyPotion(StatusEffectInstance... effects) {
        super(null, effects);
    }

    public PolyPotion(@Nullable String baseName, StatusEffectInstance... effects) {
        super(baseName, effects);
    }


    @Override
    public Potion getPolymerReplacement(ServerPlayerEntity player) {
        return Potions.EMPTY;
    }
}
