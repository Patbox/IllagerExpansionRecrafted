package eu.pb4.illagerexpansion.item;

import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import eu.pb4.illagerexpansion.item.custom.*;
import eu.pb4.illagerexpansion.poly.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.equipment.ArmorType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class ItemRegistry {
    public static final List<Item> ITEMS = new ArrayList<>();

    //GENERAL ITEMS
    public static final Item UNUSUAL_DUST = registerItem("unusual_dust",
            (s) -> new SimplePolymerAutoItem(s, Items.GLOWSTONE_DUST));
    public static final Item ILLUSIONARY_DUST = registerItem("illusionary_dust",
            (s) -> new IllusionaryDustItem(s));
    public static final Item HORN_OF_SIGHT = registerItem("horn_of_sight",
            (s) -> new HornOfSightItem(s.stacksTo(1)));
    public static final Item HALLOWED_GEM = registerItem("hallowed_gem",
            (s) -> new SimplePolymerAutoItem(s, Items.EMERALD));
    public static final Item PRIMAL_ESSENCE = registerItem("primal_essence",
            (s) -> new SimplePolymerAutoItem(s, Items.GHAST_TEAR));
    public static final Item PLATINUM_CHUNK = registerItem("platinum_chunk",
            (s) -> new SimplePolymerAutoItem(s, Items.IRON_NUGGET));
    public static final Item PLATINUM_SHEET = registerItem("platinum_sheet",
            (s) -> new SimplePolymerAutoItem(s, Items.IRON_INGOT));
    public static final Item FIRECALLER_BELT = registerItem("firecaller_belt",
            (s) -> new SimplePolymerAutoItem(s.fireResistant().stacksTo(1), Items.STICK));


    //TOOLS
    public static final Item HATCHET = registerItem("hatchet",
            (s) -> new HatchetItem(s.durability(250)));

    public static final Item PLATINUM_UPGRADE_TEMPLATE = registerItem("platinum_upgrade_template", PolymerSmithingTemplate::createPlatinumUpgradeTemplate);
    public static final Item PLATINUM_INFUSED_NETHERITE_PICKAXE = registerItem("platinum_infused_netherite_pickaxe",
            (s) -> new PlatinumPickaxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1, -2.8f, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_AXE = registerItem("platinum_infused_netherite_axe",
            (s) -> new PlatinumAxeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 5, -3.0f, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_HOE = registerItem("platinum_infused_netherite_hoe",
            (s) -> new PlatinumHoeItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, -2, 0.0f, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_SWORD = registerItem("platinum_infused_netherite_sword",
            (s) -> new PlatinumSwordItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE,3, -2.4f, s.fireResistant() ));
    public static final Item PLATINUM_INFUSED_NETHERITE_SPEAR = registerItem("platinum_infused_netherite_spear",
            (s) -> new PlatinumSpearItem(s.fireResistant().spear(ModToolMaterial.PLATINUM_INFUSED_NETHERITE, 1.15F, 1.2F, 0.4F, 2.5F, 7.0F, 5.5F, 5.1F, 8.75F, 4.6F) ));

    public static final Item PLATINUM_INFUSED_NETHERITE_SHOVEL = registerItem("platinum_infused_netherite_shovel",
            (s) -> new PlatinumShovelItem(ModToolMaterial.PLATINUM_INFUSED_NETHERITE,  1.5f, -3.0f, s.fireResistant()));

    //ARMOR
    public static final Item PLATINUM_INFUSED_NETHERITE_HELMET = registerItem("platinum_infused_netherite_helmet",
            (s) -> new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorType.HELMET, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_CHESTPLATE = registerItem("platinum_infused_netherite_chestplate",
            (s) -> new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorType.CHESTPLATE, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_LEGGINGS = registerItem("platinum_infused_netherite_leggings",
            (s) -> new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorType.LEGGINGS, s.fireResistant()));
    public static final Item PLATINUM_INFUSED_NETHERITE_BOOTS = registerItem("platinum_infused_netherite_boots",
            (s) -> new PolymerArmorItem(ModArmorMaterial.PLATINUM_INFUSED_NETHERITE, ArmorType.BOOTS, s.fireResistant()));


    //SPAWN EGGS
    public static final Item PROVOKER_SPAWN_EGG = registerItem("provoker_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.PROVOKER)));
    public static final Item SURRENDERED_SPAWN_EGG = registerItem("surrendered_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.SURRENDERED)));
    public static final Item BASHER_SPAWN_EGG = registerItem("basher_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.BASHER)));
    public static final Item SORCERER_SPAWN_EGG = registerItem("sorcerer_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.SORCERER)));
    public static final Item ARCHIVIST_SPAWN_EGG = registerItem("archivist_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.ARCHIVIST)));
    public static final Item ILLAGER_BRUTE_SPAWN_EGG = registerItem("inquisitor_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.INQUISITOR)));
    public static final Item MARAUDER_SPAWN_EGG = registerItem("marauder_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.MARAUDER)));
    public static final Item ALCHEMIST_SPAWN_EGG = registerItem("alchemist_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.ALCHEMIST)));
    public static final Item FIRECALLER_SPAWN_EGG = registerItem("firecaller_spawn_egg",
            (s) -> new PolymerSpawnEggItem(Items.VINDICATOR_SPAWN_EGG, s.spawnEgg(EntityRegistry.FIRECALLER)));
    public static final TagKey<Item> MAGIC_DAMAGE_BLOCKING_ARMOR = TagKey.create(Registries.ITEM, id("magic_damage_blocking_armor"));

    public static Item registerItem(String name, Function<Item.Properties, Item> function) {
        var id = Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, name);
        var item = function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
        ITEMS.add(item);
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }


    public static void registerModItems() {
        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "main"),
                CreativeModeTab.builder(null, -1)
                        .title(Component.translatable("itemGroup.illagerexp.sandboxmisc"))
                        .icon(() -> new ItemStack(ItemRegistry.HORN_OF_SIGHT))
                        .displayItems(((displayContext, entries) -> {
                            for (var item : ITEMS) {
                                if (!(item instanceof SpawnEggItem)) {
                                    entries.accept(item);
                                }
                            }
                            for (var item : ITEMS) {
                                if (item instanceof SpawnEggItem) {
                                    entries.accept(item);
                                }
                            }
                        }))
                        .build()
        );
    }
}
