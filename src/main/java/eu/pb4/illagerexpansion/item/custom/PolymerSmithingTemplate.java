package eu.pb4.illagerexpansion.item.custom;

import eu.pb4.illagerexpansion.poly.PolymerAutoItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class PolymerSmithingTemplate extends SmithingTemplateItem implements PolymerAutoItem {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private static final Text PLATINUM_UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("upgrade", id("platinum_upgrade_template")))
            .formatted(TITLE_FORMATTING);
    private static final Text PLATINUM_UPGRADE_APPLIES_TO_TEXT = Text.translatable(
                    Util.createTranslationKey("item", id("platinum_upgrade_template.applies_to"))
            )
            .formatted(DESCRIPTION_FORMATTING);
    private static final Text PLATINUM_UPGRADE_INGREDIENTS_TEXT = Text.translatable(
                    Util.createTranslationKey("item", id("platinum_upgrade_template.ingredients"))
            )
            .formatted(DESCRIPTION_FORMATTING);


    public PolymerSmithingTemplate(Text appliesToText, Text ingredientsText, Text baseSlotDescriptionText, Text additionsSlotDescriptionText, List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures, Item.Settings settings) {
        super(appliesToText, ingredientsText, baseSlotDescriptionText, additionsSlotDescriptionText, emptyBaseSlotTextures, emptyAdditionsSlotTextures, settings);
    }

    public static PolymerSmithingTemplate createPlatinumUpgradeTemplate(Item.Settings settings) {
        return new PolymerSmithingTemplate(
                PLATINUM_UPGRADE_APPLIES_TO_TEXT,
                PLATINUM_UPGRADE_INGREDIENTS_TEXT,
                Text.empty(),
                Text.empty(),
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
