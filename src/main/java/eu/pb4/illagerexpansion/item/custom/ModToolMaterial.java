package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public interface ModToolMaterial {
    ToolMaterial PLATINUM_INFUSED_NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2031, 9.0f, 4.0f, 17, TagKey.of(RegistryKeys.ITEM, id("platinum_repair")));

}
