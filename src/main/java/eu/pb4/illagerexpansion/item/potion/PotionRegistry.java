package eu.pb4.illagerexpansion.item.potion;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PotionRegistry {
    public static RegistryEntry<Potion> BERSERKING = register("berserking", new SimplePolymerPotion("berserking", new StatusEffectInstance(StatusEffects.STRENGTH, 600, 1), new StatusEffectInstance(StatusEffects.SPEED, 600, 1)));;
    public static RegistryEntry<Potion> BERSERKING_LONG = register("berserking_long", new SimplePolymerPotion("berserking_long", new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0), new StatusEffectInstance(StatusEffects.SPEED, 1200, 0)));;
    public static RegistryEntry<Potion> BERSERKING_STRONG = register("berserking_strong", new SimplePolymerPotion("berserking_strong", new StatusEffectInstance(StatusEffects.STRENGTH, 300, 2), new StatusEffectInstance(StatusEffects.SPEED, 300, 2)));;

    static RegistryEntry<Potion> register(String name, Potion item) {
        return Registry.registerReference(Registries.POTION, Identifier.of(IllagerExpansion.MOD_ID, name), item);
    }

    public static void registerPotions() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.GOAT_HORN, PotionRegistry.BERSERKING);
            builder.registerPotionRecipe(PotionRegistry.BERSERKING, Items.REDSTONE, PotionRegistry.BERSERKING_LONG);
            builder.registerPotionRecipe(PotionRegistry.BERSERKING, Items.GLOWSTONE_DUST, PotionRegistry.BERSERKING_STRONG);
        });
    }
}
