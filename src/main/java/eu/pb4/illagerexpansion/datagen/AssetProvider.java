package eu.pb4.illagerexpansion.datagen;

import com.google.common.hash.HashCode;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Util;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

class AssetProvider implements DataProvider {
    private final PackOutput output;

    public AssetProvider(FabricPackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            runWriters(assetWriter);
        }, Util.backgroundExecutor());
    }

    public static void runWriters(BiConsumer<String,byte[]> assetWriter) {

    }

    @Override
    public String getName() {
        return "polyfactory:assets";
    }
}
