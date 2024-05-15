package eu.pb4.illagerexpansion.mixin.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.Schema1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1460.class)
public abstract class Schema1460Mixin extends Schema {
    public Schema1460Mixin(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Shadow protected static void targetEntityItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {};

    @Inject(method = "registerEntities", at = @At("RETURN"))
    private void registerCustomEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();

        targetEntityItems(schema, map, mod("provoker"));
        targetEntityItems(schema, map, mod("invoker"));
        targetEntityItems(schema, map, mod("basher"));
        targetEntityItems(schema, map, mod("sorcerer"));
        targetEntityItems(schema, map, mod("archivist"));
        targetEntityItems(schema, map, mod("inquisitor"));
        targetEntityItems(schema, map, mod("marauder"));
        targetEntityItems(schema, map, mod("alchemist"));
        targetEntityItems(schema, map, mod("firecaller"));
        targetEntityItems(schema, map, mod("surrendered"));
        registerSimple(map, mod("magma"));
        registerSimple(map, mod("invoker_fangs"));
        register(map, mod("hatchet"), (n) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema), "item", TypeReferences.ITEM_STACK.in(schema)));
    }

    @Unique
    private static String mod(String path) {
        return "illagerexp:" + path;
    }
}