package me.sandbox.world.features.structurefeatures;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;

public class IllagerFortFeature extends BaseStructure {
    //public static RegistryEntry<StructurePool> FORT_POOL = StructurePools.register(new StructurePool(new Identifier("illagerexp:illager_fort_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illager_fort"), 1)), StructurePool.Projection.RIGID));
    public static RegistryEntry<StructurePool> FORT_POOL = RegistryEntry.of(new StructurePool(null,
            ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:illager_fort"), 1)), StructurePool.Projection.RIGID));

    public IllagerFortFeature(Config config) {
        super(config, null, 4, ConstantHeightProvider.create(YOffset.fixed(0)), Heightmap.Type.WORLD_SURFACE_WG);
    }
}

