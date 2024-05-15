package eu.pb4.illagerexpansion.util;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class IllagerUtil {
    private static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    public static boolean ifWearing(LivingEntity entity, Item item, Runnable runnable) {
        var done = new MutableBoolean(true);
        ifTrinkets(entity, item, runnable, done);
        return !done.booleanValue();
    }

    private static void ifTrinkets(LivingEntity entity, Item item, Runnable runnable, MutableBoolean done) {
        if (TRINKETS_LOADED) {
            TrinketsApi.getTrinketComponent(entity).ifPresent(c -> c.forEach((slotReference, stack) -> {
                if (stack.isOf(item) && done.booleanValue()) {
                    runnable.run();
                    done.setFalse();
                }
            }));
        }
    }
}
