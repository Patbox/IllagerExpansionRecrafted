package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class PolymerSmithingTemplate extends SmithingTemplateItem implements PolymerAutoItem {
    private static final ChatFormatting TITLE_FORMATTING = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMATTING = ChatFormatting.BLUE;
    private static final Component PLATINUM_UPGRADE_TEXT = Component.translatable(Util.makeDescriptionId("upgrade", id("platinum_upgrade_template")))
            .withStyle(TITLE_FORMATTING);
    private static final Component PLATINUM_UPGRADE_APPLIES_TO_TEXT = Component.translatable(
                    Util.makeDescriptionId("item", id("platinum_upgrade_template.applies_to"))
            )
            .withStyle(DESCRIPTION_FORMATTING);
    private static final Component PLATINUM_UPGRADE_INGREDIENTS_TEXT = Component.translatable(
                    Util.makeDescriptionId("item", id("platinum_upgrade_template.ingredients"))
            )
            .withStyle(DESCRIPTION_FORMATTING);


    public PolymerSmithingTemplate(Component appliesToText, Component ingredientsText, Component baseSlotDescriptionText, Component additionsSlotDescriptionText, List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures, Item.Properties settings) {
        super(appliesToText, ingredientsText, baseSlotDescriptionText, additionsSlotDescriptionText, emptyBaseSlotTextures, emptyAdditionsSlotTextures, settings);
    }

    public static PolymerSmithingTemplate createPlatinumUpgradeTemplate(Item.Properties settings) {
        return new PolymerSmithingTemplate(
                PLATINUM_UPGRADE_APPLIES_TO_TEXT,
                PLATINUM_UPGRADE_INGREDIENTS_TEXT,
                Component.empty(),
                Component.empty(),
                List.of(),
                List.of(),
                settings
        );
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE;
    }
}
