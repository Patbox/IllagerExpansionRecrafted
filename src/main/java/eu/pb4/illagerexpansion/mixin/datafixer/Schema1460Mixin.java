package eu.pb4.illagerexpansion.mixin.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

@Mixin(V1460.class)
public abstract class Schema1460Mixin extends Schema {
    public Schema1460Mixin(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Shadow protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {};

    @Inject(method = "registerEntities", at = @At("RETURN"))
    private void registerCustomEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();

        registerMob(schema, map, mod("provoker"));
        registerMob(schema, map, mod("invoker"));
        registerMob(schema, map, mod("basher"));
        registerMob(schema, map, mod("sorcerer"));
        registerMob(schema, map, mod("archivist"));
        registerMob(schema, map, mod("inquisitor"));
        registerMob(schema, map, mod("marauder"));
        registerMob(schema, map, mod("alchemist"));
        registerMob(schema, map, mod("firecaller"));
        registerMob(schema, map, mod("surrendered"));
        registerSimple(map, mod("magma"));
        registerSimple(map, mod("invoker_fangs"));
        register(map, mod("hatchet"), (n) -> DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(schema), "item", References.ITEM_STACK.in(schema)));
    }

    @Unique
    private static String mod(String path) {
        return "illagerexp:" + path;
    }
}