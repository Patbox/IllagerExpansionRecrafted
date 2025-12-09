package eu.pb4.illagerexpansion.util.spellutil;

import com.google.common.collect.Maps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import java.util.*;

public class EnchantToolUtil {


    public boolean eligibleForEnchant(LivingEntity entity) {
        var mainhand = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        var offhand = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        return (mainhand.getItem() instanceof BowItem || mainhand.getItem() instanceof CrossbowItem
                || mainhand.is(ItemTags.SWORDS) || mainhand.is(ItemTags.AXES) || offhand.getItem() instanceof BowItem
        || offhand.getItem() instanceof CrossbowItem || offhand.is(ItemTags.SWORDS) || offhand.is(ItemTags.AXES));
    }

    public void doEnchant(HolderLookup.Provider lookup, ResourceKey<Enchantment> enchantment, int enchantLevel, LivingEntity entity) {
        ItemStack mainhanditem = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offhanditem = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        var ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        ench.upgrade(lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), enchantLevel);
        mainhanditem.set(DataComponents.ENCHANTMENTS, ench.toImmutable());
        offhanditem.set(DataComponents.ENCHANTMENTS, ench.toImmutable());
    }

    public void enchant(LivingEntity entity) {
        var lookup = entity.level().registryAccess();
        var mainhanditem = entity.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        var offhanditem = entity.getItemBySlot(EquipmentSlot.OFFHAND).getItem();
        var mainStack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        var offStack = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if (mainhanditem instanceof BowItem || offhanditem instanceof BowItem) {
            doEnchant(lookup, Enchantments.POWER, 3, entity);
            }
        if (mainStack.is(ItemTags.AXES) || mainStack.is(ItemTags.SWORDS) || offStack.is(ItemTags.SWORDS) || offStack.is(ItemTags.AXES)) {
            doEnchant(lookup, Enchantments.SHARPNESS, 3, entity);
        }
        if (mainhanditem instanceof CrossbowItem || offhanditem instanceof CrossbowItem) {
            Random random = new Random();
            int randvalue = random.nextInt(2);
            if (randvalue == 1) {
                doEnchant(lookup, Enchantments.PIERCING, 4, entity);
            }
            if (randvalue == 0) {
                doEnchant(lookup, Enchantments.MULTISHOT, 1, entity);
            }
        }
        entity.setItemSlot(EquipmentSlot.MAINHAND, mainStack);
        entity.setItemSlot(EquipmentSlot.OFFHAND, offStack);
    }
}
