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


public class SorcererHutFeature extends BaseStructure {
    // static RegistryEntry<StructurePool> HUT_POOL = StructurePools.register(new StructurePool(new Identifier("illagerexp:sorcerer_hut_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:sorcerer_hut"), 1)), StructurePool.Projection.RIGID));
    public static RegistryEntry<StructurePool> HUT_POOL = RegistryEntry.of(new StructurePool(null, ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("illagerexp:sorcerer_hut"), 1)), StructurePool.Projection.RIGID));

    public SorcererHutFeature(Config config) {
        super(config, HUT_POOL, 6, ConstantHeightProvider.create(YOffset.fixed(0)), Heightmap.Type.WORLD_SURFACE_WG);
    }

}

