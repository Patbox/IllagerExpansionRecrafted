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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;


public class BlockRegistry {


    //Decoration Blocks
    public static final Block IMBUING_TABLE = registerBlock("imbuing_table",
            (s) -> new ImbuingTableBlock(s.mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.COPPER).strength(4f).requiresTool()), true);


    public static final Block MAGIC_FIRE = registerBlock("magic_fire",
            (s) -> new MagicFireBlock(s.dropsNothing().nonOpaque().mapColor(MapColor.PURPLE).noCollision().luminance(state -> 10), 0.0f), false);

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> f, boolean group) {
        Item x;
        var id = Identifier.of(IllagerExpansion.MOD_ID, name);
        var block = f.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));
        if (block instanceof PolymerHeadBlock) {
            x = ItemRegistry.registerItem(name, (s) -> new PolymerHeadBlockItem((Block & PolymerHeadBlock) block, s));
        } else {
            x = ItemRegistry.registerItem(name, (s) -> new PolymerBlockItem(block, s, Items.STRUCTURE_VOID));
        }
        if (!group) {
            ItemRegistry.ITEMS.remove(x);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void registerModBlocks() {
    }
}