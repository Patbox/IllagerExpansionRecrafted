package eu.pb4.illagerexpansion.mixin.enumi;

import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Raid.RaiderType.class)
public enum RaiderTypeMixin {
    IE_BASHER(EntityRegistry.BASHER, new int[]{1, 1, 2, 1, 2, 2, 3, 3}),
    IE_PROVOKER(EntityRegistry.PROVOKER, new int[]{0, 1, 1, 0, 1, 1, 2, 2}),
    IE_SORCERER(EntityRegistry.SORCERER, new int[]{0, 0, 0, 0, 0, 1, 1, 1}),
    IE_ARCHIVIST(EntityRegistry.ARCHIVIST, new int[]{0, 1, 0, 1, 1, 1, 2, 3}),
    IE_MARAUDER(EntityRegistry.MARAUDER, new int[]{0, 1, 1, 1, 2, 2, 3, 3}),
    IE_INQUISITOR(EntityRegistry.INQUISITOR, new int[]{0, 0, 0, 0, 1, 0, 1, 1}),
    IE_ALCHEMIST(EntityRegistry.ALCHEMIST, new int[]{0, 0, 0, 1, 2, 1, 2, 2}),
    ;

    @Shadow
    RaiderTypeMixin(final EntityType<? extends Raider> entityType, final int[] spawnsPerWaveBeforeBonus) {
    }
}
