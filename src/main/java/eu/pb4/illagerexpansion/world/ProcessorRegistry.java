package eu.pb4.illagerexpansion.world;

import com.google.common.collect.ImmutableList;
import eu.pb4.illagerexpansion.IllagerExpansion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;


public class ProcessorRegistry {
    public static StructureProcessorType<NoWaterlogProcessor> NO_WATERLOG_PROCESSOR = () -> NoWaterlogProcessor.CODEC;

    public static void registerProcessors() {
        Registry.register(Registries.STRUCTURE_PROCESSOR, Identifier.of(IllagerExpansion.MOD_ID, "waterlog"), NO_WATERLOG_PROCESSOR);
    }
}
