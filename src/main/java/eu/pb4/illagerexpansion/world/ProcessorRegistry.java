package eu.pb4.illagerexpansion.world;

import com.google.common.collect.ImmutableList;
import eu.pb4.illagerexpansion.IllagerExpansion;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;


public class ProcessorRegistry {
    public static StructureProcessorType<NoWaterlogProcessor> NO_WATERLOG_PROCESSOR = () -> NoWaterlogProcessor.CODEC;

    public static void registerProcessors() {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "waterlog"), NO_WATERLOG_PROCESSOR);
    }
}
