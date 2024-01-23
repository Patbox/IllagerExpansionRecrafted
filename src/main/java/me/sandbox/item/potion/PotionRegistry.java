package me.sandbox.item.potion;

import me.sandbox.item.ItemRegistry;
import me.sandbox.mixin.BrewingRecipeRegistryMixin;
import me.sandbox.mixin.PotionsAccessor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

public class PotionRegistry {
    public static Potion BERSERKING;
    public static Potion BERSERKING_LONG;
    public static Potion BERSERKING_STRONG;

    public static void registerPotions() {
        BERSERKING = PotionsAccessor.callRegister("berserking", new PolyPotion("berserking", new StatusEffectInstance(StatusEffects.STRENGTH, 600, 1), new StatusEffectInstance(StatusEffects.SPEED, 600, 1)));
        BERSERKING_LONG = PotionsAccessor.callRegister("berserking_long", new PolyPotion("berserking_long", new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0), new StatusEffectInstance(StatusEffects.SPEED, 1200, 0)));
        BERSERKING_STRONG = PotionsAccessor.callRegister("berserking_strong", new PolyPotion("berserking_strong", new StatusEffectInstance(StatusEffects.STRENGTH, 300, 2), new StatusEffectInstance(StatusEffects.SPEED, 300, 2)));

        BrewingRecipeRegistryMixin.callRegisterPotionRecipe(Potions.AWKWARD, Items.GOAT_HORN, PotionRegistry.BERSERKING);
        BrewingRecipeRegistryMixin.callRegisterPotionRecipe(PotionRegistry.BERSERKING, Items.REDSTONE, PotionRegistry.BERSERKING_LONG);
        BrewingRecipeRegistryMixin.callRegisterPotionRecipe(PotionRegistry.BERSERKING, Items.GLOWSTONE_DUST, PotionRegistry.BERSERKING_STRONG);
    }
}
