package me.sandbox.world.features.structurefeatures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.sandbox.world.features.StructureRegistry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;
import java.util.function.Function;

public class BaseStructure extends Structure {
    public static final Codec<BaseStructure> CODEC = RecordCodecBuilder.<BaseStructure>mapCodec((instance) -> {
        return instance.group(configCodecBuilder(instance), StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter((structure) -> {
            return structure.startPool;
        }), Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((structure) -> {
            return structure.startJigsawName;
        }), Codec.intRange(0, 7).fieldOf("size").forGetter((structure) -> {
            return structure.size;
        }), HeightProvider.CODEC.fieldOf("start_height").forGetter((structure) -> {
            return structure.startHeight;
        }), Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((structure) -> {
            return structure.projectStartToHeightmap;
        }), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((structure) -> {
            return structure.maxDistanceFromCenter;
        })).apply(instance, BaseStructure::new);
    }).flatXmap(createValidator(), createValidator()).codec();
    protected final RegistryEntry<StructurePool> startPool;
    protected final Optional<Identifier> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final Optional<Heightmap.Type> projectStartToHeightmap;
    protected final int maxDistanceFromCenter;

    public BaseStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public BaseStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, Heightmap.Type projectStartToHeightmap) {
        this(config, startPool, Optional.empty(), size, startHeight, Optional.of(projectStartToHeightmap), 80);
    }

    public BaseStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight) {
        this(config, startPool, Optional.empty(), size, startHeight, Optional.empty(), 80);
    }

    public Optional<StructurePosition> getStructurePosition(Context context) {
        if (!canGenerate(context)) {
            return Optional.empty();
        }
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());

        return StructurePoolBasedGenerator.generate(context, this.startPool, this.startJigsawName, this.size, blockPos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter, StructurePoolAliasLookup.EMPTY);
    }

    public StructureType<?> getType() {
        return StructureRegistry.BASE_STRUCTURE;
    }

    public boolean canGenerate(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = chunkPos.x >> 4;
        int j = chunkPos.z >> 4;
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        }
        return !context.chunkGenerator().createStructurePlacementCalculator(context.dynamicRegistryManager().getWrapperOrThrow(RegistryKeys.STRUCTURE_SET),
                context.noiseConfig(),
                context.seed()
                ).canGenerate(context.dynamicRegistryManager().get(RegistryKeys.STRUCTURE_SET).entryOf(StructureSetKeys.VILLAGES), chunkPos.x, chunkPos.z, 10);
    }

    private static Function<BaseStructure, DataResult<BaseStructure>> createValidator() {
        return (feature) -> {
            byte var10000;
            switch(feature.getTerrainAdaptation()) {
                case NONE:
                    var10000 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                case BEARD_BOX:
                    var10000 = 12;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            int i = var10000;
            return feature.maxDistanceFromCenter + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") : DataResult.success(feature);
        };
    }
}
