package net.berserker_rpg.item;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.armor.Armors;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BerserkerGroup {
    public static Identifier ID = new Identifier(BerserkerClassMod.MOD_ID, "generic");
    public static RegistryKey<ItemGroup> BERSERKER_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static ItemGroup BERSERKER;

    public static ItemStack icon() {
        return new ItemStack(Armors.wildlingArmorSet.armorSet().head.asItem());
    }

    public static Text displayName() {
        return Text.translatable("itemGroup." + BerserkerClassMod.MOD_ID + ".general");
    }

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, BERSERKER_KEY, createItemGroup());
    }

    public static ItemGroup createItemGroup() {
        BerserkerClassMod.LOGGER.info("Registering Item Groups for " + BerserkerClassMod.MOD_ID);
        BERSERKER = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(BerserkerGroup::icon)
                .displayName(displayName())
                .build();
        return BERSERKER;
    }
}
