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
        this.getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SWORD);
        this.getOrCreateTagBuilder(ItemTags.AXES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_AXE);
        this.getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_PICKAXE);
        this.getOrCreateTagBuilder(ItemTags.SHOVELS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_SHOVEL);
        this.getOrCreateTagBuilder(ItemTags.HOES)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HOE);

        this.getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
        ;

        this.getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
        ;

        this.getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
        ;

        this.getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
        ;

        this.getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
        ;

        this.getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(ItemRegistry.PLATINUM_SHEET)
                .add(ItemRegistry.HALLOWED_GEM);

        this.getOrCreateTagBuilder(ItemRegistry.MAGIC_DAMAGE_BLOCKING_ARMOR)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_HELMET)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_CHESTPLATE)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_LEGGINGS)
                .add(ItemRegistry.PLATINUM_INFUSED_NETHERITE_BOOTS)
                ;
    }
}
