package eu.pb4.illagerexpansion.entity;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.entity.projectile.HatchetEntity;
import eu.pb4.illagerexpansion.entity.projectile.MagmaEntity;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.poly.PlayerPolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class EntityRegistry {


    //Register Entities
    public static final EntityType<ProvokerEntity> PROVOKER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "provoker"),
            EntityType.Builder.of(ProvokerEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );

    public static final EntityType<InvokerEntity> INVOKER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "invoker"),
            EntityType.Builder.of(InvokerEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<BasherEntity> BASHER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "basher"),
            EntityType.Builder.of(BasherEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<SorcererEntity> SORCERER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "sorcerer"),
            EntityType.Builder.of(SorcererEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<ArchivistEntity> ARCHIVIST = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "archivist"),
            EntityType.Builder.of(ArchivistEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<InquisitorEntity> INQUISITOR = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "inquisitor"),
            EntityType.Builder.of(InquisitorEntity::new, MobCategory.MONSTER).sized(0.5f, 2.48f)
    );
    public static final EntityType<MarauderEntity> MARAUDER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "marauder"),
            EntityType.Builder.of(MarauderEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<AlchemistEntity> ALCHEMIST = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "alchemist"),
            EntityType.Builder.of(AlchemistEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<FirecallerEntity> FIRECALLER = registerIllager(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "firecaller"),
            EntityType.Builder.of(FirecallerEntity::new, MobCategory.MONSTER).sized(0.5f, 1.92f)
    );
    public static final EntityType<SurrenderedEntity> SURRENDERED = register(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "surrendered"),
            EntityType.Builder.of(SurrenderedEntity::new, MobCategory.MONSTER).sized(0.5f, 1.42f).fireImmune()
    );
    public static final EntityType<HatchetEntity> HATCHET = register(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "hatchet"),
            EntityType.Builder.<HatchetEntity>of(HatchetEntity::new, MobCategory.MISC).sized(0.35f, 0.35f).clientTrackingRange(4).updateInterval(10)
    );
    public static final EntityType<InvokerFangsEntity> INVOKER_FANGS = register(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "invoker_fangs"),
            EntityType.Builder.<InvokerFangsEntity>of(InvokerFangsEntity::new, MobCategory.MISC).sized(0.65f, 1.05f)
    );
    public static final EntityType<MagmaEntity> MAGMA = register(Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "magma"),
            EntityType.Builder.<MagmaEntity>of(MagmaEntity::new, MobCategory.MISC).sized(0.95f, 1.05f)
    );

    private static <T extends Entity> EntityType<T> registerIllager(Identifier provoker, EntityType.Builder<T> build) {
        var x = register(provoker, build);
        var stack = new ItemStackTemplate(Items.BIRCH_BUTTON, DataComponentPatch.builder().set(DataComponents.ITEM_MODEL, ResourcePackExtras.bridgeModel(provoker.withPrefix("pbentity/"))).build());
        PlayerPolymerEntity.HEADS.put(x, stack);
        return x;
    }

    private static <T extends Entity> EntityType<T> register(Identifier provoker, EntityType.Builder<T> build) {
        var type = Registry.register(BuiltInRegistries.ENTITY_TYPE, provoker, build.build(ResourceKey.create(Registries.ENTITY_TYPE, provoker)));
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

        LootTableEvents.MODIFY.register((key, builder, source, wrapperLookup) -> {
            if (!key.identifier().getPath().equals("entities/illusioner")) {
                return;
            }

            builder.pool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    .add(LootItem.lootTableItem(ItemRegistry.ILLUSIONARY_DUST)).build());
        });
    }
}
