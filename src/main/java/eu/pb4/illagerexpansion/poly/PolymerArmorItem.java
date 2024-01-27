package eu.pb4.illagerexpansion.poly;


import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerArmorModel;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.illagerexpansion.IllagerExpansion;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PolymerArmorItem extends ArmorItem implements PolymerItem {
    private final PolymerModelData polymerTextured;
    private final Item polymerItemBase;
    private final PolymerArmorModel armorModel;

    public PolymerArmorItem(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
        this.polymerItemBase = switch (slot) {
            case HELMET -> Items.NETHERITE_HELMET;
            case CHESTPLATE -> Items.NETHERITE_CHESTPLATE;
            case LEGGINGS -> Items.NETHERITE_LEGGINGS;
            case BOOTS -> Items.NETHERITE_BOOTS;
            default -> Items.STICK;
        };

        var item = switch (slot) {
            case HELMET -> Items.LEATHER_HELMET;
            case CHESTPLATE -> Items.LEATHER_CHESTPLATE;
            case LEGGINGS -> Items.LEATHER_LEGGINGS;
            case BOOTS -> Items.LEATHER_BOOTS;
            default -> Items.STICK;
        };

        this.polymerTextured = PolymerResourcePackUtils.requestModel(item,
                new Identifier(IllagerExpansion.MOD_ID, "item/" + material.getName() + "_" + (switch (slot) {
                    case HELMET -> "helmet";
                    case CHESTPLATE -> "chestplate";
                    case LEGGINGS -> "leggings";
                    case BOOTS -> "boots";
                    default -> "";
                })
                ));

        this.armorModel = PolymerResourcePackUtils.requestArmor(new Identifier(material.getName()));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerResourcePackUtils.hasMainPack(player) ? this.polymerTextured.item() : this.polymerItemBase;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerResourcePackUtils.hasMainPack(player) ? this.polymerTextured.value() : -1;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerResourcePackUtils.hasMainPack(player) ? this.armorModel.color() : -1;
    }
}
