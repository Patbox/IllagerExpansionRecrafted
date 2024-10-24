package eu.pb4.illagerexpansion.sounds;

import eu.pb4.illagerexpansion.IllagerExpansion;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SoundRegistry {
    public static SoundEvent SURRENDERED_AMBIENT = registerSoundEvent("surrendered_ambient", SoundEvents.ENTITY_STRAY_AMBIENT);
    public static SoundEvent SURRENDERED_HURT = registerSoundEvent("surrendered_hurt", SoundEvents.ENTITY_STRAY_HURT);
    public static SoundEvent SURRENDERED_CHARGE = registerSoundEvent("surrendered_charge", SoundEvents.ENTITY_GHAST_WARN);
    public static SoundEvent SURRENDERED_DEATH = registerSoundEvent("surrendered_death", SoundEvents.ENTITY_STRAY_DEATH);
    public static SoundEvent ARCHIVIST_AMBIENT = registerSoundEvent("archivist_ambient", SoundEvents.ENTITY_ILLUSIONER_AMBIENT);
    public static SoundEvent ARCHIVIST_HURT = registerSoundEvent("archivist_hurt", SoundEvents.ENTITY_ILLUSIONER_HURT);
    public static SoundEvent ARCHIVIST_DEATH = registerSoundEvent("archivist_death", SoundEvents.ENTITY_ILLUSIONER_DEATH);
    public static SoundEvent SORCERER_CAST = registerSoundEvent("sorcerer_cast", SoundEvents.ENTITY_EVOKER_CAST_SPELL);
    public static SoundEvent SORCERER_COMPLETE_CAST = registerSoundEvent("sorcerer_complete_cast", SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE);
    public static SoundEvent HORN_OF_SIGHT = registerSoundEvent("horn_of_sight", SoundEvents.INTENTIONALLY_EMPTY);
    public static SoundEvent INVOKER_FANGS = registerSoundEvent("invoker_fangs", SoundEvents.ENTITY_EVOKER_FANGS_ATTACK);
    public static SoundEvent INVOKER_HURT = registerSoundEvent("invoker_hurt", SoundEvents.ENTITY_EVOKER_HURT);
    public static SoundEvent INVOKER_DEATH = registerSoundEvent("invoker_death", SoundEvents.ENTITY_EVOKER_DEATH);
    public static SoundEvent INVOKER_AMBIENT = registerSoundEvent("invoker_ambient", SoundEvents.ENTITY_EVOKER_AMBIENT);
    public static SoundEvent INVOKER_COMPLETE_CAST = registerSoundEvent("invoker_completecast", SoundEvents.ENTITY_EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_TELEPORT_CAST = registerSoundEvent("invoker_teleport_cast", SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR);
    public static SoundEvent INVOKER_FANGS_CAST = registerSoundEvent("invoker_fangs_cast", SoundEvents.ENTITY_EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_BIG_CAST = registerSoundEvent("invoker_big_cast", SoundEvents.ENTITY_EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_SUMMON_CAST = registerSoundEvent("invoker_summon_cast", SoundEvents.ENTITY_EVOKER_CAST_SPELL);
    public static SoundEvent INVOKER_SHIELD_BREAK = registerSoundEvent("invoker_shield_break", SoundEvents.ITEM_SHIELD_BREAK);
    public static SoundEvent ILLAGER_BRUTE_AMBIENT = registerSoundEvent("illager_brute_ambient", SoundEvents.ENTITY_VINDICATOR_AMBIENT);
    public static SoundEvent ILLAGER_BRUTE_HURT = registerSoundEvent("illager_brute_hurt", SoundEvents.ENTITY_VINDICATOR_HURT);
    public static SoundEvent ILLAGER_BRUTE_DEATH = registerSoundEvent("illager_brute_death", SoundEvents.ENTITY_VINDICATOR_DEATH);
    public static SoundEvent PROVOKER_AMBIENT = registerSoundEvent("provoker_idle", SoundEvents.ENTITY_EVOKER_AMBIENT);
    public static SoundEvent PROVOKER_HURT = registerSoundEvent("provoker_hurt", SoundEvents.ENTITY_EVOKER_HURT);
    public static SoundEvent PROVOKER_DEATH = registerSoundEvent("provoker_death", SoundEvents.ENTITY_EVOKER_DEATH);
    public static SoundEvent PROVOKER_CELEBRATE = registerSoundEvent("provoker_celebrate", SoundEvents.ENTITY_EVOKER_CELEBRATE);
    public static SoundEvent BASHER_AMBIENT = registerSoundEvent("basher_idle", SoundEvents.ENTITY_VINDICATOR_AMBIENT);
    public static SoundEvent BASHER_HURT = registerSoundEvent("basher_hurt", SoundEvents.ENTITY_VINDICATOR_HURT);
    public static SoundEvent BASHER_DEATH = registerSoundEvent("basher_death",  SoundEvents.ENTITY_VINDICATOR_DEATH);
    public static SoundEvent BASHER_CELEBRATE = registerSoundEvent("basher_celebrate", SoundEvents.ENTITY_VINDICATOR_CELEBRATE);
    public static SoundEvent FIRECALLER_AMBIENT = registerSoundEvent("firecaller_idle", SoundEvents.ENTITY_ILLUSIONER_AMBIENT);
    public static SoundEvent FIRECALLER_HURT = registerSoundEvent("firecaller_hurt", SoundEvents.ENTITY_ILLUSIONER_HURT);
    public static SoundEvent FIRECALLER_DEATH = registerSoundEvent("firecaller_death", SoundEvents.ENTITY_ILLUSIONER_DEATH);
    public static SoundEvent FIRECALLER_CAST = registerSoundEvent("firecaller_cast", SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL);
    public static SoundEvent SORCERER_HURT = registerSoundEvent("sorcerer_hurt", SoundEvents.ENTITY_EVOKER_HURT);
    public static SoundEvent SORCERER_DEATH = registerSoundEvent("sorcerer_death", SoundEvents.ENTITY_EVOKER_DEATH);
    public static SoundEvent SORCERER_AMBIENT = registerSoundEvent("sorcerer_idle", SoundEvents.ENTITY_EVOKER_AMBIENT);
    public static SoundEvent SORCERER_CELEBRATE = registerSoundEvent("sorcerer_celebrate", SoundEvents.ENTITY_EVOKER_CELEBRATE);

    private static SoundEvent registerSoundEvent(String name, SoundEvent soundEvent) {
        Identifier id = Identifier.of(IllagerExpansion.MOD_ID, name);
        return soundEvent;//PolymerSoundEvent.of(id, soundEvent);
    }
    public static void registerSounds() {
    }
}
