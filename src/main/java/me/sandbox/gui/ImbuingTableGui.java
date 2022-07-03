package me.sandbox.gui;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.sandbox.IllagerExpansion;
import me.sandbox.item.ItemRegistry;
import me.sandbox.sounds.SoundRegistry;
import me.sandbox.util.ImbueUtil;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ImbuingTableGui extends SimpleGui {
    protected final CraftingResultInventory output = new CraftingResultInventory();
    final Inventory input = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            ImbuingTableGui.this.onContentChanged(this);
        }
    };

    public ImbuingTableGui(ServerPlayerEntity player) {
        super(PolymerRPUtils.hasPack(player) ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_9X4, player, false);

        this.setTitle(PolymerRPUtils.hasPack(player)
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
                return true;
            }

            @Override
            public void onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                input.setStack(0, ItemStack.EMPTY);
                input.setStack(1, ItemStack.EMPTY);
                input.getStack(2).increment(-1);
                playerEntity.world.playSound(null, player.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundRegistry.SORCERER_COMPLETE_CAST, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        });

        if (!PolymerRPUtils.hasPack(player)) {
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
        Map<Enchantment, Integer> bookmap = EnchantmentHelper.get(book);
        if (!book.isEmpty() && !gem.isEmpty() && !imbuingItem.isEmpty()) {
            for (Enchantment bookEnchantment : bookmap.keySet()) {
                if (bookmap.size() > 1) {
                    //bigBook = true;
                } else if (ImbueUtil.getBadEnchants().contains(bookEnchantment.getTranslationKey())) {
                    //badEnchant = true;
                } else if (bookmap.getOrDefault(bookEnchantment, 0) != bookEnchantment.getMaxLevel()) {
                    //lowEnchant = true;
                } else if (!bookEnchantment.isAcceptableItem(imbuingItem)) {
                    //badItem = true;
                } else {
                    int imbueLevel = bookmap.get(bookEnchantment) + 1;
                    Map<Enchantment, Integer> imbueMap = EnchantmentHelper.get(imbuingItem);
                    for (Enchantment imbueEnchant : imbueMap.keySet()) {
                        int level = imbueMap.getOrDefault(imbueEnchant, 0);
                        bookmap.put(imbueEnchant, level);
                    }
                    bookmap.put(bookEnchantment, imbueLevel);
                    EnchantmentHelper.set(bookmap, imbuingResult);
                    this.output.setStack(0, imbuingResult);
                    //updateBooleans(false);
                    //this.sendContentUpdates();
                }
            }
        } else {
            output.setStack(0, ItemStack.EMPTY);
        }
    }

}
