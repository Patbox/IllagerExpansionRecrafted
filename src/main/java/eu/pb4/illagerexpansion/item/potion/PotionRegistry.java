package eu.pb4.illagerexpansion.item.potion;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class PotionRegistry {
    public static Holder<Potion> BERSERKING = register("berserking", new SimplePolymerPotion("berserking", new MobEffectInstance(MobEffects.STRENGTH, 600, 1), new MobEffectInstance(MobEffects.SPEED, 600, 1)));;
    public static Holder<Potion> BERSERKING_LONG = register("berserking_long", new SimplePolymerPotion("berserking_long", new MobEffectInstance(MobEffects.STRENGTH, 1200, 0), new MobEffectInstance(MobEffects.SPEED, 1200, 0)));;
    public static Holder<Potion> BERSERKING_STRONG = register("berserking_strong", new SimplePolymerPotion("berserking_strong", new MobEffectInstance(MobEffects.STRENGTH, 300, 2), new MobEffectInstance(MobEffects.SPEED, 300, 2)));;

    static Holder<Potion> register(String name, Potion item) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, name), item);
    }

    public static void registerPotions() {
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            builder.addMix(Potions.AWKWARD, Items.GOAT_HORN, PotionRegistry.BERSERKING);
            builder.addMix(PotionRegistry.BERSERKING, Items.REDSTONE, PotionRegistry.BERSERKING_LONG);
            builder.addMix(PotionRegistry.BERSERKING, Items.GLOWSTONE_DUST, PotionRegistry.BERSERKING_STRONG);
        });
    }
}
