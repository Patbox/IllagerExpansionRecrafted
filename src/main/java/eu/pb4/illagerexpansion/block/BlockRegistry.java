package eu.pb4.illagerexpansion.block;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.block.custom.ImbuingTableBlock;
import eu.pb4.illagerexpansion.block.custom.MagicFireBlock;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;


public class BlockRegistry {


    //Decoration Blocks
    public static final Block IMBUING_TABLE = registerBlock("imbuing_table",
            (s) -> new ImbuingTableBlock(s.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(4f).requiresCorrectToolForDrops()), true);


    public static final Block MAGIC_FIRE = registerBlock("magic_fire",
            (s) -> new MagicFireBlock(s.noLootTable().noOcclusion().mapColor(MapColor.COLOR_PURPLE).noCollision().lightLevel(state -> 10), 0.0f), false);

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> f, boolean group) {
        Item x;
        var id = Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, name);
        var block = f.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)));
        if (block instanceof PolymerHeadBlock) {
            x = ItemRegistry.registerItem(name, (s) -> new PolymerHeadBlockItem((Block & PolymerHeadBlock) block, s.useBlockDescriptionPrefix()));
        } else {
            x = ItemRegistry.registerItem(name, (s) -> new PolymerBlockItem(block, s.useBlockDescriptionPrefix(), Items.STRUCTURE_VOID));
        }
        if (!group) {
            ItemRegistry.ITEMS.remove(x);
        }
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void registerModBlocks() {
    }
}