package eu.pb4.illagerexpansion.world.features.structurefeatures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.illagerexpansion.world.features.StructureRegistry;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class BaseStructure extends Structure {
    public static final MapCodec<BaseStructure> CODEC = RecordCodecBuilder.<BaseStructure>mapCodec((instance) -> {
        return instance.group(settingsCodec(instance), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((structure) -> {
            return structure.startPool;
        }), Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((structure) -> {
            return structure.startJigsawName;
        }), Codec.intRange(0, 7).fieldOf("size").forGetter((structure) -> {
            return structure.size;
        }), HeightProvider.CODEC.fieldOf("start_height").forGetter((structure) -> {
            return structure.startHeight;
        }), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((structure) -> {
            return structure.projectStartToHeightmap;
        }), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((structure) -> {
            return structure.maxDistanceFromCenter;
        })).apply(instance, BaseStructure::new);
    }).flatXmap(createValidator(), createValidator());
    protected final Holder<StructureTemplatePool> startPool;
    protected final Optional<Identifier> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final Optional<Heightmap.Types> projectStartToHeightmap;
    protected final int maxDistanceFromCenter;

    public BaseStructure(StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public BaseStructure(StructureSettings config, Holder<StructureTemplatePool> startPool, int size, HeightProvider startHeight, Heightmap.Types projectStartToHeightmap) {
        this(config, startPool, Optional.empty(), size, startHeight, Optional.of(projectStartToHeightmap), 80);
    }

    public BaseStructure(StructureSettings config, Holder<StructureTemplatePool> startPool, int size, HeightProvider startHeight) {
        this(config, startPool, Optional.empty(), size, startHeight, Optional.empty(), 80);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!canGenerate(context)) {
            return Optional.empty();
        }
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());

        return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.size, blockPos, false, this.projectStartToHeightmap, new JigsawStructure.MaxDistance(this.maxDistanceFromCenter), PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
    }

    public StructureType<?> type() {
        return StructureRegistry.BASE_STRUCTURE;
    }

    public boolean canGenerate(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = chunkPos.x() >> 4;
        int j = chunkPos.z() >> 4;
        WorldgenRandom chunkRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        }
        return !context.chunkGenerator().createState(context.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET),
                context.randomState(),
                context.seed()
                ).hasStructureChunkInRange(context.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).getOrThrow(BuiltinStructureSets.VILLAGES), chunkPos.x(), chunkPos.z(), 10);
    }

    private static Function<BaseStructure, DataResult<BaseStructure>> createValidator() {
        return (feature) -> {
            byte var10000;
            switch(feature.terrainAdaptation()) {
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
