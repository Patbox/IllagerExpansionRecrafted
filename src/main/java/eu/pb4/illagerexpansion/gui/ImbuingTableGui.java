package eu.pb4.illagerexpansion.gui;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.util.IEGameRules;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ImbuingTableGui extends SimpleGui {
    protected final CraftingResultInventory output = new CraftingResultInventory();
    private int cost;

    public ImbuingTableGui(ServerPlayerEntity player) {
        super(PolymerResourcePackUtils.hasMainPack(player) ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_9X4, player, false);

        this.setTitle(PolymerResourcePackUtils.hasMainPack(player)
                ? Text.empty().append(Text.literal("-0.")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(new StyleSpriteSource.Font(Identifier.of(IllagerExpansion.MOD_ID, "gui")))))
                .append(Text.literal("Imbue"))
                : Text.literal("Imbue")
        );

        this.setSlotRedirect(18 + 1, new Slot(this.input, 0, 26, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.ENCHANTED_BOOK);
            }
        });

        this.setSlotRedirect(18 + 4, new Slot(this.input, 1, 80, 54));

        this.setSlotRedirect(18 + 7, new Slot(this.input, 2, 134, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ItemRegistry.HALLOWED_GEM);
            }
        });

        this.setSlotRedirect(4, new Slot(this.output, 3, 80, 14) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return !getStack().isEmpty() && (playerEntity.isCreative() || playerEntity.experienceLevel >= ImbuingTableGui.this.cost);
            }

            @Override
            public void onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                if (!playerEntity.isCreative()) {
                    playerEntity.experienceLevel -= ImbuingTableGui.this.cost;
                }
                input.setStack(0, ItemStack.EMPTY);
                input.setStack(1, ItemStack.EMPTY);
                input.getStack(2).increment(-1);
                playerEntity.getEntityWorld().playSound(null, player.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundRegistry.SORCERER_COMPLETE_CAST, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        });

        if (!PolymerResourcePackUtils.hasMainPack(player)) {
            this.setSlot(27 + 1, new GuiElementBuilder(Items.BOOK).setName(Text.empty()));
            this.setSlot(27 + 4, new GuiElementBuilder(Items.STICK).setName(Text.empty()));
            this.setSlot(27 + 7, new GuiElementBuilder(Items.EMERALD).setName(Text.empty()));


            var filler = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).setName(Text.empty());
            while (this.getFirstEmptySlot() != -1) {
                this.addSlot(filler);
            }
        }

        this.open();
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
        if (action == SlotActionType.QUICK_MOVE) {
            var slot = this.getSlotRedirect(index);
            if (slot != null && slot.inventory == output && slot.hasStack()) {
                var firstEmpty = this.player.getInventory().getEmptySlot();
                if (firstEmpty != -1) {
                    var stack = slot.getStack();
                    slot.onTakeItem(this.player, stack);
                    slot.setStack(ItemStack.EMPTY);
                    this.player.getInventory().setStack(firstEmpty, stack);
                    return false;
                }

                return false;
            }
        }

        return super.onAnyClick(index, type, action);
    }

    @Override
    public void onScreenHandlerClosed() {
        super.onScreenHandlerClosed();
        this.player.giveOrDropStack(this.input.getStack(0));
        this.player.giveOrDropStack(this.input.getStack(1));
        this.player.giveOrDropStack(this.input.getStack(2));
        this.input.clear();
        this.output.clear();
    }    final Inventory input = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            ImbuingTableGui.this.onContentChanged(this);
        }
    };

    private void onContentChanged(SimpleInventory inventory) {
        if (inventory == this.input) {
            this.updateResult();
        }
    }

    public void updateResult() {
        ItemStack imbuingItem = this.input.getStack(1);
        ItemStack book = this.input.getStack(0);
        ItemStack gem = this.input.getStack(2);
        ItemStack imbuingResult = imbuingItem.copy();
        var bookEnchantments = book.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        if (!book.isEmpty() && bookEnchantments.getSize() == 1 && !gem.isEmpty() && !imbuingItem.isEmpty()) {
            var bookEnchantment = bookEnchantments.getEnchantments().stream().findAny().get();

            if (bookEnchantment.value().getMaxLevel() == 1 || !bookEnchantment.value().isAcceptableItem(imbuingItem)) {
                output.setStack(0, ItemStack.EMPTY);
                return;
            }

            var gamerules = this.player.getEntityWorld().getGameRules();

            int imbueLevel = bookEnchantments.getLevel(bookEnchantment) + 1;

            var cost = Math.max(bookEnchantment.value().getMinPower(imbueLevel) * gamerules.get(IEGameRules.XP_COST_BOOK_MULTIPLIER).get(),
                    gamerules.get(IEGameRules.XP_COST_BOOK_MIN).get());

            var itemMin = gamerules.get(IEGameRules.XP_COST_ITEM_MIN).get();
            var itemMul = gamerules.get(IEGameRules.XP_COST_ITEM_MULTIPLIER).get();

            var toolMap = imbuingItem.getEnchantments();
            var newEnch = new ItemEnchantmentsComponent.Builder(bookEnchantments);
            for (var imbueEnchant : toolMap.getEnchantments()) {
                if (!Enchantment.canBeCombined(bookEnchantment, imbueEnchant)) {
                    output.setStack(0, ItemStack.EMPTY);
                    return;
                }

                int level = toolMap.getLevel(imbueEnchant);
                newEnch.add(imbueEnchant, level);
                cost += Math.max(imbueEnchant.value().getMinPower(imbueLevel) * itemMul, itemMin);
            }

            newEnch.add(bookEnchantment, imbueLevel);
            imbuingResult.set(DataComponentTypes.ENCHANTMENTS, newEnch.build());
            if (cost > gamerules.getInt(IEGameRules.XP_COST_MAX) && !this.player.isCreative()) {
                this.cost = 0;
                output.setStack(0, ItemStack.EMPTY);
                this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).hideDefaultTooltip()
                        .setName(Text.translatable("container.repair.expensive").formatted(Formatting.RED)));
                return;
            }
            this.cost = MathHelper.ceil(cost);
            if (this.player.experienceLevel >= this.cost || this.player.isCreative()) {
                this.output.setStack(0, imbuingResult);
            } else {
                output.setStack(0, ItemStack.EMPTY);
            }
            this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).hideDefaultTooltip()
                    .setName(Text.translatable("container.repair.cost", this.cost)
                            .formatted(this.player.experienceLevel >= this.cost ? Formatting.GREEN : Formatting.RED))
            );
        } else {
            output.setStack(0, ItemStack.EMPTY);
            this.clearSlot(2);
        }
    }




}
