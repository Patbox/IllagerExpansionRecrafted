package eu.pb4.illagerexpansion.poly;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface PolymerAutoItem extends PolymerItem {

    @Override
    default @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return PolymerCommonUtils.hasResourcePack(context.getClientConnection(), PolymerResourcePackUtils.getMainUuid())
                ? PolymerItem.super.getPolymerItemModel(stack, context)
                : null
                ;
    }
}
