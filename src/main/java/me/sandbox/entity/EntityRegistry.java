package me.sandbox.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import me.sandbox.IllagerExpansion;
import me.sandbox.entity.projectile.HatchetEntity;
import me.sandbox.entity.projectile.MagmaEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityRegistry {


    //Register Entities
    public static final EntityType<ProvokerEntity> PROVOKER = register(new Identifier(IllagerExpansion.MOD_ID, "provoker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ProvokerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );

    public static final EntityType<InvokerEntity> INVOKER = register(new Identifier(IllagerExpansion.MOD_ID, "invoker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InvokerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<BasherEntity> BASHER = register(new Identifier(IllagerExpansion.MOD_ID, "basher"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BasherEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<SorcererEntity> SORCERER = register(new Identifier(IllagerExpansion.MOD_ID, "sorcerer"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SorcererEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<ArchivistEntity> ARCHIVIST = register(new Identifier(IllagerExpansion.MOD_ID, "archivist"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ArchivistEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<InquisitorEntity> INQUISITOR = register(new Identifier(IllagerExpansion.MOD_ID, "inquisitor"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InquisitorEntity::new).dimensions(EntityDimensions.fixed(0.5f, 2.48f)).build()
    );
    public static final EntityType<MarauderEntity> MARAUDER = register(new Identifier(IllagerExpansion.MOD_ID, "marauder"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MarauderEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<AlchemistEntity> ALCHEMIST = register(new Identifier(IllagerExpansion.MOD_ID, "alchemist"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AlchemistEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<FirecallerEntity> FIRECALLER = register(new Identifier(IllagerExpansion.MOD_ID, "firecaller"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FirecallerEntity::new).dimensions(EntityDimensions.fixed(0.5f, 1.92f)).build()
    );
    public static final EntityType<SurrenderedEntity> SURRENDERED = register(new Identifier(IllagerExpansion.MOD_ID, "surrendered"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SurrenderedEntity::new).fireImmune().dimensions(EntityDimensions.fixed(0.5f, 1.42f)).build()
    );
    public static final EntityType<HatchetEntity> HATCHET = register(new Identifier(IllagerExpansion.MOD_ID, "hatchet"),
            FabricEntityTypeBuilder.<HatchetEntity>create(SpawnGroup.MISC, (HatchetEntity::new)).dimensions(EntityDimensions.fixed(0.35f, 0.35f)).trackRangeBlocks(4).trackedUpdateRate(10).build()
    );
    public static final EntityType<InvokerFangsEntity> INVOKER_FANGS = register(new Identifier(IllagerExpansion.MOD_ID, "invoker_fangs"),
            FabricEntityTypeBuilder.<InvokerFangsEntity>create(SpawnGroup.MISC, InvokerFangsEntity::new).dimensions(EntityDimensions.fixed(0.65f, 1.05f)).build()
    );
    public static final EntityType<MagmaEntity> MAGMA = register(new Identifier(IllagerExpansion.MOD_ID, "magma"),
            FabricEntityTypeBuilder.<MagmaEntity>create(SpawnGroup.MISC, MagmaEntity::new).dimensions(EntityDimensions.fixed(0.95f, 1.05f)).build()
    );

    private static <T extends Entity> EntityType<T> register(Identifier provoker, EntityType<T> build) {
        Registry.register(Registries.ENTITY_TYPE, provoker, build);
        PolymerEntityUtils.registerType(build);
        return build;
    }

    public static void registerEntities() {
    }

}
