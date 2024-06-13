package eu.pb4.illagerexpansion.datagen;

import eu.pb4.illagerexpansion.block.BlockRegistry;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;


class RecipesProvider extends FabricRecipeProvider {

    public RecipesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.HALLOWED_GEM)
                .pattern("#B#")
                .pattern("RDR")
                .pattern("#B#")
                .input('#', Items.AMETHYST_SHARD)
                .input('B', ItemRegistry.UNUSUAL_DUST)
                .input('R', ItemRegistry.ILLUSIONARY_DUST)
                .input('D', Items.DIAMOND)
                .criterion("dust", InventoryChangedCriterion.Conditions.items(ItemRegistry.UNUSUAL_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.HORN_OF_SIGHT)
                .pattern("GAG")
                .pattern("GHG")
                .pattern("GGG")
                .input('A', ItemRegistry.HALLOWED_GEM)
                .input('G', Items.GOLD_INGOT)
                .input('H', Items.GOAT_HORN)
                .criterion("dust", InventoryChangedCriterion.Conditions.items(Items.GOAT_HORN))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockRegistry.IMBUING_TABLE)
                .pattern("#P#")
                .pattern("OSO")
                .pattern("#E#")
                .input('#', Items.COPPER_BLOCK)
                .input('P', Items.PAPER)
                .input('O', Items.DARK_OAK_LOG)
                .input('S', ItemRegistry.PRIMAL_ESSENCE)
                .input('E', Items.EXPERIENCE_BOTTLE)
                .criterion("dust", InventoryChangedCriterion.Conditions.items(Items.GOAT_HORN))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.PLATINUM_SHEET)
                .input(ItemRegistry.PLATINUM_CHUNK, 4)
                .criterion("dust", InventoryChangedCriterion.Conditions.items(ItemRegistry.PLATINUM_CHUNK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.PLATINUM_UPGRADE_TEMPLATE, 2)
                .pattern("#P#")
                .pattern("#S#")
                .pattern("###")
                .input('#', Items.DIAMOND)
                .input('S', Items.COPPER_BLOCK)
                .input('P', ItemRegistry.PLATINUM_UPGRADE_TEMPLATE)
                .criterion("dust", InventoryChangedCriterion.Conditions.items(Items.GOAT_HORN))
                .offerTo(exporter);

        for (var x : List.of(ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE,  ItemRegistry.PLATINUM_INFUSED_NETHERITE_PICKAXE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_SHOVEL,
                ItemRegistry.PLATINUM_INFUSED_NETHERITE_HOE, ItemRegistry.PLATINUM_INFUSED_NETHERITE_SWORD, ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE,
                ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS, ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET, ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)) {
            SmithingTransformRecipeJsonBuilder.create(
                    Ingredient.ofItems(ItemRegistry.PLATINUM_UPGRADE_TEMPLATE),
                    Ingredient.ofItems(Registries.ITEM.get(Identifier.of(Registries.ITEM.getId(x).getPath().substring("platinum_infused_".length())))),
                    Ingredient.ofItems(ItemRegistry.PLATINUM_SHEET),
                    RecipeCategory.TOOLS,
                    x
            )
                    .criterion("dust", InventoryChangedCriterion.Conditions.items(ItemRegistry.PLATINUM_SHEET))
                    .offerTo(exporter, getRecipeName(x));
        }
    }

    public void of(RecipeExporter exporter, RecipeEntry<?>... recipes) {
        for (var recipe : recipes) {
            exporter.accept(recipe.id(), recipe.value(), null);
        }
    }
}
