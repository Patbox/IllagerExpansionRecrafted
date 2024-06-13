package eu.pb4.illagerexpansion.world.features;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.world.features.structurefeatures.BaseStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;

public class StructureRegistry {
    public static final StructureType<BaseStructure> BASE_STRUCTURE = Registry.register(Registries.STRUCTURE_TYPE, Identifier.of(IllagerExpansion.MOD_ID, "base_structure"), () -> BaseStructure.CODEC);


    public static void registerStructureFeatures() {
    }
}
