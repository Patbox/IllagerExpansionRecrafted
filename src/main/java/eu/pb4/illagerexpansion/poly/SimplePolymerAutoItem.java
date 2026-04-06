package eu.pb4.illagerexpansion.poly;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class SimplePolymerAutoItem extends Item implements PolymerAutoItem {
    private final Item polymerItem;

    public SimplePolymerAutoItem(Properties settings, Item polymerItem) {
        super(settings);
        this.polymerItem = polymerItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return this.polymerItem;
    }
}
