package me.sandbox.item;

import eu.pb4.polymer.api.item.PolymerSpawnEggItem;
import me.sandbox.IllagerExpansion;
import me.sandbox.entity.EntityRegistry;
import me.sandbox.item.custom.*;
import me.sandbox.poly.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    //GENERAL ITEMS
    public static final Item UNUSUAL_DUST = registerItem("unusual_dust",
            new SimplePolymerAutoItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc), Items.GLOWSTONE_DUST));
    public static final Item ILLUSIONARY_DUST = registerItem("illusionary_dust",
            new IllusionaryDustItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc)));
    public static final Item HORN_OF_SIGHT = registerItem("horn_of_sight",
            new HornOfSightItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc).maxCount(1)));
    public static final Item HALLOWED_GEM = registerItem("hallowed_gem",
            new SimplePolymerAutoItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc), Items.EMERALD));
    public static final Item PRIMAL_ESSENCE = registerItem("primal_essence",
            new SimplePolymerAutoItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc), Items.GHAST_TEAR));
    public static final Item PLATINUM_CHUNK = registerItem("platinum_chunk",
            new SimplePolymerAutoItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc), Items.IRON_NUGGET));
    public static final Item PLATINUM_SHEET = registerItem("platinum_sheet",
            new SimplePolymerAutoItem(new FabricItemSettings().group(ModItemGroup.SandBoxMisc), Items.IRON_INGOT));
    public static final Item FIRECALLER_BELT = registerItem("firecaller_belt",
            new SimplePolymerAutoItem(new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc).maxCount(1), Items.LEATHER_HORSE_ARMOR));


    //TOOLS
    public static final Item HATCHET = registerItem("hatchet",
            new HatchetItem(new FabricItemSettings().maxDamage(250).group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_PICKAXE = registerItem("platinum_infused_netherite_pickaxe",
            new ModPickaxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1, -2.8f, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_AXE = registerItem("platinum_infused_netherite_axe",
            new ModAxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 5, -3.0f, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_HOE = registerItem("platinum_infused_netherite_hoe",
            new ModHoeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, -2, 0.0f, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_SWORD = registerItem("platinum_infused_netherite_sword",
            new PolymerSwordItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 3, -2.4f, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_SHOVEL = registerItem("platinum_infused_netherite_shovel",
            new PolymerShovelItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1.5f, -3.0f, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));

    //ARMOR
    public static final Item PLATINUM_INFUSED_NETHERITE_HELMET = registerItem("platinum_infused_netherite_helmet",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, EquipmentSlot.HEAD, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_CHESTPLATE = registerItem("platinum_infused_netherite_chestplate",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, EquipmentSlot.CHEST, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_LEGGINGS = registerItem("platinum_infused_netherite_leggings",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, EquipmentSlot.LEGS, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));
    public static final Item PLATINUM_INFUSED_NETHERITE_BOOTS = registerItem("platinum_infused_netherite_boots",
            new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, EquipmentSlot.FEET, new FabricItemSettings().fireproof().group(ModItemGroup.SandBoxMisc)));


    //SPAWN EGGS
    public static final Item PROVOKER_SPAWN_EGG = registerItem("provoker_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.PROVOKER, Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item SURRENDERED_SPAWN_EGG = registerItem("surrendered_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.SURRENDERED,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item BASHER_SPAWN_EGG = registerItem("basher_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.BASHER,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item SORCERER_SPAWN_EGG = registerItem("sorcerer_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.SORCERER,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item ARCHIVIST_SPAWN_EGG = registerItem("archivist_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.ARCHIVIST,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item ILLAGER_BRUTE_SPAWN_EGG = registerItem("inquisitor_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.INQUISITOR,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item MARAUDER_SPAWN_EGG = registerItem("marauder_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.MARAUDER,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item ALCHEMIST_SPAWN_EGG = registerItem("alchemist_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.ALCHEMIST,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));
    public static final Item FIRECALLER_SPAWN_EGG = registerItem("firecaller_spawn_egg",
            new PolymerSpawnEggItem(EntityRegistry.FIRECALLER,Items.VINDICATOR_SPAWN_EGG, new Item.Settings().group(ModItemGroup.SandBoxMobs)));



    public static Item registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(IllagerExpansion.MOD_ID, name), item);

        PolymerModels.requestModel(new Identifier(IllagerExpansion.MOD_ID, "item/" + name), item);
        return item;
    }




    public static void registerModItems() {
    }
}
