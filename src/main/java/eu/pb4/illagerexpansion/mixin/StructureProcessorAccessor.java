package eu.pb4.illagerexpansion.mixin;

import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureProcessor.class)
public interface StructureProcessorAccessor {
    @Invoker("getType")
    StructureProcessorType<?> callGetType();
}
