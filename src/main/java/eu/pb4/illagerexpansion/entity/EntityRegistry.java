package eu.pb4.illagerexpansion.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.entity.projectile.HatchetEntity;
import eu.pb4.illagerexpansion.entity.projectile.MagmaEntity;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class EntityRegistry {


    //Register Entities
    public static final EntityType<ProvokerEntity> PROVOKER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "provoker"),
            EntityType.Builder.create(ProvokerEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );

    public static final EntityType<InvokerEntity> INVOKER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "invoker"),
            EntityType.Builder.create(InvokerEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<BasherEntity> BASHER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "basher"),
            EntityType.Builder.create(BasherEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<SorcererEntity> SORCERER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "sorcerer"),
            EntityType.Builder.create(SorcererEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<ArchivistEntity> ARCHIVIST = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "archivist"),
            EntityType.Builder.create(ArchivistEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<InquisitorEntity> INQUISITOR = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "inquisitor"),
            EntityType.Builder.create(InquisitorEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 2.48f)
    );
    public static final EntityType<MarauderEntity> MARAUDER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "marauder"),
            EntityType.Builder.create(MarauderEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<AlchemistEntity> ALCHEMIST = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "alchemist"),
            EntityType.Builder.create(AlchemistEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<FirecallerEntity> FIRECALLER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "firecaller"),
            EntityType.Builder.create(FirecallerEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.92f)
    );
    public static final EntityType<SurrenderedEntity> SURRENDERED = register(Identifier.of(IllagerExpansion.MOD_ID, "surrendered"),
            EntityType.Builder.create(SurrenderedEntity::new, SpawnGroup.MONSTER).dimensions(0.5f, 1.42f).makeFireImmune()
    );
    public static final EntityType<HatchetEntity> HATCHET = register(Identifier.of(IllagerExpansion.MOD_ID, "hatchet"),
            EntityType.Builder.<HatchetEntity>create(HatchetEntity::new, SpawnGroup.MISC).dimensions(0.35f, 0.35f).maxTrackingRange(4).trackingTickInterval(10)
    );
    public static final EntityType<InvokerFangsEntity> INVOKER_FANGS = register(Identifier.of(IllagerExpansion.MOD_ID, "invoker_fangs"),
            EntityType.Builder.<InvokerFangsEntity>create(InvokerFangsEntity::new, SpawnGroup.MISC).dimensions(0.65f, 1.05f)
    );
    public static final EntityType<MagmaEntity> MAGMA = register(Identifier.of(IllagerExpansion.MOD_ID, "magma"),
            EntityType.Builder.<MagmaEntity>create(MagmaEntity::new, SpawnGroup.MISC).dimensions(0.95f, 1.05f)
    );

    private static <T extends Entity> EntityType<T> registerIllager(Identifier provoker, EntityType.Builder<T> build) {
        var x = register(provoker, build);
        var stack = new ItemStack(Items.BIRCH_BUTTON);
        stack.set(DataComponentTypes.ITEM_MODEL, PolymerResourcePackUtils.getBridgedModelId(provoker.withPrefixedPath("pbentity/")));
        PlayerPolymerEntity.HEADS.put(x, stack);
        return x;
    }
    private static <T extends Entity> EntityType<T> register(Identifier provoker, EntityType.Builder<T> build) {
        var type = Registry.register(Registries.ENTITY_TYPE, provoker, build.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, provoker)));
        PolymerEntityUtils.registerType(type);
        return type;
    }

    public static void registerEntities() {
        FabricDefaultAttributeRegistry.register(ALCHEMIST, AlchemistEntity.createAlchemistAttributes());
        FabricDefaultAttributeRegistry.register(ARCHIVIST, ArchivistEntity.createArchivistAttributes());
        FabricDefaultAttributeRegistry.register(BASHER, BasherEntity.createBasherAttributes());
        FabricDefaultAttributeRegistry.register(FIRECALLER, FirecallerEntity.createFirecallerAttributes());
        FabricDefaultAttributeRegistry.register(INQUISITOR, InquisitorEntity.createInquisitorAttributes());
        FabricDefaultAttributeRegistry.register(INVOKER, InvokerEntity.createInvokerAttributes());
        FabricDefaultAttributeRegistry.register(MARAUDER, MarauderEntity.createMarauderAttributes());
        FabricDefaultAttributeRegistry.register(PROVOKER, ProvokerEntity.createProvokerAttributes());
        FabricDefaultAttributeRegistry.register(SORCERER, SorcererEntity.createSorcererAttributes());
        FabricDefaultAttributeRegistry.register(SURRENDERED, SurrenderedEntity.createSurrenderedAttributes());
    }

}
