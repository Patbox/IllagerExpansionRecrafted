package me.sandbox.block;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.block.PolymerHeadBlock;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import eu.pb4.polymer.api.item.PolymerHeadBlockItem;
import me.sandbox.IllagerExpansion;
import me.sandbox.block.custom.ImbuingTableBlock;
import me.sandbox.block.custom.MagicFireBlock;
import me.sandbox.item.ModItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class BlockRegistry {


    //Decoration Blocks
    public static final Block IMBUING_TABLE = registerBlock("imbuing_table",
            new ImbuingTableBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(4f).requiresTool()), ModItemGroup.SandBoxMisc);


    public static final Block MAGIC_FIRE = registerBlock("magic_fire",
            new MagicFireBlock(FabricBlockSettings.of(Material.FIRE, MapColor.PURPLE).noCollision().luminance(state -> 10), 0.0f), null);

    private static Block registerBlock(String name, Block block, @Nullable ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(IllagerExpansion.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        if (block instanceof PolymerHeadBlock) {
            return Registry.register(Registry.ITEM, new Identifier(IllagerExpansion.MOD_ID, name),
                    new PolymerHeadBlockItem((PolymerHeadBlock) block, new FabricItemSettings().group(group)));
        } else {
            return Registry.register(Registry.ITEM, new Identifier(IllagerExpansion.MOD_ID, name),
                    new PolymerBlockItem(block, new FabricItemSettings().group(group), Items.STRUCTURE_VOID));
        }
    }

    public static void registerModBlocks() {
    }
}