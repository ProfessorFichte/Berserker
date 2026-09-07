package com.berserker.fabric;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BerserkerClassMod.init();
        registerItemGroup();
        BerserkerClassMod.registerItems();
        BerserkerClassMod.registerSounds();
        BerserkerClassMod.registerEffects();

        Armors.forEachGroupOverride((pieces, key) -> {
            ItemGroupEvents.modifyEntriesEvent(BerserkerGroup.BERSERKER_KEY).register(content -> {
                content.getDisplayStacks().removeIf(stack -> pieces.stream().anyMatch(p -> stack.isOf((ArmorItem) p)));
                content.getSearchTabStacks().removeIf(stack -> pieces.stream().anyMatch(p -> stack.isOf((ArmorItem) p)));
            });
            ItemGroupEvents.modifyEntriesEvent(key).register(content -> {
                for (var piece : pieces) {
                    content.add((ArmorItem) piece);
                }
            });
        });

        WeaponsRegister.forEachGroupOverride((item, key) -> {
            ItemGroupEvents.modifyEntriesEvent(BerserkerGroup.BERSERKER_KEY).register(content -> {
                content.getDisplayStacks().removeIf(stack -> stack.isOf(item));
                content.getSearchTabStacks().removeIf(stack -> stack.isOf(item));
            });
            ItemGroupEvents.modifyEntriesEvent(key).register(content -> content.add(item));
        });
    }

    private void registerItemGroup() {
        BerserkerGroup.BERSERKER = FabricItemGroup.builder()
                .icon(BerserkerGroup::icon)
                .displayName(BerserkerGroup.displayName())
                .build();
        Registry.register(Registries.ITEM_GROUP, BerserkerGroup.BERSERKER_KEY, BerserkerGroup.BERSERKER);
    }
}
