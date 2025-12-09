package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public interface ModToolMaterial {
    ToolMaterial PLATINUM_INFUSED_NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2031, 9.0f, 4.0f, 17, TagKey.create(Registries.ITEM, id("platinum_repair")));

}
