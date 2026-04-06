package eu.pb4.illagerexpansion.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

//Credit to TelepathicGrunt for providing the anti-waterlogging processor!!

public class NoWaterlogProcessor extends StructureProcessor {
    public static MapCodec<NoWaterlogProcessor> CODEC = MapCodec.unit(NoWaterlogProcessor::new);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlaceSettings data) {
        ChunkPos currentChunkPos = ChunkPos.containing(structureBlockInfo2.pos());
        if (structureBlockInfo2.state().getBlock() instanceof SimpleWaterloggedBlock) {
            ChunkAccess currentChunk = world.getChunk(currentChunkPos.x(), currentChunkPos.z());
            if (world.getFluidState(structureBlockInfo2.pos()).is(FluidTags.WATER)) {
                currentChunk.setBlockState(structureBlockInfo2.pos(), structureBlockInfo2.state());
            }
        }
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ProcessorRegistry.NO_WATERLOG_PROCESSOR;
    }
}
