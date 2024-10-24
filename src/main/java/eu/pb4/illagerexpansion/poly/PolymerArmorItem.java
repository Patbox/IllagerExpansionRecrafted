package eu.pb4.illagerexpansion.poly;


import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.tooltip.TooltipType;
import xyz.nucleoid.packettweaker.PacketContext;

public class PolymerArmorItem extends ArmorItem implements PolymerAutoItem {
    private final Item polymerItemBase;

    public PolymerArmorItem(ArmorMaterial material, EquipmentType slot, Settings settings) {
        super(material, slot, settings);
        this.polymerItemBase = switch (slot) {
            case HELMET -> Items.NETHERITE_HELMET;
            case CHESTPLATE -> Items.NETHERITE_CHESTPLATE;
            case LEGGINGS -> Items.NETHERITE_LEGGINGS;
            case BOOTS -> Items.NETHERITE_BOOTS;
            default -> Items.STICK;
        };
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return this.polymerItemBase;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        var out = PolymerAutoItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        if (!PolymerCommonUtils.hasResourcePack(context.getClientConnection(), PolymerResourcePackUtils.getMainUuid())) {
            out.set(DataComponentTypes.EQUIPPABLE, this.polymerItemBase.getComponents().get(DataComponentTypes.EQUIPPABLE));
        }

        return out;
    }
}
