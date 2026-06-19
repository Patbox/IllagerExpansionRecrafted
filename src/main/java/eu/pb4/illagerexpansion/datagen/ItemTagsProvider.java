package eu.pb4.illagerexpansion.datagen;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable FabricTagsProvider.BlockTagsProvider blockTagsProvider) {
        super(output, registriesFuture, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ItemTags.SWORDS)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SWORD));
        this.tag(ItemTags.AXES)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE));
        this.tag(ItemTags.PICKAXES)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_PICKAXE));
        this.tag(ItemTags.SHOVELS)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SHOVEL));
        this.tag(ItemTags.HOES)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HOE));

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS))
        ;

        this.tag(ItemTags.FOOT_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS))
        ;

        this.tag(ItemTags.LEG_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS))
        ;

        this.tag(ItemTags.CHEST_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE))
        ;

        this.tag(ItemTags.HEAD_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET))
        ;

        this.tag(ItemTags.TRIM_MATERIALS)
                .add(get(ItemRegistry.PLATINUM_SHEET))
                .add(get(ItemRegistry.HALLOWED_GEM));

        this.tag(ItemRegistry.MAGIC_DAMAGE_BLOCKING_ARMOR)
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS))
                .add(get(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS))
                ;
    }

    private ResourceKey<Item> get(Item item) {
        return item.builtInRegistryHolder().key();
    }
}
