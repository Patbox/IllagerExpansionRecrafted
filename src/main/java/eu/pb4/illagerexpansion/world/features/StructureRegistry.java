package eu.pb4.illagerexpansion.world.features;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.illagerexpansion.world.features.structurefeatures.BaseStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class StructureRegistry {
    public static final StructureType<BaseStructure> BASE_STRUCTURE = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, "base_structure"), () -> BaseStructure.CODEC);


    public static void registerStructureFeatures() {
    }
}
