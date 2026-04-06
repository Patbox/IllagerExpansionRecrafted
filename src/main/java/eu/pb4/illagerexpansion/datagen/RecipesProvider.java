package eu.pb4.illagerexpansion.datagen;

import eu.pb4.illagerexpansion.block.BlockRegistry;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.List;
import java.util.concurrent.CompletableFuture;


class RecipesProvider extends FabricRecipeProvider {

    public RecipesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        var reg = registryLookup.lookupOrThrow(Registries.ITEM);

        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                ShapedRecipeBuilder.shaped(reg, RecipeCategory.MISC, ItemRegistry.HALLOWED_GEM)
                        .pattern("#B#")
                        .pattern("RDR")
                        .pattern("#B#")
                        .define('#', Items.AMETHYST_SHARD)
                        .define('B', ItemRegistry.UNUSUAL_DUST)
                        .define('R', ItemRegistry.ILLUSIONARY_DUST)
                        .define('D', Items.DIAMOND)
                        .unlockedBy("dust", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.UNUSUAL_DUST))
                        .save(output);

                ShapedRecipeBuilder.shaped(reg, RecipeCategory.MISC, ItemRegistry.HORN_OF_SIGHT)
                        .pattern("GAG")
                        .pattern("GHG")
                        .pattern("GGG")
                        .define('A', ItemRegistry.HALLOWED_GEM)
                        .define('G', Items.GOLD_INGOT)
                        .define('H', Items.GOAT_HORN)
                        .unlockedBy("dust", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOAT_HORN))
                        .save(output);

                ShapedRecipeBuilder.shaped(reg, RecipeCategory.MISC, BlockRegistry.IMBUING_TABLE)
                        .pattern("#P#")
                        .pattern("OSO")
                        .pattern("#E#")
                        .define('#', Items.COPPER_BLOCK)
                        .define('P', Items.PAPER)
                        .define('O', Items.DARK_OAK_LOG)
                        .define('S', ItemRegistry.PRIMAL_ESSENCE)
                        .define('E', Items.EXPERIENCE_BOTTLE)
                        .unlockedBy("dust", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOAT_HORN))
                        .save(output);

                ShapelessRecipeBuilder.shapeless(reg, RecipeCategory.MISC, ItemRegistry.PLATINUM_SHEET)
                        .requires(ItemRegistry.PLATINUM_CHUNK, 4)
                        .unlockedBy("dust", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PLATINUM_CHUNK))
                        .save(output);

                ShapedRecipeBuilder.shaped(reg, RecipeCategory.MISC, ItemRegistry.PLATINUM_UPGRADE_TEMPLATE, 2)
                        .pattern("#P#")
                        .pattern("#S#")
                        .pattern("###")
                        .define('#', Items.DIAMOND)
                        .define('S', Items.COPPER_BLOCK)
                        .define('P', ItemRegistry.PLATINUM_UPGRADE_TEMPLATE)
                        .unlockedBy("dust", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOAT_HORN))
                        .save(output);

                for (var x : List.of(ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_SPEAR, ItemRegistry.PLATINUM_INFUSED_NETHERITE_PICKAXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_SHOVEL,
                        ItemRegistry.PLATINUM_INFUSED_NETHERITE_HOE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_SWORD, ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE,
                        ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS, ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET,
                        ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)) {
                    SmithingTransformRecipeBuilder.smithing(
                                    Ingredient.of(ItemRegistry.PLATINUM_UPGRADE_TEMPLATE),
                                    Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.parse(BuiltInRegistries.ITEM.getKey(x).getPath().substring("platinum_infused_".length())))),
                                    Ingredient.of(ItemRegistry.PLATINUM_SHEET),
                                    RecipeCategory.TOOLS,
                                    x
                            )
                            .unlocks("dust", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.PLATINUM_SHEET))
                            .save(output, getSimpleRecipeName(x));
                }
            }
        };
    }

    public void of(RecipeOutput exporter, RecipeHolder<?>... recipes) {
        for (var recipe : recipes) {
            exporter.accept(recipe.id(), recipe.value(), null);
        }
    }

    @Override
    public String getName() {
        return "recipes";
    }
}
