package com.berserker.fabric;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BerserkerClassMod.init();
        // The `berserker_rpg:generic` group is created inside registerItems() (see BerserkerGroup), so that
        // both loaders build it the same way and it exists before the weapon/armor registrations fill it.
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
}
