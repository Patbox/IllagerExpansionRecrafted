package me.sandbox.poly;

import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.polymer.api.resourcepack.PolymerModelData;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;

public class PolymerModels {
    public static final Map<Item, PolymerModelData> MODELS = new IdentityHashMap<>();

    public static void setup() {
        PolymerRPUtils.addAssetSource("illagerexp");

    }

    public static void requestModel(Identifier identifier, Item item) {
        MODELS.put(item, PolymerRPUtils.requestModel(((PolymerItem) item).getPolymerItem(item.getDefaultStack(), null), identifier));
    }
}
