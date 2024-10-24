package eu.pb4.illagerexpansion.poly;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;


import static eu.pb4.illagerexpansion.IllagerExpansion.id;

public class PolymerModels {

    public static void setup() {
        PolymerResourcePackUtils.addModAssets("illagerexp");
        PolymerResourcePackUtils.addBridgedModelsFolder(id("pbentity"));
    }
}
