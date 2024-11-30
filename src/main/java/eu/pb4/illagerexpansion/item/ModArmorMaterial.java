package eu.pb4.illagerexpansion.item;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.EnumMap;


import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public interface ModArmorMaterial {
    ArmorMaterial PLATINUM_INFUSED_NETHERITE = new ArmorMaterial(37, Util.make(new EnumMap<>(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 3);
        map.put(EquipmentType.LEGGINGS, 6);
        map.put(EquipmentType.CHESTPLATE, 8);
        map.put(EquipmentType.HELMET, 3);
        map.put(EquipmentType.BODY, 11);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0f, 0.25f, TagKey.of(RegistryKeys.ITEM, id("platinum_repair")),
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, id("platinum")));

    static void register() {}
}
