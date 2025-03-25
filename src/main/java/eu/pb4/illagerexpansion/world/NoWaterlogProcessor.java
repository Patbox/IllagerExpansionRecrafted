package eu.pb4.illagerexpansion.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Waterloggable;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;

//Credit to TelepathicGrunt for providing the anti-waterlogging processor!!

public class NoWaterlogProcessor extends StructureProcessor {
    public static MapCodec<NoWaterlogProcessor> CODEC = MapCodec.unit(NoWaterlogProcessor::new);

    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        ChunkPos currentChunkPos = new ChunkPos(structureBlockInfo2.pos());
        if (structureBlockInfo2.state().getBlock() instanceof Waterloggable) {
            Chunk currentChunk = world.getChunk(currentChunkPos.x, currentChunkPos.z);
            if (world.getFluidState(structureBlockInfo2.pos()).isIn(FluidTags.WATER)) {
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
