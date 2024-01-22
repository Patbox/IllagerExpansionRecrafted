package me.sandbox.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import me.sandbox.IllagerExpansion;
import me.sandbox.entity.EntityRegistry;
import me.sandbox.item.custom.*;
import me.sandbox.poly.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
            new SimplePolymerAutoItem(new Item.Settings().fireproof().maxCount(1), Items.LEATHER_HORSE_ARMOR));


    //TOOLS
    public static final Item HATCHET = registerItem("hatchet",
            new HatchetItem(new Item.Settings().maxDamage(250)));
    public static final Item PLATINUM_INFUSED_NETHERITE_PICKAXE = registerItem("platinum_infused_netherite_pickaxe",
            new ModPickaxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1, -2.8f, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_AXE = registerItem("platinum_infused_netherite_axe",
            new ModAxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 5, -3.0f, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_HOE = registerItem("platinum_infused_netherite_hoe",
            new ModHoeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, -2, 0.0f, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_SWORD = registerItem("platinum_infused_netherite_sword",
            new PolymerSwordItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 3, -2.4f, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_SHOVEL = registerItem("platinum_infused_netherite_shovel",
            new PolymerShovelItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1.5f, -3.0f, new Item.Settings().fireproof()));

    //ARMOR
    public static final Item PLATINUM_INFUSED_NETHERITE_HELMET = registerItem("platinum_infused_netherite_helmet",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.HELMET, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_CHESTPLATE = registerItem("platinum_infused_netherite_chestplate",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_LEGGINGS = registerItem("platinum_infused_netherite_leggings",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Settings().fireproof()));
    public static final Item PLATINUM_INFUSED_NETHERITE_BOOTS = registerItem("platinum_infused_netherite_boots",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorItem.Type.BOOTS, new Item.Settings().fireproof()));


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


    public static Item registerItem(String name, Item item) {
        ITEMS.add(item);
        Registry.register(Registries.ITEM, new Identifier(IllagerExpansion.MOD_ID, name), item);

        PolymerModels.requestModel(new Identifier(IllagerExpansion.MOD_ID, "item/" + name), item);
        return item;
    }


    public static void registerModItems() {
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(IllagerExpansion.MOD_ID, "main"),
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
