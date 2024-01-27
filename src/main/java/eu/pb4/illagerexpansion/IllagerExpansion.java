package eu.pb4.illagerexpansion;

import eu.pb4.illagerexpansion.block.BlockRegistry;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import eu.pb4.illagerexpansion.item.potion.PotionRegistry;
import eu.pb4.illagerexpansion.poly.PolymerModels;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import eu.pb4.illagerexpansion.world.ProcessorRegistry;
import eu.pb4.illagerexpansion.world.features.StructureRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllagerExpansion implements ModInitializer {
    public static final String MOD_ID = "illagerexp";
    public static final Logger LOGGER = LoggerFactory.getLogger("IllagerExpansion");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PolymerModels.setup();

        ItemRegistry.registerModItems();
        BlockRegistry.registerModBlocks();
        SoundRegistry.registerSounds();
        EntityRegistry.registerEntities();
        StructureRegistry.registerStructureFeatures();
        ProcessorRegistry.registerProcessors();
        PotionRegistry.registerPotions();

        LOGGER.info("Why are there so many illagers?");
    }
}
