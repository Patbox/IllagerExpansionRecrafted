package eu.pb4.illagerexpansion.mixin.enumi;

import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinIntrinsics;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpellcasterIllager.IllagerSpell.class)
public enum IllagerSpellMixin {
    IE_ENCHANT(MixinIntrinsics.currentEnumOrdinal(), 0.8, 0.8, 0.2),
    IE_CONJURE_FLAMES(MixinIntrinsics.currentEnumOrdinal(), 1.8, 0.0, 1.8),
    IE_CONJURE_TELEPORT(MixinIntrinsics.currentEnumOrdinal(), 1.5, 1.5, 0.8),
    IE_NECRORAISE(MixinIntrinsics.currentEnumOrdinal(), 0.3, 0.8, 0.05),
    IE_CONJURE_SKULLBOLT(MixinIntrinsics.currentEnumOrdinal(), 0.5, 0.05, 0.05),
    IE_PROVOKE(MixinIntrinsics.currentEnumOrdinal(),1.0,0.8,0.75);
    ;

    @Shadow
    IllagerSpellMixin(final int id, final double red, final double green, final double blue) {

    }
}