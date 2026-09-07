package com.berserker.neoforge;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;

@Mod(BerserkerClassMod.MOD_ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        BerserkerClassMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, NeoForgeMod::onBuildCreativeTabContents);
    }
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.ITEM_GROUP, reg -> {
            BerserkerGroup.BERSERKER = ItemGroup.builder()
                    .icon(BerserkerGroup::icon)
                    .displayName(BerserkerGroup.displayName())
                    .build();
            Registry.register(Registries.ITEM_GROUP, BerserkerGroup.BERSERKER_KEY, BerserkerGroup.BERSERKER);
        });
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            BerserkerClassMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            BerserkerClassMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            BerserkerClassMod.registerEffects();
        });
    }

    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        Armors.forEachGroupOverride((pieces, key) -> {
            if (event.getTabKey().equals(BerserkerGroup.BERSERKER_KEY)) {
                for (var piece : pieces) {
                    removeFromTab(event, (ArmorItem) piece);
                }
            } else if (event.getTabKey().equals(key)) {
                for (var piece : pieces) {
                    event.add((ArmorItem) piece);
                }
            }
        });
        WeaponsRegister.forEachGroupOverride((item, key) -> {
            if (event.getTabKey().equals(BerserkerGroup.BERSERKER_KEY)) {
                removeFromTab(event, item);
            } else if (event.getTabKey().equals(key)) {
                event.add(item);
            }
        });
    }

    private static void removeFromTab(BuildCreativeModeTabContentsEvent event, Item item) {
        var toRemove = new ArrayList<ItemStack>();
        for (var stack : event.getParentEntries()) {
            if (stack.isOf(item)) {
                toRemove.add(stack);
            }
        }
        for (var stack : event.getSearchEntries()) {
            if (stack.isOf(item)) {
                toRemove.add(stack);
            }
        }
        for (var stack : toRemove) {
            event.remove(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

}
