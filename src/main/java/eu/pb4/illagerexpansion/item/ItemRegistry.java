package eu.pb4.illagerexpansion.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import eu.pb4.illagerexpansion.item.custom.*;
import eu.pb4.illagerexpansion.poly.*;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class ItemRegistry {
    public static final List<Item> ITEMS = new ArrayList<>();

    //GENERAL ITEMS
    public static final Item UNUSUAL_DUST = registerItem("unusual_dust",
            new SimplePolymerAutoItem(new Item.Settings(), Items.GLOWSTONE_DUST));
    public static final Item ILLUSIONARY_DUST = registerItem("illusionary_dust",
            new IllusionaryDustItem(new Item.Settings()));
    public static final Item HORN_OF_SIGHT = registerItem("horn_of_sight",
            new HornOfSightItem(new Item.Settings().maxCount(1)));
    public static final Item HALLOWED_GEM = registerItem("hallowed_gem",
            new SimplePolymerAutoItem(new Item.Settings(), Items.EMERALD));
    public static final Item PRIMAL_ESSENCE = registerItem("primal_essence",
            new SimplePolymerAutoItem(new Item.Settings(), Items.GHAST_TEAR));
    public static final Item PLATINUM_CHUNK = registerItem("platinum_chunk",
            new SimplePolymerAutoItem(new Item.Settings(), Items.IRON_NUGGET));
    public static final Item PLATINUM_SHEET = registerItem("platinum_sheet",
            new SimplePolymerAutoItem(new Item.Settings(), Items.IRON_INGOT));
    public static final Item FIRECALLER_BELT = registerItem("firecaller_belt",
            new SimplePolymerAutoItem(new Item.Settings().fireproof().maxCount(1), Items.STICK));


    //TOOLS
    public static final Item HATCHET = registerItem("hatchet",
            new HatchetItem(new Item.Settings().maxDamage(250)));

    public static final Item PLATINUM_UPGRADE_TEMPLATE = registerItem("platinum_upgrade_template", PolymerSmithingTemplate.createPlatinumUpgradeTemplate());
    public static final Item PLATINUM_INFUSED_NETHERITE_PICKAXE = registerItem("platinum_infused_netherite_pickaxe",
            new PlatinumPickaxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, new Item.Settings().fireproof()
                    .attributeModifiers(MiningToolItem.createAttributeModifiers(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1, -2.8f))));
    public static final Item PLATINUM_INFUSED_NETHERITE_AXE = registerItem("platinum_infused_netherite_axe",
            new PlatinumAxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, new Item.Settings().fireproof()
                    .attributeModifiers(MiningToolItem.createAttributeModifiers(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 5, -3.0f))));
    public static final Item PLATINUM_INFUSED_NETHERITE_HOE = registerItem("platinum_infused_netherite_hoe",
            new PlatinumHoeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, new Item.Settings().fireproof()
                    .attributeModifiers(MiningToolItem.createAttributeModifiers(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, -2, 0.0f))));
    public static final Item PLATINUM_INFUSED_NETHERITE_SWORD = registerItem("platinum_infused_netherite_sword",
            new PlatinumSwordItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, new Item.Settings().fireproof()
                    .attributeModifiers(MiningToolItem.createAttributeModifiers(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 3, -2.4f))));
    public static final Item PLATINUM_INFUSED_NETHERITE_SHOVEL = registerItem("platinum_infused_netherite_shovel",
            new PlatinumShovelItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, new Item.Settings().fireproof()
                    .attributeModifiers(MiningToolItem.createAttributeModifiers(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1.5f, -3.0f))));

    //ARMOR
    public static final Item PLATINUM_INFUSED_NETHERITE_HELMET = registerItem("platinum_infused_netherite_helmet",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.HELMET, new Item.Settings().fireproof()
                    .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(40))));
    public static final Item PLATINUM_INFUSED_NETHERITE_CHESTPLATE = registerItem("platinum_infused_netherite_chestplate",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Settings().fireproof()
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(40))));
    public static final Item PLATINUM_INFUSED_NETHERITE_LEGGINGS = registerItem("platinum_infused_netherite_leggings",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Settings().fireproof()
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(40))));
    public static final Item PLATINUM_INFUSED_NETHERITE_BOOTS = registerItem("platinum_infused_netherite_boots",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.BOOTS, new Item.Settings().fireproof()
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(40))));


    //SPAWN EGGS
    public static final Item PROVOKER_SPAWN_EGG = registerItem("provoker_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.PROVOKER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item SURRENDERED_SPAWN_EGG = registerItem("surrendered_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.SURRENDERED, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item BASHER_SPAWN_EGG = registerItem("basher_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.BASHER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item SORCERER_SPAWN_EGG = registerItem("sorcerer_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.SORCERER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item ARCHIVIST_SPAWN_EGG = registerItem("archivist_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.ARCHIVIST, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item ILLAGER_BRUTE_SPAWN_EGG = registerItem("inquisitor_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.INQUISITOR, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item MARAUDER_SPAWN_EGG = registerItem("marauder_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.MARAUDER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item ALCHEMIST_SPAWN_EGG = registerItem("alchemist_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.ALCHEMIST, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final Item FIRECALLER_SPAWN_EGG = registerItem("firecaller_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.FIRECALLER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings()));
    public static final TagKey<Item> MAGIC_DAMAGE_BLOCKING_ARMOR = TagKey.of(RegistryKeys.ITEM, id("magic_damage_blocking_armor"));

    public static Item registerItem(String name, Item item) {
        ITEMS.add(item);
        Registry.register(Registries.ITEM, Identifier.of(IllagerExpansion.MOD_ID, name), item);

        PolymerModels.requestModel(Identifier.of(IllagerExpansion.MOD_ID, "item/" + name), item);
        return item;
    }


    public static void registerModItems() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(IllagerExpansion.MOD_ID, "main"),
                ItemGroup.create(null, -1)
                        .displayName(Text.translatable("itemGroup.illagerexp.sandboxmisc"))
                        .icon(() -> new ItemStack(ItemRegistry.HORN_OF_SIGHT))
                        .entries(((displayContext, entries) -> {
                            for (var item : ITEMS) {
                                if (!(item instanceof SpawnEggItem)) {
                                    entries.add(item);
                                }
                            }
                            for (var item : ITEMS) {
                                if (item instanceof SpawnEggItem) {
                                    entries.add(item);
                                }
                            }
                        }))
                        .build()
        );
    }
}
