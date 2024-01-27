package eu.pb4.illagerexpansion.mixin;


import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Potions.class)
public interface PotionsAccessor {
    @Invoker
    static Potion callRegister(String name, Potion potion) {
        throw new UnsupportedOperationException();
    }
}
