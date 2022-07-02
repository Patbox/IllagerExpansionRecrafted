package me.sandbox.item;

import eu.pb4.polymer.api.item.PolymerItemGroup;
import me.sandbox.IllagerExpansion;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final ItemGroup SandBoxMisc = PolymerItemGroup.create(new Identifier(IllagerExpansion.MOD_ID, "sandboxmisc"),
            Text.translatable("itemGroup.illagerexp.sandboxmisc"),
            () -> new ItemStack(ItemRegistry.HORN_OF_SIGHT));

    public static final ItemGroup SandBoxMobs = PolymerItemGroup.create(new Identifier(IllagerExpansion.MOD_ID, "sandboxmobs"),
            Text.translatable("itemGroup.illagerexp.sandboxmobs"),
            () -> new ItemStack(ItemRegistry.PROVOKER_SPAWN_EGG));
}
