package eu.pb4.illagerexpansion;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.gson.JsonParser;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import java.nio.file.Files;


public class EarlyRiser implements Runnable {

    private boolean isFriendsAndFoesIllusionerEnabled() {
        if (!FabricLoader.getInstance().isModLoaded("friendsandfoes")) {
            return false;
        }

        try {
            var file = JsonParser.parseString(Files.readString(FabricLoader.getInstance().getConfigDir().resolve("friendsandfoes.json")));
            return file.getAsJsonObject().getAsJsonPrimitive("enableIllusioner").getAsBoolean()
                    && file.getAsJsonObject().getAsJsonPrimitive("enableIllusionerInRaids").getAsBoolean();
        } catch (Throwable e) {
            // File doesn't exist or failed to parse, assume it's defaulted, which sets it to true!
            return true;
        }
    }

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        if (isFriendsAndFoesIllusionerEnabled()) {
            String Raid = remapper.mapClassName("intermediary", "net.minecraft.class_3765$class_3766");
            String EntityType = 'L' + remapper.mapClassName("intermediary", "net.minecraft.class_1299") + ';';
            ClassTinkerers
                    .enumBuilder(Raid, EntityType, int[].class)
                    .addEnum("IE_BASHER", () -> new Object[]{EntityRegistry.BASHER, new int[]{1, 1, 2, 1, 2, 2, 3, 3}})
                    .addEnum("IE_PROVOKER", () -> new Object[]{EntityRegistry.PROVOKER, new int[]{0, 1, 1, 0, 1, 1, 2, 2}})
                    .addEnum("IE_SORCERER", () -> new Object[]{EntityRegistry.SORCERER, new int[]{0, 0, 0, 0, 0, 1, 1, 1}})
                    .addEnum("IE_ARCHIVIST", () -> new Object[]{EntityRegistry.ARCHIVIST, new int[]{0, 1, 0, 1, 1, 1, 2, 3}})
                    .addEnum("IE_MARAUDER", () -> new Object[]{EntityRegistry.MARAUDER, new int[]{0, 1, 1, 1, 2, 2, 3, 3}})
                    .addEnum("IE_INQUISITOR", () -> new Object[]{EntityRegistry.INQUISITOR, new int[]{0, 0, 0, 0, 1, 0, 1, 1}})
                    .addEnum("IE_ALCHEMIST", () -> new Object[]{EntityRegistry.ALCHEMIST, new int[]{0, 0, 0, 1, 2, 1, 2, 2}})
                    .build();
        } else {
            String Raid = remapper.mapClassName("intermediary", "net.minecraft.class_3765$class_3766");
            String EntityType = 'L' + remapper.mapClassName("intermediary", "net.minecraft.class_1299") + ';';
            ClassTinkerers
                    .enumBuilder(Raid, EntityType, int[].class)
                    .addEnum("IE_BASHER", () -> new Object[]{EntityRegistry.BASHER, new int[]{1, 1, 2, 1, 2, 2, 3, 3}})
                    .addEnum("IE_PROVOKER", () -> new Object[]{EntityRegistry.PROVOKER, new int[]{0, 1, 1, 0, 1, 1, 2, 2}})
                    .addEnum("IE_SORCERER", () -> new Object[]{EntityRegistry.SORCERER, new int[]{0, 0, 0, 0, 0, 1, 1, 1}})
                    .addEnum("IE_ILLUSIONER", () -> new Object[]{net.minecraft.entity.EntityType.ILLUSIONER, new int[]{0, 0, 0, 0, 0, 1, 0, 1}})
                    .addEnum("IE_ARCHIVIST", () -> new Object[]{EntityRegistry.ARCHIVIST, new int[]{0, 1, 0, 1, 1, 1, 2, 3}})
                    .addEnum("IE_MARAUDER", () -> new Object[]{EntityRegistry.MARAUDER, new int[]{0, 1, 1, 1, 2, 2, 3, 3}})
                    .addEnum("IE_INQUISITOR", () -> new Object[]{EntityRegistry.INQUISITOR, new int[]{0, 0, 0, 0, 1, 0, 1, 1}})
                    .addEnum("IE_ALCHEMIST", () -> new Object[]{EntityRegistry.ALCHEMIST, new int[]{0, 0, 0, 1, 2, 1, 2, 2}})
                    .build();
        }
        final String SpellcastingIllagerEntity = remapper.mapClassName("intermediary", "net.minecraft.class_1617$class_1618");
        ClassTinkerers.enumBuilder(SpellcastingIllagerEntity, int.class, double.class, double.class, double.class)
                .addEnum("IE_ENCHANT",6, 0.8, 0.8, 0.2 )
                .addEnum("IE_CONJURE_FLAMES", 7, 1.8, 0.0, 1.8 )
                .addEnum("IE_CONJURE_TELEPORT", 8, 1.5, 1.5, 0.8)
                .addEnum("IE_NECRORAISE", 9, 0.3, 0.8, 0.05)
                .addEnum("IE_CONJURE_SKULLBOLT",10, 0.5, 0.05, 0.05)
                .addEnum("IE_PROVOKE", 11, 1.0, 0.8, 0.75)
                .build();
    }
}
