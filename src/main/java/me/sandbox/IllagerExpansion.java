package me.sandbox;

import me.sandbox.block.BlockRegistry;
import me.sandbox.entity.EntityRegistry;
import me.sandbox.item.ItemRegistry;
import me.sandbox.item.potion.PotionRegistry;
import me.sandbox.poly.PolymerModels;
import me.sandbox.sounds.SoundRegistry;
import me.sandbox.world.ProcessorRegistry;
import me.sandbox.world.features.StructureRegistry;
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
