package eu.pb4.illagerexpansion.util;

import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class TrinketsHelper {
    public static final boolean LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    public static boolean ifWearing(LivingEntity entity, Item item, Runnable runnable) {
        var done = new MutableBoolean(true);
        if (LOADED) {
            ifTrinkets(entity, item, runnable, done);
        }
        return !done.booleanValue();
    }

    private static void ifTrinkets(LivingEntity entity, Item item, Runnable runnable, MutableBoolean done) {
        TrinketsApi.getAttachment(entity).forEach((slotReference, stack) -> {
            if (stack.is(item) && done.booleanValue()) {
                runnable.run();
                done.setFalse();
            }
        });
    }
}
