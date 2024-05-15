package eu.pb4.illagerexpansion.block;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.block.custom.ImbuingTableBlock;
import eu.pb4.illagerexpansion.block.custom.MagicFireBlock;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;


public class BlockRegistry {


    //Decoration Blocks
    public static final Block IMBUING_TABLE = registerBlock("imbuing_table",
            new ImbuingTableBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(4f).requiresTool()), true);


    public static final Block MAGIC_FIRE = registerBlock("magic_fire",
            new MagicFireBlock(AbstractBlock.Settings.create().dropsNothing().nonOpaque().mapColor(MapColor.PURPLE).noCollision().luminance(state -> 10), 0.0f), false);

    private static Block registerBlock(String name, Block block, boolean group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registries.BLOCK, new Identifier(IllagerExpansion.MOD_ID, name), block);
    }

    private static <T extends Block & PolymerHeadBlock> Item registerBlockItem(String name, Block block, boolean group) {
        Item x;
        if (block instanceof PolymerHeadBlock) {
            x = ItemRegistry.registerItem(name, new PolymerHeadBlockItem((T) block, new Item.Settings()));
        } else {
            x = ItemRegistry.registerItem(name, new PolymerBlockItem(block, new Item.Settings(), Items.STRUCTURE_VOID));
        }
        if (!group) {
            ItemRegistry.ITEMS.remove(x);
        }
        return x;
    }

    public static void registerModBlocks() {
    }
}