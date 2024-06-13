package eu.pb4.illagerexpansion.item;


import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.poly.PolymerModels;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public interface ModArmorMaterial {
    RegistryEntry<ArmorMaterial> PLATINUM_INFUSED_NETHERITE = register("platinum_infused_netherite", new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
        map.put(ArmorItem.Type.BODY, 20);
    }), 37, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> Ingredient.ofItems(ItemRegistry.PLATINUM_SHEET), List.of(), 3.0f, 0.25f));

    static RegistryEntry<ArmorMaterial> register(String name, ArmorMaterial item) {
        RegistrySyncUtils.setServerEntry(Registries.ARMOR_MATERIAL, item);
        return  Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(IllagerExpansion.MOD_ID, name), item);
    }


    static void register() {}
}
