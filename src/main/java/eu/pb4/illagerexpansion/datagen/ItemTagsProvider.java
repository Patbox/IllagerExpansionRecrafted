package eu.pb4.illagerexpansion.datagen;

import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.valueLookupBuilder(ItemTags.SWORDS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SWORD);
        this.valueLookupBuilder(ItemTags.AXES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
        this.valueLookupBuilder(ItemTags.PICKAXES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_PICKAXE);
        this.valueLookupBuilder(ItemTags.SHOVELS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SHOVEL);
        this.valueLookupBuilder(ItemTags.HOES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HOE);

        this.valueLookupBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
        ;

        this.valueLookupBuilder(ItemTags.FOOT_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
        ;

        this.valueLookupBuilder(ItemTags.LEG_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
        ;

        this.valueLookupBuilder(ItemTags.CHEST_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
        ;

        this.valueLookupBuilder(ItemTags.HEAD_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
        ;

        this.valueLookupBuilder(ItemTags.TRIM_MATERIALS)
                .add(ItemRegistry.PLATINUM_SHEET)
                .add(ItemRegistry.HALLOWED_GEM);

        this.valueLookupBuilder(ItemRegistry.MAGIC_DAMAGE_BLOCKING_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
                ;
    }
}
