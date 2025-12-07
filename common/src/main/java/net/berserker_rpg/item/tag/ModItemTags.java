package net.berserker_rpg.item.tag;

import net.berserker_rpg.BerserkerClassMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModItemTags {
    public static final TagKey<Item> BERSERKER_AXES = register("berserker_axes");

    private static TagKey<Item> register(String id) {
        return TagKey.of(RegistryKeys.ITEM, BerserkerClassMod.id(id));
    }

}
