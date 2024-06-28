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
import net.minecraft.util.Identifier;

public class EntityRegistry {


    //Register Entities
    public static final EntityType<ProvokerEntity> PROVOKER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "provoker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ProvokerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );

    public static final EntityType<InvokerEntity> INVOKER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "invoker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InvokerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<BasherEntity> BASHER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "basher"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BasherEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<SorcererEntity> SORCERER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "sorcerer"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SorcererEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<ArchivistEntity> ARCHIVIST = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "archivist"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ArchivistEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<InquisitorEntity> INQUISITOR = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "inquisitor"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InquisitorEntity::new).dimensions(EntityDimensions.fixed(0.5f, 2.48f)).build()
    );
    public static final EntityType<MarauderEntity> MARAUDER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "marauder"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MarauderEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<AlchemistEntity> ALCHEMIST = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "alchemist"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AlchemistEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<FirecallerEntity> FIRECALLER = registerIllager(Identifier.of(IllagerExpansion.MOD_ID, "firecaller"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FirecallerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<SurrenderedEntity> SURRENDERED = register(Identifier.of(IllagerExpansion.MOD_ID, "surrendered"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SurrenderedEntity::new).fireImmune().dimensions(EntityDimensions.fixed(0.5f, 1.42f)).build()
    );
    public static final EntityType<HatchetEntity> HATCHET = register(Identifier.of(IllagerExpansion.MOD_ID, "hatchet"),
            FabricEntityTypeBuilder.<HatchetEntity>create(SpawnGroup.MISC, (HatchetEntity::new)).dimensions(EntityDimensions.fixed(0.35f, 0.35f)).trackRangeBlocks(4).trackedUpdateRate(10).build()
    );
    public static final EntityType<InvokerFangsEntity> INVOKER_FANGS = register(Identifier.of(IllagerExpansion.MOD_ID, "invoker_fangs"),
            FabricEntityTypeBuilder.<InvokerFangsEntity>create(SpawnGroup.MISC, InvokerFangsEntity::new).dimensions(EntityDimensions.fixed(0.65f, 1.05f)).build()
    );
    public static final EntityType<MagmaEntity> MAGMA = register(Identifier.of(IllagerExpansion.MOD_ID, "magma"),
            FabricEntityTypeBuilder.<MagmaEntity>create(SpawnGroup.MISC, MagmaEntity::new).dimensions(EntityDimensions.fixed(0.95f, 1.05f)).build()
    );

    private static <T extends Entity> EntityType<T> registerIllager(Identifier provoker, EntityType<T> build) {
        var x = register(provoker, build);
        var stack = new ItemStack(Items.BIRCH_BUTTON);
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(PolymerResourcePackUtils.requestModel(stack.getItem(), provoker.withPrefixedPath("pbentity/")).value()));
        PlayerPolymerEntity.HEADS.put(x, stack);
        return x;
    }
    private static <T extends Entity> EntityType<T> register(Identifier provoker, EntityType<T> build) {
        Registry.register(Registries.ENTITY_TYPE, provoker, build);
        PolymerEntityUtils.registerType(build);
        return build;
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
