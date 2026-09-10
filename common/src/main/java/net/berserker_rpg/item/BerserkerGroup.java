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

    /// The group is built here rather than per platform: `ItemGroup.builder()` is a Fabric injection and
    /// `FabricItemGroup` is Fabric-only, but the vanilla `ItemGroup.Builder` constructor works on both
    /// loaders. This is the Fabric path; Forge registers {@link #createItemGroup()} from its own
    /// `RegisterEvent` window for `creative_mode_tab`.
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, BERSERKER_KEY, createItemGroup());
    }

    /// Builds the group instance without registering it. Creation only, so Forge can register it through the
    /// helper it is handed in the ITEM_GROUP (`creative_mode_tab`) window - which is event 65, long after ITEM
    /// (event 7), so this must NOT ride along in the item pass.
    public static ItemGroup createItemGroup() {
        BerserkerClassMod.LOGGER.info("Registering Item Groups for " + BerserkerClassMod.MOD_ID);
        BERSERKER = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(BerserkerGroup::icon)
                .displayName(displayName())
                .build();
        return BERSERKER;
    }
}
