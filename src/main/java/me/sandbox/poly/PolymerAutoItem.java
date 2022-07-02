package me.sandbox.poly;

import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PolymerAutoItem extends PolymerItem {

    @Override
    default int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return PolymerModels.MODELS.get(this).value();
    }
}
