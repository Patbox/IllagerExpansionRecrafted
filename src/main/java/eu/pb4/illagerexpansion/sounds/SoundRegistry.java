package eu.pb4.illagerexpansion.sounds;

import eu.pb4.illagerexpansion.IllagerExpansion;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class SoundRegistry {
    public static SoundEvent SURRENDERED_AMBIENT = registerSoundEvent("surrendered_ambient", SoundEvents.STRAY_AMBIENT);
    public static SoundEvent SURRENDERED_HURT = registerSoundEvent("surrendered_hurt", SoundEvents.STRAY_HURT);
    public static SoundEvent SURRENDERED_CHARGE = registerSoundEvent("surrendered_charge", SoundEvents.GHAST_WARN);
    public static SoundEvent SURRENDERED_DEATH = registerSoundEvent("surrendered_death", SoundEvents.STRAY_DEATH);
    public static SoundEvent ARCHIVIST_AMBIENT = registerSoundEvent("archivist_ambient", SoundEvents.ILLUSIONER_AMBIENT);
    public static SoundEvent ARCHIVIST_HURT = registerSoundEvent("archivist_hurt", SoundEvents.ILLUSIONER_HURT);
    public static SoundEvent ARCHIVIST_DEATH = registerSoundEvent("archivist_death", SoundEvents.ILLUSIONER_DEATH);
    public static SoundEvent SORCERER_CAST = registerSoundEvent("sorcerer_cast", SoundEvents.EVOKER_CAST_SPELL);
    public static SoundEvent SORCERER_COMPLETE_CAST = registerSoundEvent("sorcerer_complete_cast", SoundEvents.ILLUSIONER_MIRROR_MOVE);
    public static SoundEvent HORN_OF_SIGHT = registerSoundEvent("horn_of_sight", SoundEvents.EMPTY);
    public static SoundEvent INVOKER_FANGS = registerSoundEvent("invoker_fangs", SoundEvents.EVOKER_FANGS_ATTACK);
    public static SoundEvent INVOKER_HURT = registerSoundEvent("invoker_hurt", SoundEvents.EVOKER_HURT);
    public static SoundEvent INVOKER_DEATH = registerSoundEvent("invoker_death", SoundEvents.EVOKER_DEATH);
    public static SoundEvent INVOKER_AMBIENT = registerSoundEvent("invoker_ambient", SoundEvents.EVOKER_AMBIENT);
    public static SoundEvent INVOKER_COMPLETE_CAST = registerSoundEvent("invoker_completecast", SoundEvents.EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_TELEPORT_CAST = registerSoundEvent("invoker_teleport_cast", SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    public static SoundEvent INVOKER_FANGS_CAST = registerSoundEvent("invoker_fangs_cast", SoundEvents.EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_BIG_CAST = registerSoundEvent("invoker_big_cast", SoundEvents.EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_SUMMON_CAST = registerSoundEvent("invoker_summon_cast", SoundEvents.EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_SHIELD_BREAK = registerSoundEvent("invoker_shield_break", SoundEvents.SHIELD_BREAK.value());
    public static SoundEvent ILLAGER_BRUTE_AMBIENT = registerSoundEvent("illager_brute_ambient", SoundEvents.VINDICATOR_AMBIENT);
    public static SoundEvent ILLAGER_BRUTE_HURT = registerSoundEvent("illager_brute_hurt", SoundEvents.VINDICATOR_HURT);
    public static SoundEvent ILLAGER_BRUTE_DEATH = registerSoundEvent("illager_brute_death", SoundEvents.VINDICATOR_DEATH);
    public static SoundEvent PROVOKER_AMBIENT = registerSoundEvent("provoker_idle", SoundEvents.EVOKER_AMBIENT);
    public static SoundEvent PROVOKER_HURT = registerSoundEvent("provoker_hurt", SoundEvents.EVOKER_HURT);
    public static SoundEvent PROVOKER_DEATH = registerSoundEvent("provoker_death", SoundEvents.EVOKER_DEATH);
    public static SoundEvent PROVOKER_CELEBRATE = registerSoundEvent("provoker_celebrate", SoundEvents.EVOKER_CELEBRATE);
    public static SoundEvent BASHER_AMBIENT = registerSoundEvent("basher_idle", SoundEvents.VINDICATOR_AMBIENT);
    public static SoundEvent BASHER_HURT = registerSoundEvent("basher_hurt", SoundEvents.VINDICATOR_HURT);
    public static SoundEvent BASHER_DEATH = registerSoundEvent("basher_death",  SoundEvents.VINDICATOR_DEATH);
    public static SoundEvent BASHER_CELEBRATE = registerSoundEvent("basher_celebrate", SoundEvents.VINDICATOR_CELEBRATE);
    public static SoundEvent FIRECALLER_AMBIENT = registerSoundEvent("firecaller_idle", SoundEvents.ILLUSIONER_AMBIENT);
    public static SoundEvent FIRECALLER_HURT = registerSoundEvent("firecaller_hurt", SoundEvents.ILLUSIONER_HURT);
    public static SoundEvent FIRECALLER_DEATH = registerSoundEvent("firecaller_death", SoundEvents.ILLUSIONER_DEATH);
    public static SoundEvent FIRECALLER_CAST = registerSoundEvent("firecaller_cast", SoundEvents.ILLUSIONER_CAST_SPELL);
    public static SoundEvent SORCERER_HURT = registerSoundEvent("sorcerer_hurt", SoundEvents.EVOKER_HURT);
    public static SoundEvent SORCERER_DEATH = registerSoundEvent("sorcerer_death", SoundEvents.EVOKER_DEATH);
    public static SoundEvent SORCERER_AMBIENT = registerSoundEvent("so2rcerer_idle", SoundEvents.EVOKER_AMBIENT);
    public static SoundEvent SORCERER_CELEBRATE = registerSoundEvent("sorcerer_celebrate", SoundEvents.EVOKER_CELEBRATE);

    private static SoundEvent registerSoundEvent(String name, SoundEvent soundEvent) {
        Identifier id = Identifier.fromNamespaceAndPath(IllagerExpansion.MOD_ID, name);
        var event = Registry.register(BuiltInRegistries.SOUND_EVENT, id, new SoundEvent(id, Optional.empty()));
        PolymerSoundEvent.registerOverlay(event, soundEvent);
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.SOUND_EVENT, event);
        return event;
    }
    public static void registerSounds() {
    }
}
