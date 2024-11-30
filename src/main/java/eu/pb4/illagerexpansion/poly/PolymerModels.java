package eu.pb4.illagerexpansion.poly;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;


import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class PolymerModels {

    public static void setup() {
        PolymerResourcePackUtils.addModAssets("illagerexp");
        ResourcePackExtras.forDefault().addBridgedModelsFolder(id("pbentity"));
    }
}
