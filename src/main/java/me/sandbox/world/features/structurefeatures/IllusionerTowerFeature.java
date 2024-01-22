package me.sandbox.world.features.structurefeatures;


import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;

import java.util.Optional;


public class IllusionerTowerFeature extends BaseStructure {
    //public static final RegistryEntry<StructurePool> TOWER_POOLS = StructurePools.register(new StructurePool(new Identifier("illagerexp:illusioner_tower_pools"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-1"), 1), Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-2"), 1), Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-3"), 1)), StructurePool.Projection.RIGID));
    public static final RegistryEntry<StructurePool> TOWER_POOLS = RegistryEntry.of(new StructurePool(null, ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-1"), 1), Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-2"), 1), Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illusioner_tower-3"), 1)), StructurePool.Projection.RIGID));

    public IllusionerTowerFeature(Config config) {
        super(config, TOWER_POOLS, 3, ConstantHeightProvider.create(YOffset.fixed(0)), Heightmap.Type.WORLD_SURFACE_WG);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        if (!canGenerateSimple(context)) {
            return Optional.empty();
        }
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());

        return StructurePoolBasedGenerator.generate(context, this.startPool, this.startJigsawName, this.size, blockPos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter, StructurePoolAliasLookup.EMPTY);
    }

    public static boolean canGenerateSimple(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        return !context.chunkGenerator().createStructurePlacementCalculator(context.dynamicRegistryManager().getWrapperOrThrow(RegistryKeys.STRUCTURE_SET),
                context.noiseConfig(),
                context.seed()
        ).canGenerate(context.dynamicRegistryManager().get(RegistryKeys.STRUCTURE_SET).entryOf(StructureSetKeys.VILLAGES), chunkPos.x, chunkPos.z, 10);
    }
}

