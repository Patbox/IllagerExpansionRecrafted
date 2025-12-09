package eu.pb4.illagerexpansion.mixin;

import eu.pb4.illagerexpansion.world.ProcessorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @Inject(
            method = "placeInWorld",
            at = @At(value = "HEAD")
    )
    private void preventAutoWaterlogging(ServerLevelAccessor world, BlockPos pos, BlockPos pivot, StructurePlaceSettings placementData, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir) {

        if(placementData.getProcessors().stream().anyMatch(processor ->
                ((StructureProcessorAccessor)processor).callGetType() == ProcessorRegistry.NO_WATERLOG_PROCESSOR)) {
            placementData.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }
    }
}