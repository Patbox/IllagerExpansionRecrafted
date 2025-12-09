package eu.pb4.illagerexpansion.gui;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.util.IEGameRules;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ImbuingTableGui extends SimpleGui {
    protected final ResultContainer output = new ResultContainer();
    private int cost;

    public ImbuingTableGui(ServerPlayer player) {
        super(PolymerResourcePackUtils.hasMainPack(player) ? MenuType.GENERIC_9x3 : MenuType.GENERIC_9x4, player, false);

        this.setTitle(PolymerResourcePackUtils.hasMainPack(player)
                ? Component.empty().append(Component.literal("-0.")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "gui")))))
                .append(Component.literal("Imbue"))
                : Component.literal("Imbue")
        );

        this.setSlotRedirect(18 + 1, new Slot(this.input, 0, 26, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.ENCHANTED_BOOK);
            }
        });

        this.setSlotRedirect(18 + 4, new Slot(this.input, 1, 80, 54));

        this.setSlotRedirect(18 + 7, new Slot(this.input, 2, 134, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ItemRegistry.HALLOWED_GEM);
            }
        });

        this.setSlotRedirect(4, new Slot(this.output, 3, 80, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player playerEntity) {
                return !getItem().isEmpty() && (playerEntity.isCreative() || playerEntity.experienceLevel >= ImbuingTableGui.this.cost);
            }

            @Override
            public void onTake(Player playerEntity, ItemStack itemStack) {
                if (!playerEntity.isCreative()) {
                    playerEntity.experienceLevel -= ImbuingTableGui.this.cost;
                }
                input.setItem(0, ItemStack.EMPTY);
                input.setItem(1, ItemStack.EMPTY);
                input.getItem(2).grow(-1);
                playerEntity.level().playSound(null, player.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundRegistry.SORCERER_COMPLETE_CAST, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        });

        if (!PolymerResourcePackUtils.hasMainPack(player)) {
            this.setSlot(27 + 1, new GuiElementBuilder(Items.BOOK).setName(Component.empty()));
            this.setSlot(27 + 4, new GuiElementBuilder(Items.STICK).setName(Component.empty()));
            this.setSlot(27 + 7, new GuiElementBuilder(Items.EMERALD).setName(Component.empty()));


            var filler = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).setName(Component.empty());
            while (this.getFirstEmptySlot() != -1) {
                this.addSlot(filler);
            }
        }

        this.open();
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, net.minecraft.world.inventory.ClickType action) {
        if (action == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
            var slot = this.getSlotRedirect(index);
            if (slot != null && slot.container == output && slot.hasItem()) {
                var firstEmpty = this.player.getInventory().getFreeSlot();
                if (firstEmpty != -1) {
                    var stack = slot.getItem();
                    slot.onTake(this.player, stack);
                    slot.setByPlayer(ItemStack.EMPTY);
                    this.player.getInventory().setItem(firstEmpty, stack);
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
        this.player.handleExtraItemsCreatedOnUse(this.input.getItem(0));
        this.player.handleExtraItemsCreatedOnUse(this.input.getItem(1));
        this.player.handleExtraItemsCreatedOnUse(this.input.getItem(2));
        this.input.clearContent();
        this.output.clearContent();
    }    final Container input = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            ImbuingTableGui.this.onContentChanged(this);
        }
    };

    private void onContentChanged(SimpleContainer inventory) {
        if (inventory == this.input) {
            this.updateResult();
        }
    }

    public void updateResult() {
        ItemStack imbuingItem = this.input.getItem(1);
        ItemStack book = this.input.getItem(0);
        ItemStack gem = this.input.getItem(2);
        ItemStack imbuingResult = imbuingItem.copy();
        var bookEnchantments = book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!book.isEmpty() && bookEnchantments.size() == 1 && !gem.isEmpty() && !imbuingItem.isEmpty()) {
            var bookEnchantment = bookEnchantments.keySet().stream().findAny().get();

            if (bookEnchantment.value().getMaxLevel() == 1 || !bookEnchantment.value().canEnchant(imbuingItem)) {
                output.setItem(0, ItemStack.EMPTY);
                return;
            }

            var gamerules = this.player.level().getGameRules();

            int imbueLevel = bookEnchantments.getLevel(bookEnchantment) + 1;

            var cost = Math.max(bookEnchantment.value().getMinCost(imbueLevel) * gamerules.get(IEGameRules.XP_COST_BOOK_MULTIPLIER),
                    gamerules.get(IEGameRules.XP_COST_BOOK_MIN));

            var itemMin = gamerules.get(IEGameRules.XP_COST_ITEM_MIN);
            var itemMul = gamerules.get(IEGameRules.XP_COST_ITEM_MULTIPLIER);

            var toolMap = imbuingItem.getEnchantments();
            var newEnch = new ItemEnchantments.Mutable(bookEnchantments);
            for (var imbueEnchant : toolMap.keySet()) {
                if (!Enchantment.areCompatible(bookEnchantment, imbueEnchant)) {
                    output.setItem(0, ItemStack.EMPTY);
                    return;
                }

                int level = toolMap.getLevel(imbueEnchant);
                newEnch.upgrade(imbueEnchant, level);
                cost += Math.max(imbueEnchant.value().getMinCost(imbueLevel) * itemMul, itemMin);
            }

            newEnch.upgrade(bookEnchantment, imbueLevel);
            imbuingResult.set(DataComponents.ENCHANTMENTS, newEnch.toImmutable());
            if (cost > gamerules.get(IEGameRules.XP_COST_MAX) && !this.player.isCreative()) {
                this.cost = 0;
                output.setItem(0, ItemStack.EMPTY);
                this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).hideDefaultTooltip()
                        .setName(Component.translatable("container.repair.expensive").withStyle(ChatFormatting.RED)));
                return;
            }
            this.cost = Mth.ceil(cost);
            if (this.player.experienceLevel >= this.cost || this.player.isCreative()) {
                this.output.setItem(0, imbuingResult);
            } else {
                output.setItem(0, ItemStack.EMPTY);
            }
            this.setSlot(2, new GuiElementBuilder(Items.EXPERIENCE_BOTTLE).hideDefaultTooltip()
                    .setName(Component.translatable("container.repair.cost", this.cost)
                            .withStyle(this.player.experienceLevel >= this.cost ? ChatFormatting.GREEN : ChatFormatting.RED))
            );
        } else {
            output.setItem(0, ItemStack.EMPTY);
            this.clearSlot(2);
        }
    }




}
