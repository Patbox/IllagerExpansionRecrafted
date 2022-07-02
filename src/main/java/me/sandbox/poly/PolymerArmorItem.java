package me.sandbox.poly;

import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.polymer.api.resourcepack.PolymerArmorModel;
import eu.pb4.polymer.api.resourcepack.PolymerModelData;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import me.sandbox.IllagerExpansion;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PolymerArmorItem extends ArmorItem implements PolymerItem {
    private final PolymerModelData polymerTextured;
    private final Item polymerItemBase;
    private final PolymerArmorModel armorModel;

    public PolymerArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
        this.polymerItemBase = switch (slot) {
            case HEAD -> Items.NETHERITE_HELMET;
            case CHEST -> Items.NETHERITE_CHESTPLATE;
            case LEGS -> Items.NETHERITE_LEGGINGS;
            case FEET -> Items.NETHERITE_BOOTS;
            default -> Items.STICK;
        };

        var item = switch (slot) {
            case HEAD -> Items.LEATHER_HELMET;
            case CHEST -> Items.LEATHER_CHESTPLATE;
            case LEGS -> Items.LEATHER_LEGGINGS;
            case FEET -> Items.LEATHER_BOOTS;
            default -> Items.STICK;
        };

        this.polymerTextured = PolymerRPUtils.requestModel(item,
                new Identifier(IllagerExpansion.MOD_ID, "item/" + material.getName() + "_" + (switch (slot) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                })
                ));

        this.armorModel = PolymerRPUtils.requestArmor(new Identifier(material.getName()));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerRPUtils.hasPack(player) ? this.polymerTextured.item() : this.polymerItemBase;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerRPUtils.hasPack(player) ? this.polymerTextured.value() : -1;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerRPUtils.hasPack(player) ? this.armorModel.value() : -1;
    }
}
