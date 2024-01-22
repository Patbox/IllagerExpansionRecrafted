package me.sandbox.world;

import com.google.common.collect.ImmutableList;
import me.sandbox.IllagerExpansion;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;


public class ProcessorRegistry {
    public static StructureProcessorType<NoWaterlogProcessor> NO_WATERLOG_PROCESSOR = () -> NoWaterlogProcessor.CODEC;

    public static final RegistryEntry<StructureProcessorList> WATERLOGGED_LIST = ProcessorRegistry.register("waterlogged_processor_list", ImmutableList.of(new NoWaterlogProcessor()));


    public static void registerProcessors() {
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(IllagerExpansion.MOD_ID, "waterlog"), NO_WATERLOG_PROCESSOR);
    }
    public static RegistryEntry<StructureProcessorList> register(String id, ImmutableList<StructureProcessor> processorList) {
        Identifier identifier = new Identifier(IllagerExpansion.MOD_ID, id);
        StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
        return RegistryEntry.of(structureProcessorList);
        //return BuiltinRegistries.add(RegistryKeys.STRUCTURE_PROCESSOR, identifier, structureProcessorList);
    }
}
