package eu.pb4.illagerexpansion.gui;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ImbuingTableGui extends SimpleGui {
    protected final CraftingResultInventory output = new CraftingResultInventory();
    private int cost;

    public ImbuingTableGui(ServerPlayerEntity player) {
        super(PolymerResourcePackUtils.hasMainPack(player) ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_9X4, player, false);

        this.setTitle(PolymerResourcePackUtils.hasMainPack(player)
                ? Text.empty().append(Text.literal("-0.")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(new Identifier(IllagerExpansion.MOD_ID, "gui"))))
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
                playerEntity.getWorld().playSound(null, player.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundRegistry.SORCERER_COMPLETE_CAST, SoundCategory.PLAYERS, 1.0f, 1.0f);
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
    }    final Inventory input = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            ImbuingTableGui.this.onContentChanged(this);
        }
    };

    @Override
    public void onClose() {
        this.player.giveItemStack(this.input.getStack(0));
        this.player.giveItemStack(this.input.getStack(1));
        this.player.giveItemStack(this.input.getStack(2));
        this.input.clear();
        this.output.clear();
    }

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
        Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.get(book);
        if (!book.isEmpty() && bookEnchantments.size() == 1 && !gem.isEmpty() && !imbuingItem.isEmpty()) {
            var bookEnchantment = bookEnchantments.keySet().stream().findAny().get();

            if (bookEnchantment.getMaxLevel() == 1 || !bookEnchantment.isAcceptableItem(imbuingItem)) {
                output.setStack(0, ItemStack.EMPTY);
                return;
            }


            int imbueLevel = bookEnchantments.get(bookEnchantment) + 1;

            int cost = Math.max(bookEnchantment.getMinPower(imbueLevel) / 3, 8);

            Map<Enchantment, Integer> toolMap = EnchantmentHelper.get(imbuingItem);
            for (Enchantment imbueEnchant : toolMap.keySet()) {
                if (!imbueEnchant.canCombine(bookEnchantment) || !bookEnchantment.canCombine(imbueEnchant)) {
                    output.setStack(0, ItemStack.EMPTY);
                    return;
                }

                int level = toolMap.getOrDefault(imbueEnchant, 0);
                bookEnchantments.put(imbueEnchant, level);
                cost += Math.max(imbueEnchant.getMinPower(imbueLevel) / 5, 5);
            }
            bookEnchantments.put(bookEnchantment, imbueLevel);
            EnchantmentHelper.set(bookEnchantments, imbuingResult);
            if (cost > 35) {
                this.cost = 0;
                output.setStack(0, ItemStack.EMPTY);
                this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE)
                        .setName(Text.translatable("container.repair.expensive", cost).formatted(Formatting.RED)));
                return;
            }
            this.output.setStack(0, imbuingResult);
            this.cost = cost;
            this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE)
                    .setName(Text.translatable("container.repair.cost", cost).formatted(Formatting.GREEN))
            );
        } else {
            output.setStack(0, ItemStack.EMPTY);
            this.clearSlot(2);
        }
    }




}
