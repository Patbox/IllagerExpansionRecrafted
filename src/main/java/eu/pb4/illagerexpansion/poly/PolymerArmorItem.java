package eu.pb4.illagerexpansion.poly;


import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import xyz.nucleoid.packettweaker.PacketContext;

public class PolymerArmorItem extends Item implements PolymerAutoItem {
    private final Item polymerItemBase;

    public PolymerArmorItem(ArmorMaterial material, ArmorType slot, Properties settings) {
        super(settings.humanoidArmor(material, slot));
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
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        var out = PolymerAutoItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        if (!PolymerCommonUtils.hasResourcePack(context.getClientConnection(), PolymerResourcePackUtils.getMainUuid())) {
            out.set(DataComponents.EQUIPPABLE, this.polymerItemBase.components().get(DataComponents.EQUIPPABLE));
        }

        return out;
    }
}
