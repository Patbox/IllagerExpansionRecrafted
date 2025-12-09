package eu.pb4.illagerexpansion.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import java.util.EnumMap;


import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public interface ModArmorMaterial {
    ArmorMaterial PLATINUM_INFUSED_NETHERITE = new ArmorMaterial(37, Util.make(new EnumMap<>(ArmorType.class), (map) -> {
        map.put(ArmorType.BOOTS, 3);
        map.put(ArmorType.LEGGINGS, 6);
        map.put(ArmorType.CHESTPLATE, 8);
        map.put(ArmorType.HELMET, 3);
        map.put(ArmorType.BODY, 11);
    }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.25f, TagKey.create(Registries.ITEM, id("platinum_repair")),
            ResourceKey.create(EquipmentAssets.ROOT_ID, id("platinum")));

    static void register() {}
}
